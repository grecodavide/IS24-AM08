package it.polimi.ingsw.network.tcp;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.controllers.PlayerControllerTCP;
import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.AvailableMatchesMessage;
import it.polimi.ingsw.network.messages.responses.ResponseMessage;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.MessageJsonParser;
import it.polimi.ingsw.utils.Pair;

/*
 * actual connection procedure:
 * - socket accepted
 * - socket asks for available matches, giving its name to server
 * - when received, it communicates which match it wants to join
 * - only then a playercontroller will be created, with said match
 * - from there the constructor is done, a player has joined and it just has to listen
 */

/**
 * Every time a socket gets accepted by the TCP server, a new ClientListener
 * will be created with it, and it will:
 * - Acquire the client's username
 * - Make the client (which is still not a {@link Player}) choose/create a
 * {@link Match} to join
 * - Create its {@link PlayerControllerTCP}, which will also make him join such
 * {@link Match}
 * - Listen for any message received and, execute the corresponding action
 * <p>
 * Note that this will just require the action to be executed, but its
 * {@link PlayerControllerTCP}
 * that actually calls the {@link Player} methods
 */
public class ClientListener extends Thread {
    private Socket socket;
    private PlayerControllerTCP playerController;
    private MessageJsonParser parser;
    private IOHandler io;
    private Server server;

    /**
     * Class constructor. Needs to have a reference to the server instance since it
     * needs to
     * handle the match assignment
     *
     * @param socket the socket that required a connection
     * @param server the instance of {@link Server} that's running
     */
    public ClientListener(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.io = new IOHandler(this.socket);
            this.server = server;
            this.parser = new MessageJsonParser();

            this.clientInteraction(); // init player controller
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to create Listener thread");
            // e.printStackTrace();
        }
    }

    /**
     * Used to acquire the {@link Player}'s username and the {@link Match} he wants
     * to join.
     * If this throws an exception, it means something went wrong with the remote
     * communication,
     * and so the ListenerThread should not be created. All other cases are handled
     * internally
     *
     * @throws IOException            if the socket's input stream cannot be read
     * @throws ClassNotFoundException if the class of the received object (from the
     *                                socket's input stream) could not be found
     * @throws EOFException           if the stream gets shut down while it was
     *                                still waiting for something
     */
    private void clientInteraction() throws IOException, ClassNotFoundException, EOFException {
        ActionMessage msg;
        String username = null;
        Match match = null;
        ResponseMessage availableMatches;
        boolean shouldLoop = true;

        while (shouldLoop) {
            try {
                msg = (ActionMessage) this.parser.toMessage(this.io.readMsg());
                if (msg == null) {
                    shouldLoop = false;
                } else {
                    switch (msg) {
                        case GetAvailableMatchesMessage getAvailableMatchesMessage:
                            username = getAvailableMatchesMessage.getUsername();

                            availableMatches = new AvailableMatchesMessage(this.server.getJoinableMatchesMap());
                            this.io.writeMsg(availableMatches);
                            break;

                        case CreateMatchMessage createMatchMessage:
                            username = createMatchMessage.getUsername();
                            this.server.createMatch(createMatchMessage.getMatchName(), createMatchMessage.getMaxPlayers());
                            match = this.server.getMatch(createMatchMessage.getMatchName());

                            shouldLoop = false;
                            break;
                        case JoinMatchMessage joinMatchMessage:
                            match = this.server.getMatch(joinMatchMessage.getMatchName());

                            shouldLoop = false;
                            break;
                        default:
                            break;
                    }
                }
            } catch (JsonParseException e) {
                // message is not correctly formatted, ignore
            } catch (ChosenMatchException e) {
                ErrorMessage error = new ErrorMessage(e.getMessage(), e.getClass().getName());
                this.io.writeMsg(error);
            }
        }

        // if we reached this point, everything went smooth and the client asked to
        // either join or create a match
        this.createPlayerController(username, match);
    }

    /**
     * Once everything went smoothly, we try to actually create the
     * {@link PlayerControllerTCP}. If this
     * throws {@link AlreadyUsedUsernameException} or {@link WrongStateException}
     * the acquisition procedure is restarted
     * after sending an {@link ErrorMessage} back to the client
     *
     * @throws IOException            if the socket's input stream cannot be read
     * @throws ClassNotFoundException if the class of the received object (from the
     * @throws EOFException           if the stream gets shut down while it was
     *                                still waiting for something
     */
    private void createPlayerController(String username, Match match) throws IOException, ClassNotFoundException, EOFException {
        try {
            this.playerController = new PlayerControllerTCP(username, match, this.io);
        } catch (AlreadyUsedUsernameException | WrongStateException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage(), e.getClass().getName());
            this.io.writeMsg(error);
            this.clientInteraction();
        }

    }

    /**
     * This parses the message received from socket's input stream and executes the
     * request such message carried.
     * If the message is not one of the expected types, it will just be ignored
     *
     * @see ActionMessage
     */
    private void executeRequest(String msg) {
        ActionMessage message = (ActionMessage) parser.toMessage(msg);
        if (msg != null) {
            switch (message) {
                case ChooseSecretObjectiveMessage actionMsg:
                    this.playerController.chooseSecretObjective(Server.getObjective(actionMsg.getObjectiveID()));
                    break;
                case ChooseInitialCardSideMessage actionMsg:
                    this.playerController.chooseInitialCardSide(actionMsg.getSide());
                    break;
                case DrawCardMessage actionMsg:
                    this.playerController.drawCard(actionMsg.getSource());
                    break;
                case DrawInitialCardMessage actionMsg:
                    this.playerController.drawInitialCard();
                    break;
                case DrawSecretObjectivesMessage actionMsg:
                    this.playerController.drawSecretObjectives();
                    break;
                case SendBroadcastTextMessage actionMsg:
                    this.playerController.sendBroadcastText(actionMsg.getText());
                    break;
                case SendPrivateTextMessage actionMsg:
                    this.playerController.sendPrivateText(actionMsg.getRecpient(), actionMsg.getText());
                    break;
                case PlayCardMessage actionMsg:
                    Pair<Integer, Integer> coords = new Pair<Integer, Integer>(actionMsg.getX(), actionMsg.getY());
                    PlayableCard card = Server.getPlayableCard(actionMsg.getCardID());
                    this.playerController.playCard(coords, card, actionMsg.getSide());
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * Main loop. This will just wait for anything to be put on the input stream and
     * then call {@link ClientListener#executeRequest(String)}
     */
    public void listen() {
        try {
            while (this.socket.isConnected()) {
                String msg = this.io.readMsg();
                this.executeRequest(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            this.close();
        }
    }

    /**
     * This will close socket and input/output handlers, if not null
     */
    private void close() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            this.io.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Since the class extends {@link Thread} it needs to implement
     * {@link Thread#run()}. Specifically, this will just run
     * {@link ClientListener#listen()}
     */
    @Override
    public void run() {
        this.listen();
    }
}
