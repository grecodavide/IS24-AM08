package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.RemoteViewInterface;
import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.ServerRMIInterface;
import it.polimi.ingsw.utils.Pair;
import org.junit.Test;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PlayerControllerRMITest {
    private Server server;
    private Match match;
    private PlayerControllerRMI player1;
    private PlayerControllerRMI player2;
    private PlayerControllerRMI player3;
    private PlayerControllerRMI player4;
    TestView view1;
    TestView view2;

    public PlayerControllerRMITest() throws RemoteException, ChosenMatchException {
        server = new Server(1099, 14000);

        server.startRMIServer();
        server.createMatch("match", 4);
        match = server.getMatch("match");
    }

    @Test
    public void constructor() {
        try {
            player1 = new PlayerControllerRMI("player1", match, 1099);
        } catch (AlreadyUsedNicknameException | WrongStateException | RemoteException e) {
            fail("player1 init shouldn't throw exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            player2 = new PlayerControllerRMI("player1", match, 1099);
            // An exception is supposed to be thrown here
            fail("player 2 init should have thrown AlreadyUsedNicknameException");
        } catch (AlreadyUsedNicknameException e) {
        } catch (WrongStateException | RemoteException e) {
            fail("player2 initialization shouldn't thrown this specific exception exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            player3 = new PlayerControllerRMI("player3", match, 8000);
            // An exception is supposed to be thrown here
            fail("player 3 init should have thrown RemoteException");
        } catch (RemoteException e) {
        } catch (AlreadyUsedNicknameException | WrongStateException e) {
            fail("player3 init shouldn't throw this specific exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            player2 = new PlayerControllerRMI("player2", match, 1099);
            player3 = new PlayerControllerRMI("player3", match, 1099);
            player4 = new PlayerControllerRMI("player4", match, 1099);
        } catch (AlreadyUsedNicknameException | WrongStateException | RemoteException e) {
            throw new RuntimeException(e);
        }

        System.out.println("");

    }
    @Test public void matchFinished() throws ChosenMatchException, RemoteException, WrongStateException, AlreadyUsedNicknameException {
        this.getFinishedMatch();
        Map<String, Object> args = view1.getLastCallArguments();
        List<Pair<String, Boolean>> ranking =(List<Pair<String, Boolean>>)args.get("ranking");
        List<Pair<Player, Boolean>> matchRaking = match.getPlayersFinalRanking();
        for (int i =0; i < ranking.size(); i++) {
            assertEquals(matchRaking.get(i).first().getNickname(), ranking.get(i).first());
            assertEquals(matchRaking.get(i).second(), ranking.get(i).second());
        }

    }
    @Test
    public void matchStarted() throws ChosenMatchException, RemoteException {
        this.addPlayerWithView();
        player1.matchStarted();
        player2.matchStarted();
        Map<String, Object> args = view1.getLastCallArguments();
        Map<Color, String> pawns = (Map<Color, String>) args.get("pawns");
        Map<String, List<PlayableCard>> hands = (Map<String, List<PlayableCard>>) args.get("hands");
        Map<DrawSource, PlayableCard> visbile = (Map<DrawSource, PlayableCard>) args.get("playable");
        assertEquals(match.getVisibleObjectives().first(), ((Pair<Objective, Objective>)args.get("objectives")).first());
        assertEquals(match.getVisibleObjectives().second(), ((Pair<Objective, Objective>)args.get("objectives")).second());
        assertEquals(match.getDecksTopReigns().first(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).first());
        assertEquals(match.getDecksTopReigns().second(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).second());
        for (Player p : match.getPlayers()) {
            assertEquals(p.getNickname(), pawns.get(p.getPawnColor()));
            for (PlayableCard c : p.getBoard().getCurrentHand()) {
                assertTrue(hands.get(p.getNickname()).contains(c));
            }
        }
        for (DrawSource s : visbile.keySet()) {
            assertEquals(match.getVisiblePlayableCards().get(s), visbile.get(s));
        }
    }

    public void addPlayerWithView() throws RemoteException, ChosenMatchException {
        server = new Server(1099, 1100);
        try {
            player1 = new PlayerControllerRMI("Oingo", match, 1099);
            view1 = new TestView(player1);
            player1.registerView(view1);

            player2 = new PlayerControllerRMI("Boingo", match, 1099);
            view2 = new TestView(player2);
            player2.registerView(view2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



    private class TestView implements RemoteViewInterface {
        private PlayerControllerRMIInterface remoteController;
        private String lastCall;
        private Map<String, Object> args;

        public TestView(PlayerControllerRMI controller) {
            this.remoteController = controller;
        }

        @Override
        public void giveLobbyInfo(List<String> playersNicknames) throws RemoteException {
            lastCall = "giveLobbyInfo";
        }

        @Override
        public void matchStarted(Map<Color, String> playersNicknamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {
            lastCall = "matchStarted";
            args = new HashMap<>();
            args.put("pawns", playersNicknamesAndPawns);
            args.put("hands", playersHands);
            args.put("objectives", visibleObjectives);
            args.put("playable", visiblePlayableCards);
            args.put("decksTopReigns", decksTopReigns);
        }

        @Override
        public void giveInitialCard(InitialCard initialCard) throws RemoteException {

        }

        @Override
        public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException {

        }

        @Override
        public void someoneDrewInitialCard(String someoneNickname, InitialCard card) throws RemoteException {

        }

        @Override
        public void someoneSetInitialSide(String someoneNickname, Side side) throws RemoteException {

        }

        @Override
        public void someoneDrewSecretObjective(String someoneNickname) throws RemoteException {

        }

        @Override
        public void someoneChoseSecretObjective(String someoneNickname) throws RemoteException {

        }

        @Override
        public void someonePlayedCard(String someoneNickname, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points) throws RemoteException {

        }

        @Override
        public void someoneDrewCard(String someoneNickname, DrawSource source, PlayableCard card, PlayableCard replacementCard, Symbol replacementReign) throws RemoteException {

        }

        @Override
        public void someoneJoined(String someoneNickname) throws RemoteException {

        }

        @Override
        public void someoneQuit(String someoneNickname) throws RemoteException {

        }

        @Override
        public void matchFinished(List<Pair<String, Boolean>> ranking) throws RemoteException {
            lastCall = "matchFinished";
            args = new HashMap<>();
            args.put("ranking", ranking);
        }

        @Override
        public void someoneSentBroadcastText(String someoneNickname, String text) throws RemoteException {

        }

        @Override
        public void someoneSentPrivateText(String someoneNickname, String text) throws RemoteException {

        }

        public String getLastCall() {
            return lastCall;
        }

        public Map<String, Object> getLastCallArguments() {
            return args;
        }
    }

    public void getFinishedMatch() throws WrongStateException, AlreadyUsedNicknameException, RemoteException {
        int maxPlayers = 2;

        GameDeck<InitialCard> initialsDeck;
        GameDeck<ResourceCard> resourcesDeck;
        GameDeck<GoldCard> goldsDeck;
        GameDeck<Objective> objectivesDeck;

        initialsDeck = MatchTest.createDeterministicInitialsDeck(10);
        objectivesDeck = MatchTest.createDeterministicObjectivesDeck(6);
        goldsDeck = MatchTest.createDeterministicGoldsDeck(40);
        resourcesDeck = MatchTest.createDeterministicResourcesDeck(40);

        match = new Match(maxPlayers, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        player1 = new PlayerControllerRMI("Oingo", match, 1099);
        view1 = new TestView(player1);
        player1.registerView(view1);

        player2 = new PlayerControllerRMI("Boingo", match, 1099);
        view2 = new TestView(player2);
        player1.registerView(view2);

        try {
            for (int i = 0; i < maxPlayers; i++) {
                match.getCurrentPlayer().drawInitialCard();
                match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);
            }

            for (int i = 0; i < maxPlayers; i++) {
                Pair<Objective, Objective> obj = match.getCurrentPlayer().drawSecretObjectives();
                match.getCurrentPlayer().chooseSecretObjective(obj.first());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        int i = 1;
        while (!match.isFinished()) {
            try {
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource(resourcesDeck, goldsDeck));
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource(resourcesDeck, goldsDeck));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i++;
        }
    }

    private DrawSource decideDrawSource(GameDeck<ResourceCard> resourcesDeck, GameDeck<GoldCard> goldsDeck) {
        Map<DrawSource, PlayableCard> visible = match.getVisiblePlayableCards();
        if (!resourcesDeck.isEmpty()) {
            return DrawSource.RESOURCES_DECK;
        } else if (!goldsDeck.isEmpty()) {
            return DrawSource.GOLDS_DECK;
        } else {
            DrawSource[] values = DrawSource.values();
            for (int i=2; i < 6; i++) {
                if (visible.get(values[i]) != null) {
                    return values[i];
                }
            }
        }
        return null;
    }
}
