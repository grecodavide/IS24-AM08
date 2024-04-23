package it.polimi.ingsw.network.tcp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import it.polimi.ingsw.controllers.PlayerControllerTCP;
import it.polimi.ingsw.gamemodel.GameDeck;
import it.polimi.ingsw.gamemodel.GoldCard;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.ResourceCard;
import it.polimi.ingsw.network.messages.actions.ActionMessage;
import it.polimi.ingsw.network.messages.actions.ChooseSecretObjectiveMessage;
import it.polimi.ingsw.utils.MessageJsonParser;

/**
 * ListenerThread
 */
public class ListenerThread implements Runnable {
  private Socket socket;
  private PlayerControllerTCP playerController;
  private ObjectInputStream inputStream;
  private ObjectOutputStream outputStream;
  private MessageJsonParser parser;

  // TODO: actually implement, those are just placeholders
  public ListenerThread(Socket socket) {
    try {
      GameDeck<InitialCard> initial = new GameDeck<InitialCard>();
      GameDeck<ResourceCard> resource = new GameDeck<ResourceCard>();
      GameDeck<GoldCard> gold = new GameDeck<GoldCard>();
      GameDeck<Objective> objective = new GameDeck<Objective>();
      Match match = new Match(4, initial, resource, gold, objective);
      String username = "PLACEHOLDER";

      this.socket = socket;
      this.playerController = new PlayerControllerTCP(username, match, socket);
      this.inputStream = new ObjectInputStream(this.socket.getInputStream());
      this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
      this.parser = new MessageJsonParser();
    } catch (Exception e) {
      System.out.println("Failed to create Listener thread");
    }
  }

  private void executeRequest(String msg) {
    try {
      ActionMessage message = (ActionMessage) parser.toMessage(msg);
      switch (message) {
        case ChooseSecretObjectiveMessage actionMsg:
          System.out.println(actionMsg);
          break;
        default:
          break;
      }

    } catch (Exception e) {
      System.out.println("The parsing failed. Check format");
      System.out.println("Msg: " + msg);
    }
  }

  @Override
  public void run() {
  }

}
