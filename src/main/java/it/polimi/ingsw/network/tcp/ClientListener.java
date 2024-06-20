package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonParseException;
import it.polimi.ingsw.controllers.PlayerControllerTCP;
import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongNameException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.AvailableMatchesMessage;
import it.polimi.ingsw.network.messages.responses.ResponseMessage;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.MessageJsonParser;
import it.polimi.ingsw.utils.Pair;

/*
 * actual connection procedure: - socket accepted - socket asks for available matches, giving its
 * name to server - when received, it communicates which match it wants to join - only then a
 * PlayerController will be created, with said match - from there the constructor is done, a player
 * has joined and it just has to listen
 */

/**
 * Every time a socket gets accepted by the TCP server, a new ClientListener will be created with
 * it, and it will: - Acquire the client's username - Make the client (which is still not a
 * {@link Player}) choose/create a {@link Match} to join - Create its {@link PlayerControllerTCP},
 * which will also make him join such {@link Match} - Listen for any message received and, execute
 * the corresponding action
 * <p>
 * Note that this will just require the action to be executed, but its {@link PlayerControllerTCP}
 * that actually calls the {@link Player} methods
 */
public class ClientListener extends Thread {
    private Socket socket;
    private PlayerControllerTCP playerController;
    private MessageJsonParser parser;
    private IOHandler io;
    private Server server;
    private Match match;
    private Map<Integer, Objective> objectives;
    private Map<Integer, PlayableCard> playableCards;

    /**
     * Class constructor. Needs to have a reference to the server instance since it needs to handle
     * the match assignment
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

            this.objectives = CardsManager.getInstance().getObjectives();
            Map<Integer, ResourceCard> resources = CardsManager.getInstance().getResourceCards();
            Map<Integer, GoldCard> golds = CardsManager.getInstance().getGoldCards();

            this.playableCards = new HashMap<>();
            resources.forEach((id, card) -> this.playableCards.put(id, card));
            golds.forEach((id, card) -> this.playableCards.put(id, card));

        } catch (IOException e) {
            this.sendError("Failed to create Listener thread", e);
        }
    }


    /**
     * Sends error message with custom text
     *
     * @param prompt the text to be shown
     * @param exception the exception type
     */
    private void sendError(String prompt, Exception exception) {
        try {
            this.io.writeMsg(new ErrorMessage(prompt, exception.getClass().getName()));
        } catch (Exception ignored) {
        }
    }


    /**
     * Loops until a player controller is created
     *
     * @throws IOException if there was an I/O error
     */
    private void setPlayerController() {
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
                            availableMatches = new AvailableMatchesMessage(
                                    this.server.getJoinableMatchesMap());
                            this.io.writeMsg(availableMatches);
                            break;

                        case CreateMatchMessage createMatchMessage:
                            username = createMatchMessage.getUsername();
                            this.server.createMatch(createMatchMessage.getMatchName(),
                                    createMatchMessage.getMaxPlayers());
                            match = this.server.getMatch(createMatchMessage.getMatchName());

                            this.createPlayerController(username, match);
                            shouldLoop = false;
                            break;

                        case JoinMatchMessage joinMatchMessage:
                            username = joinMatchMessage.getUsername();
                            match = this.server.getMatch(joinMatchMessage.getMatchName());

                            this.createPlayerController(username, match);
                            shouldLoop = false;
                            break;

                        default:
                            break;
                    }
                }
            } catch (JsonParseException | ClassNotFoundException e) {
                // message is not correctly formatted, ignore
            } catch (ChosenMatchException | WrongStateException | AlreadyUsedUsernameException
                     | IllegalArgumentException | WrongNameException e) {
                this.sendError(e.getMessage(), e);
            } catch (IOException e) {
                this.close(match);
            }
        }
        this.match = match;
    }


    /**
     * Tries to actually create the player controller with the acquired information
     *
     * @param username The chosen username
     * @param match The match to join
     * @throws AlreadyUsedUsernameException If the match already contains the chosen username
     * @throws WrongStateException If the match currently does not accept new players
     * @throws ChosenMatchException If the match does not exist or is not valid
     */
    private void createPlayerController(String username, Match match)
            throws AlreadyUsedUsernameException, IllegalArgumentException, WrongStateException,
            ChosenMatchException, WrongNameException {
        this.playerController = new PlayerControllerTCP(username, match, this.io);
        this.playerController.sendJoined();
    }

    /**
     * This parses the message received from socket's input stream and executes the request such
     * message carried. If the message is not one of the expected types, it will just be ignored
     *
     * @see ActionMessage
     */
    private void executeRequest(String msg) {
        try {

            ActionMessage message = (ActionMessage) parser.toMessage(msg);
            if (msg != null) {
                switch (message) {
                    case ChooseSecretObjectiveMessage actionMsg:
                        this.playerController.chooseSecretObjective(
                                this.objectives.get(actionMsg.getObjectiveID()));
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
                        this.playerController.sendPrivateText(actionMsg.getRecipient(),
                                actionMsg.getText());
                        break;
                    case PlayCardMessage actionMsg:
                        Pair<Integer, Integer> coords =
                                new Pair<>(actionMsg.getX(), actionMsg.getY());
                        PlayableCard card = this.playableCards.get(actionMsg.getCardID());
                        this.playerController.playCard(coords, card, actionMsg.getSide());
                        break;
                    default:
                        break;
                }
            }
        } catch (JsonParseException e) {
            // Nothing to do here: it was either a ping or a wrongly formatted message
        }

    }

    /**
     * Main loop. This will just wait for anything to be put on the input stream and then call
     * {@link ClientListener#executeRequest(String)}
     */
    public void listen() {
        try {
            while (!this.socket.isClosed() && this.socket.isConnected()) {
                String msg = this.io.readMsg();
                // if msg is null, it means the socket was closed client side. Quit all
                if (msg == null) {
                    throw new IOException("Socket closed");
                }
                this.executeRequest(msg);
            }
        } catch (IOException | ClassNotFoundException e) {
            this.close(match);
        }
    }

    /**
     * This will close socket and input/output handlers, if not null
     */
    private void close(Match match) {
        try {
            match.removePlayer(this.playerController.getPlayer());
            if (this.socket != null && !this.socket.isClosed()) {
                this.io.close();
                this.socket.close();
            }
        } catch (IOException | NullPointerException e) {
        }
    }

    /**
     * Since the class extends {@link Thread} it needs to implement {@link Thread#run()}.
     * Specifically, this will just run {@link ClientListener#listen()}
     */
    @Override
    public void run() {
        this.setPlayerController();
        this.listen();
    }
}
