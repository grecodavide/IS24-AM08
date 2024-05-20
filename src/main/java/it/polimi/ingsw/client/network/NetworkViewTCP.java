package it.polimi.ingsw.client.network;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

public class NetworkViewTCP extends NetworkView {
    public NetworkViewTCP(GraphicalView graphicalView, String ipAddress, int port) {
        super(graphicalView, ipAddress, port);
    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveLobbyInfo'");
    }

    @Override
    public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
            Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
            Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'matchStarted'");
    }

    @Override
    public void receiveAvailableMatches(List<AvailableMatch> availableMatchs) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'receiveAvailableMatches'");
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveInitialCard'");
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveSecretObjectives'");
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneDrewInitialCard'");
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneSetInitialSide'");
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneDrewSecretObjective'");
    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneChoseSecretObjective'");
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points,
            Map<Symbol, Integer> availableResources) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someonePlayedCard'");
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
            Symbol replacementCardReign) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneDrewCard'");
    }

    @Override
    public void someoneJoined(String someoneUsername) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneJoined'");
    }

    @Override
    public void someoneQuit(String someoneUsername) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneQuit'");
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'matchFinished'");
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneSentBroadcastText'");
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneSentPrivateText'");
    }

    @Override
    public void getAvailableMatches() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAvailableMatches'");
    }

    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        // TODO
    }

    @Override
    public void joinMatch(String matchName) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'joinMatch'");
    }

    @Override
    public void drawInitialCard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawInitialCard'");
    }

    @Override
    public void chooseInitialCardSide(Side side) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chooseInitialCardSide'");
    }

    @Override
    public void drawSecretObjectives() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawSecretObjectives'");
    }

    @Override
    public void chooseSecretObjective(Objective objective) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'chooseSecretObjective'");
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'playCard'");
    }

    @Override
    public void drawCard(DrawSource source) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'drawCard'");
    }

}
