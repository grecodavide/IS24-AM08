package it.polimi.ingsw.network.tcp;

import java.net.Socket;

import it.polimi.ingsw.controllers.PlayerControllerTCP;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.network.messages.actions.ActionMessage;
import it.polimi.ingsw.network.messages.actions.ChooseInitialCardSideMessage;
import it.polimi.ingsw.network.messages.actions.ChooseSecretObjectiveMessage;
import it.polimi.ingsw.network.messages.actions.DrawCardMessage;
import it.polimi.ingsw.network.messages.actions.DrawInitialCardMessage;
import it.polimi.ingsw.network.messages.actions.DrawSecretObjectivesMessage;
import it.polimi.ingsw.network.messages.actions.PlayCardMessage;
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

            /*
            actual connection procedure:
            - socket accepted
            - socket asks for available matches, giving its name to server
            - when received, it communicates which match it wants to join
            - only then a playercontroller will be created, with said match
            - from there the constructor is done, a player has joined and now it just has to listen
            */

            // FIXME: PLACEHOLDERS
            String username = this.io.readMsg();
            Match match = this.server.getJoinableMatches().get(0);


            this.playerController = new PlayerControllerTCP(username, match, this.io);
            this.parser = new MessageJsonParser();
        } catch (Exception e) {
            System.out.println("Failed to create Listener thread");
            e.printStackTrace();
        }
    }

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
