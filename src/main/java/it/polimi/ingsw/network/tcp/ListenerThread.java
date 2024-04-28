package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.polimi.ingsw.controllers.PlayerControllerTCP;
import it.polimi.ingsw.gamemodel.GameDeck;
import it.polimi.ingsw.gamemodel.GoldCard;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.ResourceCard;
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
public class ListenerThread implements Runnable {
    private Socket socket;
    private PlayerControllerTCP playerController;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private MessageJsonParser parser;

    public ListenerThread(Socket socket) {
        try {
            // TODO: PLACEHOLDERS, BEWARE
            GameDeck<InitialCard> initial = new GameDeck<InitialCard>();
            GameDeck<ResourceCard> resource = new GameDeck<ResourceCard>();
            GameDeck<GoldCard> gold = new GameDeck<GoldCard>();
            GameDeck<Objective> objective = new GameDeck<Objective>();
            Match match = new Match(4, initial, resource, gold, objective);
            String username = "PLACEHOLDER";
            // END OF PLACEHOLDERS
      
            this.socket = socket;
            this.playerController = new PlayerControllerTCP(username, match, socket);
            this.inputStream = new ObjectInputStream(this.socket.getInputStream());
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
            this.parser = new MessageJsonParser();
        } catch (Exception e) {
            System.out.println("Failed to create Listener thread");
        }
    }

    private Objective getObjective(Integer cardID) {
        return Server.objectives.get(cardID);
    }

    private PlayableCard getPlayableCard(Integer cardID) {
        return Server.playableCards.get(cardID);
    }

    private void executeRequest(String msg) {
        try {
            ActionMessage message = (ActionMessage) parser.toMessage(msg);
            switch (message) {
                case ChooseSecretObjectiveMessage actionMsg:
                    this.playerController.chooseSecretObjective(this.getObjective(actionMsg.getObjectiveID()));
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
                    PlayableCard card = this.getPlayableCard(actionMsg.getCardID());
                    this.playerController.playCard(coords, card, actionMsg.getSide());
                    break;
                default:
                    break;
            }

        } catch (Exception e) {
            System.out.println("The parsing failed. Check format");
            System.out.println("Msg: " + msg);
        }
    }

    private String readMsg() throws IOException, ClassNotFoundException {
        return this.inputStream.readObject().toString();
    }

    private void listen() {
        try {
            while (this.socket.isConnected()) {
                String msg = this.readMsg();
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
            if (this.inputStream != null) {
                this.inputStream.close();
            }
            if (this.outputStream != null) {
                this.outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.listen();
    }

}
