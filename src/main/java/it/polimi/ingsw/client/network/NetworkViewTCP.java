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
import it.polimi.ingsw.utils.Pair;

public class NetworkViewTCP extends NetworkView {
    private IOHandler io;

    public NetworkViewTCP(GraphicalView graphicalView, String address, Integer port) throws IOException {
        super(graphicalView, address, port);
        Socket socket = new Socket(address, port);
        this.io = new IOHandler(socket);
        new Thread(new ClientReceiver(this, socket)).start();
    }

    public void notifyError(Exception exception) {
        this.graphicalView.notifyError(exception);
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
        this.graphicalView.matchStarted(playersUsernamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards, decksTopReign);
    }

    @Override
    public void getAvailableMatches() {
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
}
