package it.polimi.ingsw.client.network;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.controllers.PlayerControllerRMIInterface;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.server.ServerRMIInterface;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

public class NetworkViewRMI extends NetworkView {
    private final ServerRMIInterface server;
    private PlayerControllerRMIInterface controller;

    public NetworkViewRMI(GraphicalView graphicalView, String ipAddress, int port) throws RemoteException {
        super(graphicalView);

        // Try to get a remote Server instance from the network
        Registry registry = LocateRegistry.getRegistry(port);
        try {
            this.server = (ServerRMIInterface) registry.lookup("CodexNaturalisRMIServer");
        } catch (NotBoundException e) {
            // If the registry exists but the lookup string isn't found, exit the application since it's
            // a programmatic error (it regards the code, not the app life cycle)
            throw new RuntimeException(e);
        }
    }

    // Methods called by PlayerControllerRMI
    @Override
    public void receiveAvailableMatches(List<AvailableMatch> availableMatches) {
        graphicalView.receiveAvailableMatches(availableMatches);
    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) throws RemoteException {
        graphicalView.giveLobbyInfo(playersUsernames);
    }

    @Override
    public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
                             Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
                             Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {
        graphicalView.matchStarted(playersUsernamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards, decksTopReigns);
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) throws RemoteException {
        graphicalView.giveInitialCard(initialCard);
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        graphicalView.giveSecretObjectives(secretObjectives);
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        graphicalView.someoneDrewInitialCard(someoneUsername, card);
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) {
        graphicalView.someoneSetInitialSide(someoneUsername, side);
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        graphicalView.someoneDrewSecretObjective(someoneUsername);
    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        graphicalView.someoneChoseSecretObjective(someoneUsername);
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points,
                                  Map<Symbol, Integer> availableResources) {
        graphicalView.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
                                Symbol replacementCardReign) {
        graphicalView.someoneDrewCard(someoneUsername, source, card, replacementCard, replacementCardReign);
    }

    @Override
    public void someoneJoined(String someoneUsername) {
        graphicalView.someoneJoined(someoneUsername);
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        graphicalView.someoneQuit(someoneUsername);
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        graphicalView.matchFinished(ranking);
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        graphicalView.someoneSentBroadcastText(someoneUsername, text);
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        graphicalView.someoneSentPrivateText(someoneUsername, text);
    }

    // Methods called by the GraphicalView
    @Override
    public void getAvailableMatches() {
        try {
            List<AvailableMatch> matches = server.getJoinableMatches();
            this.receiveAvailableMatches(matches);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void joinMatch(String matchName) {
        try {
            controller = server.joinMatch(matchName, this.username);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        try {
            server.createMatch(matchName, maxPlayers);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void drawInitialCard() {
        try {
            controller.drawInitialCard();
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void chooseInitialCardSide(Side side) {
        try {
            controller.chooseInitialCardSide(side);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void drawSecretObjectives() {
        try {
            controller.drawSecretObjectives();
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void chooseSecretObjective(Objective objective) {
        try {
            controller.chooseSecretObjective(objective);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        try {
            controller.playCard(coords, card, side);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void drawCard(DrawSource source) {
        try {
            controller.drawCard(source);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

}
