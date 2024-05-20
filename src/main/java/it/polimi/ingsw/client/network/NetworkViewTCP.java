package it.polimi.ingsw.client.network;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.tcp.ClientReceiver;
import it.polimi.ingsw.network.tcp.IOHandler;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

public class NetworkViewTCP extends NetworkView {
    private IOHandler io;

    public NetworkViewTCP(GraphicalView graphicalView, String address, Integer port) {
        super(graphicalView);
        try {
            Socket socket = new Socket(address, port);
            this.io = new IOHandler(socket);
            new Thread(new ClientReceiver(this, socket)).start();
        } catch (IOException e) {
            // TODO: if here, there was a connection problem. handle properly
        }
    }

    public String getUsername() {
        return this.username;
    }

    public IOHandler getIO() {
        return this.io;
    }

    private void sendMessage(Message msg) {
        try {
            this.io.writeMsg(msg);
        } catch (IOException e) {
            // TODO: handle IO
        }
    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveLobbyInfo'");
    }

    @Override
    public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
            Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
            Pair<Symbol, Symbol> decksTopReign) {
        this.graphicalInterface.matchStarted(playersUsernamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards,
                decksTopReign);
    }

    @Override
    public void receiveAvailableMatches(List<AvailableMatch> availableMatchs) {
        this.graphicalInterface.receiveAvailableMatches(availableMatchs);
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {
        this.graphicalInterface.giveInitialCard(initialCard);
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        this.graphicalInterface.giveSecretObjectives(secretObjectives);
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        this.graphicalInterface.someoneDrewInitialCard(someoneUsername, card);
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) {
        this.graphicalInterface.someoneSetInitialSide(someoneUsername, side);
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        this.graphicalInterface.someoneDrewSecretObjective(someoneUsername);
    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        this.graphicalInterface.someoneChoseSecretObjective(someoneUsername);
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points,
            Map<Symbol, Integer> availableResources) {
        this.graphicalInterface.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
            Symbol replacementCardReign) {
        this.graphicalInterface.someoneDrewCard(someoneUsername, source, card, replacementCard, replacementCardReign);
    }

    @Override
    public void someoneJoined(String someoneUsername) {
        this.graphicalInterface.someoneJoined(someoneUsername);
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        this.graphicalInterface.someoneQuit(someoneUsername);
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        this.graphicalInterface.matchFinished(ranking);
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        this.graphicalInterface.someoneSentBroadcastText(someoneUsername, text);
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        this.graphicalInterface.someoneSentPrivateText(someoneUsername, text);
    }

    @Override
    public void getAvailableMatches(){
        this.sendMessage(new GetAvailableMatchesMessage(this.username));
    }

    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        this.sendMessage(new CreateMatchMessage(this.username, matchName, maxPlayers));
    }

    @Override
    public void joinMatch(String matchName) {
        this.sendMessage(new JoinMatchMessage(this.username, matchName));
    }

    @Override
    public void drawInitialCard() {
        this.sendMessage(new DrawInitialCardMessage(this.username));
    }

    @Override
    public void chooseInitialCardSide(Side side) {
        this.sendMessage(new ChooseInitialCardSideMessage(this.username, side));
    }

    @Override
    public void drawSecretObjectives() {
        this.sendMessage(new DrawSecretObjectivesMessage(this.username));
    }

    @Override
    public void chooseSecretObjective(Objective objective) {
        this.sendMessage(new ChooseSecretObjectiveMessage(this.username, objective.getID()));
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        this.sendMessage(new PlayCardMessage(this.username, coords, card.getId(), side));
    }

    @Override
    public void drawCard(DrawSource source) {
        this.sendMessage(new DrawCardMessage(this.username, source));
    }

    @Override
    public void showError(String cause, Exception exception) {
        this.graphicalInterface.showError(cause, exception);
    }
}
