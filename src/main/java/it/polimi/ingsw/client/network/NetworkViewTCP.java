package it.polimi.ingsw.client.network;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.tcp.IOHandler;
import it.polimi.ingsw.utils.Pair;

public class NetworkViewTCP extends NetworkView {
    private final String username;
    private IOHandler io;

    public NetworkViewTCP(String username, Socket socket) {
        this.username = username;
        try {
            this.io = new IOHandler(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // GRAPHICAL
    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {

    }

    @Override
    public void matchStarted(Map<Color, String> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
            Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
            Pair<Symbol, Symbol> decksTopReigns) {

    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {

    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {

    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {

    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) {

    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {

    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {

    }

    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> resources) {
        // Example implementation
        graphicalInterface.someonePlayedCard(someoneUsername, coords, card, side, points, resources);
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
            Symbol replacementReign) throws RemoteException {

    }

    @Override
    public void someoneJoined(String someoneUsername) throws RemoteException {

    }

    @Override
    public void someoneQuit(String someoneUsername) throws RemoteException {

    }

    @Override
    public void matchFinished(List<Pair<String, Boolean>> ranking) throws RemoteException {

    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException {

    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException {

    }
    
    // NETWORK
    private void sendMessage(Message msg) {
        try {
            this.io.writeMsg(msg);
        } catch (IOException e) {
            // TODO: error handling
        }
    }

    /**
     * sends a {@link drawinitialcardmessage}
     */
    @Override
    public void drawInitialCard() {
        this.sendMessage(new DrawInitialCardMessage(this.username));
    }
    
    /**
     * Sends a {@link ChooseInitialCardSideMessage}
     *
     * @param side the chosen side
     */
    @Override
    public void chooseInitialCardSide(Side side) {
        this.sendMessage(new ChooseInitialCardSideMessage(this.username, side));
    }
    
    /**
     * Sends a {@link DrawSecretObjectivesMessage}
     */
    @Override
    public void drawSecretObjectives() {
        this.sendMessage(new DrawSecretObjectivesMessage(this.username));
    }
    
    /**
     * Sends a {@link ChooseSecretObjectiveMessage}
     *
     * @param objective the chosen objective
     */
    @Override
    public void chooseSecretObjective(Objective objective) {
        this.sendMessage(new ChooseSecretObjectiveMessage(this.username, objective.getID()));
    }
    
    /**
     * Sends a {@link PlayCardMessage}
     *
     * @param coords the coordinates the card should be played on
     * @param card the chosen card
     * @param side the card's chosen side
     */
    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        this.sendMessage(new PlayCardMessage(this.username, coords, card.getId(), side));
    }
    
    /**
     * Sends a {@link DrawCardMessage} 
     *
     * @param source from where the card should be drawn
     */
    @Override
    public void drawCard(DrawSource source) {
        this.sendMessage(new DrawCardMessage(this.username, source));
    }
}
