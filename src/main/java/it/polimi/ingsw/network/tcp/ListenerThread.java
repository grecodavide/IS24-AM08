package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.net.Socket;

import com.google.gson.JsonParseException;

import it.polimi.ingsw.controllers.PlayerControllerTCP;
import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.network.messages.actions.ActionMessage;
import it.polimi.ingsw.network.messages.actions.ChooseInitialCardSideMessage;
import it.polimi.ingsw.network.messages.actions.ChooseSecretObjectiveMessage;
import it.polimi.ingsw.network.messages.actions.CreateMatchMessage;
import it.polimi.ingsw.network.messages.actions.DrawCardMessage;
import it.polimi.ingsw.network.messages.actions.DrawInitialCardMessage;
import it.polimi.ingsw.network.messages.actions.DrawSecretObjectivesMessage;
import it.polimi.ingsw.network.messages.actions.GetAvailableMatchesMessage;
import it.polimi.ingsw.network.messages.actions.JoinMatchMessage;
import it.polimi.ingsw.network.messages.actions.PlayCardMessage;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.AvailableMatchesMessage;
import it.polimi.ingsw.network.messages.responses.ResponseMessage;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.MessageJsonParser;
import it.polimi.ingsw.utils.Pair;

/**
 * ListenerThread
 */
public class ListenerThread extends Thread {
    private Socket socket;
    private PlayerControllerTCP playerController;
    private MessageJsonParser parser;
    private IOHandler io;
    private Server server;

    public ListenerThread(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.io = new IOHandler(this.socket);
            this.server = server;
            this.parser = new MessageJsonParser();

            this.clientInteraction(); // init player controller
        } catch (Exception e) {
            System.out.println("Failed to create Listener thread");
            e.printStackTrace();
        }
    }

    private void clientInteraction() throws IOException, ClassNotFoundException {
        ActionMessage msg;
        String username = null;
        Match match = null;
        ResponseMessage availableMatches;
        boolean shouldLoop = true;
        while (shouldLoop) {
            try {
                msg = (ActionMessage) this.parser.toMessage(this.io.readMsg());
                switch (msg) {
                    case GetAvailableMatchesMessage getAvailableMatchesMessage:
                        username = getAvailableMatchesMessage.getUsername();

                        // AvailableMatchesMessage------------------------------------------------------------------
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
            } catch (JsonParseException e) {
                // do nothing, ignore
            } catch (ChosenMatchException e) {
                ErrorMessage error = new ErrorMessage(e.getMessage(), e.getClass().getName());
                this.io.writeMsg(error);
            }

        }

        this.createPlayerController(username, match);

    }

    private void createPlayerController(String username, Match match) throws IOException, ClassNotFoundException {
        try {
            this.playerController = new PlayerControllerTCP(username, match, this.io);
        } catch (AlreadyUsedNicknameException | WrongStateException e) {
            ErrorMessage error = new ErrorMessage(e.getMessage(), e.getClass().getName());
            this.io.writeMsg(error);
            this.clientInteraction();
        }

    }

    /*
     * actual connection procedure:
     * - socket accepted
     * - socket asks for available matches, giving its name to server
     * - when received, it communicates which match it wants to join
     * - only then a playercontroller will be created, with said match
     * - from there the constructor is done, a player has joined and now it just has
     * to listen
     */
    // private void createPlayer() throws InvalidPlayerException {
    // ActionMessage msg;
    // String username = null;
    // Match match = null;
    // ResponseMessage availableMatches;
    // ResponseMessage playerJoined;

    // //
    // GetAvailableMatchesMessage---------------------------------------------------------------
    // try {
    // msg = (ActionMessage) this.parser.toMessage(this.io.readMsg());
    // } catch (ClassNotFoundException | IOException e) {
    // throw new InvalidPlayerException("Could not read GetAvailableMatchesMessage
    // request");
    // }
    // switch (msg) {
    // case GetAvailableMatchesMessage getAvailableMessage:
    // username = getAvailableMessage.getUsername();
    // availableMatches = new
    // AvailableMatchesMessage(this.server.getJoinableMatchesMap());
    // //
    // AvailableMatchesMessage------------------------------------------------------------------
    // try {
    // this.io.writeMsg(availableMatches);
    // } catch (IOException e) {
    // throw new InvalidPlayerException("Could not send back available matches");
    // }

    // //
    // JoinMatchMessage-------------------------------------------------------------------------
    // try {
    // msg = (ActionMessage) this.parser.toMessage(this.io.readMsg());
    // } catch (ClassNotFoundException | IOException e) {
    // throw new InvalidPlayerException("Could not read JoinMatchMessage request");
    // }
    // switch (msg) {
    // case JoinMatchMessage joinMessage:
    // match = this.server.getMatch(joinMessage.getMatchName());
    // Integer currentPlayers = match.getPlayers().size();
    // Integer maxPlayers = match.getMaxPlayers();
    // playerJoined = new SomeoneJoinedMessage(username, currentPlayers,
    // maxPlayers);
    // break;
    // default:
    // throw new InvalidPlayerException(
    // "Expected GetAvailableMatchesMessage, got " + msg.getClass().getName());
    // }
    // break;

    // case CreateMatchMessage createMatchMessage:
    // username = createMatchMessage.getUsername();
    // try {
    // this.server.createMatch(createMatchMessage.getMatchName(),
    // createMatchMessage.getMaxPlayers());
    // match = this.server.getMatch(createMatchMessage.getMatchName());
    // } catch (RemoteException | ChosenMatchException e) {
    // throw new InvalidPlayerException("Could not create match");

    // }
    // default:
    // throw new InvalidPlayerException(
    // "Expected GetAvailableMatchesMessage, got " + msg.getClass().getName());
    // }

    // //
    // PlayerJoinedMessage----------------------------------------------------------------------
    // try {
    // this.io.writeMsg(playerJoined);
    // } catch (IOException e) {
    // throw new InvalidPlayerException("Could not send back available matches");
    // }

    // //
    // PlayerController-------------------------------------------------------------------------
    // try {
    // this.playerController = new PlayerControllerTCP(username, match, this.io);
    // } catch (AlreadyUsedNicknameException | WrongStateException e) {
    // throw new InvalidPlayerException(e.getMessage());
    // }
    // }

    private void executeRequest(String msg) {
        try {
            ActionMessage message = (ActionMessage) parser.toMessage(msg);
            System.out.println(message);
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
                case PlayCardMessage actionMsg:
                    Pair<Integer, Integer> coords = new Pair<Integer, Integer>(actionMsg.getX(), actionMsg.getY());
                    PlayableCard card = Server.getPlayableCard(actionMsg.getCardID());
                    this.playerController.playCard(coords, card, actionMsg.getSide());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Parsing failed, here is the message: " + msg);
        }
    }

    private void listen() {
        try {
            while (this.socket.isConnected()) {
                String msg = this.io.readMsg();
                this.executeRequest(msg);
            }
        } catch (Exception e) {
            this.close();
        }
    }

    private void close() {
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            this.io.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() {
        this.listen();
    }

}
