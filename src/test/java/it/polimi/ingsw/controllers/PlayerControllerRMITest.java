package it.polimi.ingsw.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import org.junit.Test;

import it.polimi.ingsw.client.network.RemoteViewInterface;
import it.polimi.ingsw.utils.Pair;

public class PlayerControllerRMITest {
    private Match match;
    private PlayerControllerRMI player1;
    private PlayerControllerRMI player2;
    private TestView view1;
    private TestView view2;

    @Test
    public void constructor() {
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            player1.registerView(new TestView());
        } catch (AlreadyUsedUsernameException | WrongStateException | ChosenMatchException | RemoteException |
                 WrongNameException e) {
            fail("player1 init shouldn't throw exception: " + e.getMessage());
        }

        try {
            player2 = new PlayerControllerRMI("player1", match);
            player2.registerView(new TestView());
            // An exception is supposed to be thrown here
            fail("player 2 init should have thrown AlreadyUsedUsernameException");
        } catch (AlreadyUsedUsernameException e) {
            // this exception should be thrown
        } catch (WrongStateException | ChosenMatchException | WrongNameException e) {
            fail("player2 initialization shouldn't thrown this specific exception exception: " + e.getMessage());
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        try {
            // The given match is in NextTurnState, should be in WaitState
            initializeStartedMatch(2);
            player1 = new PlayerControllerRMI("player3", match);
            player1.registerView(new TestView());
            // An exception is supposed to be thrown here
            fail("player 3 init should have thrown WrongStateException");
        } catch (AlreadyUsedUsernameException | ChosenMatchException | RemoteException | WrongNameException e) {
            fail("player2 initialization shouldn't thrown this specific exception exception: " + e.getMessage());
        } catch (WrongStateException e) {
            // this exception should be thrown
        }
    }

    @Test
    public void someoneJoined() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());

        this.initializeUnstartedMatch(3);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();

            Thread t = new Thread(() -> {
                synchronized (view1) {
                    try {
                        player1.registerView(view1);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    view1.notifyAll();
                }
            });

            String name = (String) view1.waitForCall("someoneJoined", t).get("name");
            assertEquals("someoneJoined: wrong args in view1", "player1", name);
            // Verify that adding player2 triggers player1.someoneJoined and it calls the view method someoneJoined
            player2 = new PlayerControllerRMI("player2", match);
            player2.registerView(new TestView());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneQuit() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(3);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            player2.registerView(new TestView());
            // Verify that removing  player2 triggers player1.someoneQuit and it calls the view method someoneQuit

            Thread th = new Thread(() -> {
                synchronized (view1) {
                    match.removePlayer(player2.getPlayer());
                }
            });

            Map<String, Object> args = view1.waitForCall("someoneQuit", th);
            String name = (String) args.get("name");
            assertEquals("someoneQuit: wrong args in view1", "player2", name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneDrewInitialCard() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);

            // Let the current player call drawInitialCard
            PlayerControllerRMI currentPlayer;
            PlayerControllerRMI otherPlayer;

            if (match.getCurrentPlayer().equals(player1.getPlayer())) {
                currentPlayer = player1;
                otherPlayer = player2;
            } else {
                currentPlayer = player2;
                otherPlayer = player1;
            }

            TestView currentPlayerView = (TestView) currentPlayer.getView();
            TestView otherPlayerView = (TestView) otherPlayer.getView();
            otherPlayerView.waitingCall = "someoneDrewCard";
            Thread th = new Thread(() -> {
                synchronized (currentPlayerView) {
                    try {
                        currentPlayer.drawInitialCard();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            otherPlayerView.waitingCall = "someoneDrewInitialCard";
            // Verify that current player PlayerControllerRMI called the method giveInitialCard on its view
            Map<String, Object> lastCall = currentPlayerView.waitForCall("giveInitialCard", th);
            InitialCard card = (InitialCard) lastCall.get("card");
            InitialCard drawnCard = (InitialCard) this.getPrivateAttribute(match, "currentGivenInitialCard");

            assertEquals("someoneDrewInitialCard: wrong args in currentPlayerView", drawnCard, card);

            // Verify that the other player PlayerControllerRMI called the method someoneDrewInitialCard on its view
            lastCall = otherPlayerView.waitForCall("someoneDrewInitialCard");
            card = (InitialCard) lastCall.get("card");
            String name = (String) lastCall.get("name");


            assertEquals("someoneDrewInitialCard: wrong card arg in otherPlayerView", drawnCard, card);
            assertEquals("someoneDrewInitialCard: wrong name arg in otherPlayerView", currentPlayer.getPlayer().getUsername(), name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneSetInitialSide() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);

            PlayerControllerRMI currentPlayer;
            PlayerControllerRMI otherPlayer;

            if (match.getCurrentPlayer().equals(player1.getPlayer())) {
                currentPlayer = player1;
                otherPlayer = player2;
            } else {
                currentPlayer = player2;
                otherPlayer = player1;
            }

            TestView currentPlayerView = (TestView) currentPlayer.getView();
            TestView otherPlayerView = (TestView) otherPlayer.getView();
            currentPlayer.drawInitialCard();

            Thread th = new Thread(() -> {
                synchronized (currentPlayerView) {
                    try {
                        currentPlayer.chooseInitialCardSide(Side.FRONT);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            Map<String, Object> lastCall = otherPlayerView.waitForCall("someoneSetInitialSide", th);
            // Verify that current player PlayerControllerRMI called the method someoneSetInitialCard on its view
            Side side = (Side) lastCall.get("side");
            String name = (String) lastCall.get("name");

            assertEquals("someoneSetInitialSide: wrong name arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
            assertEquals("someoneSetInitialSide: wrong side arg in currentPlayerView", Side.FRONT, side);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneDrewSecretObjective() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);

            // Let the current player call drawInitialCard
            PlayerControllerRMI currentPlayer;
            PlayerControllerRMI otherPlayer;

            if (match.getCurrentPlayer().equals(player1.getPlayer())) {
                currentPlayer = player1;
                otherPlayer = player2;
            } else {
                currentPlayer = player2;
                otherPlayer = player1;
            }

            TestView currentPlayerView = (TestView) currentPlayer.getView();
            TestView otherPlayerView = (TestView) otherPlayer.getView();

            currentPlayer.drawInitialCard();
            currentPlayer.chooseInitialCardSide(Side.FRONT);
            otherPlayer.drawInitialCard();
            otherPlayer.chooseInitialCardSide(Side.FRONT);

            Thread th = new Thread(() -> {
                synchronized (currentPlayerView) {
                    try {
                        currentPlayer.drawSecretObjectives();
                        currentPlayerView.notifyAll();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // Verify that current player PlayerControllerRMI called the method giveSecretObjectives on its view
            Map<String, Object> lastCall = currentPlayerView.waitForCall("giveSecretObjectives", th);
            Pair<Objective, Objective> objectives = (Pair<Objective, Objective>) lastCall.get("objectives");
            Pair<Objective, Objective> actualObjectives = (Pair<Objective, Objective>) this.getPrivateAttribute(match, "currentProposedObjectives");

            assertEquals("someoneDrewSecretObjective: wrong arg in currentPlayerView", actualObjectives, objectives);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneChoseSecretObjective() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);

            PlayerControllerRMI currentPlayer;
            PlayerControllerRMI otherPlayer;

            if (match.getCurrentPlayer().equals(player1.getPlayer())) {
                currentPlayer = player1;
                otherPlayer = player2;
            } else {
                currentPlayer = player2;
                otherPlayer = player1;
            }

            TestView currentPlayerView = (TestView) currentPlayer.getView();
            TestView otherPlayerView = (TestView) otherPlayer.getView();

            currentPlayer.drawInitialCard();
            currentPlayer.chooseInitialCardSide(Side.FRONT);
            otherPlayer.drawInitialCard();
            otherPlayer.chooseInitialCardSide(Side.FRONT);
            Objective obj = currentPlayer.getPlayer().drawSecretObjectives().first();
            Thread th = new Thread(() -> {
                synchronized (currentPlayerView) {
                    try {
                        currentPlayer.chooseSecretObjective(obj);
                        currentPlayerView.notifyAll();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            // Verify that current player PlayerControllerRMI called the method someoneChoseSecretObjective on its view
            String name = (String) currentPlayerView.waitForCall("someoneChoseSecretObjective", th).get("name");

            assertEquals("someoneChoseSecretObjective: wrong arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someonePlayedCard() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);

            PlayerControllerRMI currentPlayer;
            PlayerControllerRMI otherPlayer;

            if (match.getCurrentPlayer().equals(player1.getPlayer())) {
                currentPlayer = player1;
                otherPlayer = player2;
            } else {
                currentPlayer = player2;
                otherPlayer = player1;
            }

            TestView currentPlayerView = (TestView) currentPlayer.getView();
            TestView otherPlayerView = (TestView) otherPlayer.getView();

            // Advance the game
            currentPlayer.drawInitialCard();
            currentPlayer.chooseInitialCardSide(Side.FRONT);
            otherPlayer.drawInitialCard();
            otherPlayer.chooseInitialCardSide(Side.FRONT);
            Objective currObj = currentPlayer.getPlayer().drawSecretObjectives().first();
            currentPlayer.chooseSecretObjective(currObj);
            Objective otherObj = otherPlayer.getPlayer().drawSecretObjectives().first();
            otherPlayer.chooseSecretObjective(otherObj);

            // Play the card
            PlayableCard playedCard = currentPlayer.getPlayer().getBoard().getCurrentHand().stream().filter(c -> c instanceof ResourceCard).findAny().get();
            Thread th = new Thread(() -> {
                synchronized (currentPlayerView) {
                    try {
                        currentPlayer.playCard(new Pair<>(-1, 1), playedCard, Side.FRONT);
                        currentPlayerView.notifyAll();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // Verify that current player PlayerControllerRMI called the method someonePlayedCard on its view
            Map<String, Object> lastCall = currentPlayerView.waitForCall("someonePlayedCard", th);
            String name = (String) lastCall.get("name");
            Pair<Integer, Integer> coords = (Pair<Integer, Integer>) lastCall.get("coords");
            Side side = (Side) lastCall.get("side");
            PlayableCard card = (PlayableCard) lastCall.get("card");

            assertEquals("someonePlayedCard: wrong name arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
            assertEquals("someonePlayedCard: wrong card arg in currentPlayerView", playedCard, card);
            assertEquals("someonePlayedCard: wrong side arg in currentPlayerView", Side.FRONT, side);
            assertEquals("someonePlayedCard: wrong coords(x) arg in currentPlayerView", Integer.valueOf(-1), coords.first());
            assertEquals("someonePlayedCard: wrong coords(y) arg in currentPlayerView", Integer.valueOf(1), coords.second());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneDrewCard() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);

            PlayerControllerRMI currentPlayer;
            PlayerControllerRMI otherPlayer;

            if (match.getCurrentPlayer().equals(player1.getPlayer())) {
                currentPlayer = player1;
                otherPlayer = player2;
            } else {
                currentPlayer = player2;
                otherPlayer = player1;
            }

            TestView currentPlayerView = (TestView) currentPlayer.getView();
            TestView otherPlayerView = (TestView) otherPlayer.getView();

            // Advance the game
            currentPlayer.drawInitialCard();
            currentPlayer.chooseInitialCardSide(Side.FRONT);
            otherPlayer.drawInitialCard();
            otherPlayer.chooseInitialCardSide(Side.FRONT);
            Objective currObj = currentPlayer.getPlayer().drawSecretObjectives().first();
            currentPlayer.chooseSecretObjective(currObj);
            Objective otherObj = otherPlayer.getPlayer().drawSecretObjectives().first();
            otherPlayer.chooseSecretObjective(otherObj);
            PlayableCard playedCard = currentPlayer.getPlayer().getBoard().getCurrentHand().stream().filter(c -> c instanceof ResourceCard).findAny().get();
            currentPlayer.playCard(new Pair<>(-1, 1), playedCard, Side.FRONT);
            Thread.sleep(200);
            // Check drawn card
            PlayableCard drawnCard = ((Map<DrawSource, PlayableCard>) this.getPrivateAttribute(match, "visiblePlayableCards")).get(DrawSource.FIRST_VISIBLE);

            // Verify that current player PlayerControllerRMI called the method someoneDrewCard on its view
            Thread th = new Thread(() -> {
                synchronized (currentPlayerView) {
                    try {
                        currentPlayer.drawCard(DrawSource.FIRST_VISIBLE);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            Map<String, Object> lastCall = currentPlayerView.waitForCall("someoneDrewCard", th);

            PlayableCard actualReplCard = ((Map<DrawSource, PlayableCard>) this.getPrivateAttribute(match, "visiblePlayableCards")).get(DrawSource.FIRST_VISIBLE);
            Pair<Symbol, Symbol> deckReign = match.getDecksTopReigns();

            String name = (String) lastCall.get("name");
            DrawSource source = (DrawSource) lastCall.get("source");
            Side side = (Side) lastCall.get("side");
            PlayableCard card = (PlayableCard) lastCall.get("card");
            PlayableCard replCard = (PlayableCard) lastCall.get("replCard");
            Pair<Symbol, Symbol> reigns = (Pair<Symbol, Symbol>) lastCall.get("deckReign");

            assertEquals("someoneDrewCard: wrong name arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
            assertEquals("someoneDrewCard: wrong card arg in currentPlayerView", drawnCard, card);
            assertEquals("someoneDrewCard: wrong replCard arg in currentPlayerView", actualReplCard, replCard);
            assertEquals("someoneDrewCard: wrong deckReign arg in currentPlayerView", reigns, deckReign);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneSentBroadcastText() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);
            Thread.sleep(200);

            Thread th = new Thread(() -> {
                synchronized (view1) {
                    try {
                        player1.sendBroadcastText("test text :)");
                        view1.notifyAll();
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            // Verify that current player PlayerControllerRMI called the method someonePlayedCard on its view

            Map<String, Object> lastCall = view1.waitForCall("someoneSentBroadcastText", th);
            String name = (String) lastCall.get("name");
            String text = (String) lastCall.get("text");

            assertEquals("someoneSentBroadcastText: wrong name arg in currentPlayerView", player1.getPlayer().getUsername(), name);
            assertEquals("someoneSentBroadcastText: wrong text arg in currentPlayerView", "test text :)", text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void someoneSentPrivateText() {
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName());
        this.initializeUnstartedMatch(2);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            view1 = new TestView();
            player1.registerView(view1);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            player2.registerView(view2);

            Thread th = new Thread(() -> {
                synchronized (view2) {
                    try {
                        player1.sendPrivateText("player2", "test text :)");
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            // Verify that other player PlayerControllerRMI called the method someoneSentPrivateText on its view
            Map<String, Object> lastCall  = view2.waitForCall("someoneSentPrivateText", th);
            String sender = (String) lastCall.get("name");
            String text = (String) lastCall.get("text");

            assertEquals("someoneSentPrivateText: wrong name arg in currentPlayerView", player1.getPlayer().getUsername(), sender);
            assertEquals("someoneSentPrivateText: wrong text arg in currentPlayerView", "test text :)", text);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void matchFinished() throws RemoteException, WrongStateException, AlreadyUsedUsernameException, ChosenMatchException, InterruptedException {
        this.initializeTwoPlayerFinishedMatch();
        view1.waitingCall = "matchFinished";
        view1.waitForCall("matchFinished");
        Map<String, Object> args = view1.getLastCallArguments();
        List<LeaderboardEntry> ranking = (List<LeaderboardEntry>) args.get("ranking");
        List<Pair<Player, Boolean>> matchRaking = match.getPlayersFinalRanking();

        for (int i =0; i < ranking.size(); i++) {
            assertEquals(matchRaking.get(i).first().getUsername(), ranking.get(i).username());
            assertEquals(matchRaking.get(i).second(), ranking.get(i).winner());
        }
    }

    @Test
    public void matchStarted() {
        this.initializeUnstartedMatch(2);
        this.addTwoPlayerWithView();
        view1.waitingCall = "matchStarted";
        player1.matchStarted();
        player2.matchStarted();

        view1.waitForCall("matchStarted");
        Map<String, Object> args = view1.getLastCallArguments();
        Map<Color, String> pawns = (Map<Color, String>) args.get("pawns");
        Map<String, List<PlayableCard>> hands = (Map<String, List<PlayableCard>>) args.get("hands");
        Map<DrawSource, PlayableCard> visbile = (Map<DrawSource, PlayableCard>) args.get("playable");

        assertEquals(match.getVisibleObjectives().first(), ((Pair<Objective, Objective>)args.get("objectives")).first());
        assertEquals(match.getVisibleObjectives().second(), ((Pair<Objective, Objective>)args.get("objectives")).second());
        assertEquals(match.getDecksTopReigns().first(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).first());
        assertEquals(match.getDecksTopReigns().second(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).second());
        for (Player p : match.getPlayers()) {
            assertEquals(p.getPawnColor(), pawns.get(p.getUsername()));
            for (PlayableCard c : p.getBoard().getCurrentHand()) {
                assertTrue(hands.get(p.getUsername()).contains(c));
            }
        }
        for (DrawSource s : visbile.keySet()) {
            assertEquals(match.getVisiblePlayableCards().get(s), visbile.get(s));
        }
    }

    @Test
    public void matchResumed() {
        this.initializeUnstartedMatch(2);
        this.addTwoPlayerWithView();
        view1.waitingCall = "matchResumed";
        player1.matchResumed();
        player2.matchResumed();

        view1.waitForCall("matchResumed");
        Map<String, Object> args = view1.getLastCallArguments();
        Map<Color, String> pawns = (Map<Color, String>) args.get("pawns");
        Map<String, List<PlayableCard>> hands = (Map<String, List<PlayableCard>>) args.get("hands");
        Map<DrawSource, PlayableCard> visbile = (Map<DrawSource, PlayableCard>) args.get("playable");

        assertEquals(match.getVisibleObjectives().first(), ((Pair<Objective, Objective>)args.get("objectives")).first());
        assertEquals(match.getVisibleObjectives().second(), ((Pair<Objective, Objective>)args.get("objectives")).second());
        assertEquals(match.getDecksTopReigns().first(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).first());
        assertEquals(match.getDecksTopReigns().second(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).second());
        for (Player p : match.getPlayers()) {
            assertEquals(p.getPawnColor(), pawns.get(p.getUsername()));
            for (PlayableCard c : p.getBoard().getCurrentHand()) {
                assertTrue(hands.get(p.getUsername()).contains(c));
            }
        }
        for (DrawSource s : visbile.keySet()) {
            assertEquals(match.getVisiblePlayableCards().get(s), visbile.get(s));
        }
    }

    @Test
    public void registerView() {
        this.initializeUnstartedMatch(3);

        try {
            player1 = new PlayerControllerRMI("player1", match);
            player2 = new PlayerControllerRMI("player2", match);
            view2 = new TestView();
            view2.waitingCall = "someoneJoined";
            // Verify that registering a view to player2 triggers player.registerView and it calls the view method getLobbyInfo
            player1.registerView(view2);

            List<String> names = (List<String>) view2.getLastCallArguments().get("names");

            assertTrue("registerView: wrong args in view2", names.contains("player1"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Utility methods

    private Object getPrivateAttribute(Object object, String privateAttr) throws NoSuchFieldException, IllegalAccessException {
        Field privateField = object.getClass().getDeclaredField(privateAttr);
        privateField.setAccessible(true);

        return privateField.get(object);
    }

    private void initializeUnstartedMatch(int maxPlayers) {
        match = new Match(maxPlayers, MatchTest.createDeterministicInitialsDeck(10), MatchTest.createDeterministicResourcesDeck(40), MatchTest.createDeterministicGoldsDeck(40), MatchTest.createDeterministicObjectivesDeck(6));
    }

    private void initializeStartedMatch(int maxPlayers) {
        GameDeck<InitialCard> initialsDeck;
        GameDeck<ResourceCard> resourcesDeck;
        GameDeck<GoldCard> goldsDeck;
        GameDeck<Objective> objectivesDeck;

        initialsDeck = MatchTest.createDeterministicInitialsDeck(10);
        objectivesDeck = MatchTest.createDeterministicObjectivesDeck(6);
        goldsDeck = MatchTest.createDeterministicGoldsDeck(40);
        resourcesDeck = MatchTest.createDeterministicResourcesDeck(40);

        // Set up a basic game
        String[] names = {"Oingo", "Boingo", "Jotaro", "Polnareff"};
        Player[] players = new Player[4];

        match = new Match(maxPlayers, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        for (int i = 0; i < maxPlayers; i++) {
            players[i] = new Player(names[i], match);
            try {
                match.addPlayer(players[i]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void initializeTwoPlayerFinishedMatch() throws WrongStateException, AlreadyUsedUsernameException, RemoteException, ChosenMatchException {
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

        try {
            player1 = new PlayerControllerRMI("Oingo", match);
            view1 = new TestView();
            player1.registerView(view1);

            player2 = new PlayerControllerRMI("Boingo", match);
            view2 = new TestView();
            player2.registerView(view2);

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

    private void addTwoPlayerWithView() {
        try {
            player1 = new PlayerControllerRMI("Oingo", match);
            view1 = new TestView();
            player1.registerView(view1);

            player2 = new PlayerControllerRMI("Boingo", match);
            view2 = new TestView();
            player2.registerView(view2);
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    private static class TestView implements RemoteViewInterface {
        private String lastCall = "";
        private Map<String, Object> args;
        private String waitingCall = null;


        public synchronized void giveInitialCard(InitialCard initialCard) {
            if (waitingCall != null && waitingCall.equals("giveInitialCard")) {
                lastCall = "giveInitialCard";
                this.notifyAll();
                args = new HashMap<>();
                args.put("card", initialCard);
            }

        }

        public synchronized void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
            if (waitingCall != null && waitingCall.equals("giveSecretObjectives")) {
                lastCall = "giveSecretObjectives";
                args = new HashMap<>();
                args.put("objectives", secretObjectives);
                this.notifyAll();
            }
        }

        public synchronized void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("matchStarted")) {
                lastCall = "matchStarted";
                args = new HashMap<>();
                args.put("pawns", playersUsernamesAndPawns);
                args.put("hands", playersHands);
                args.put("objectives", visibleObjectives);
                args.put("playable", visiblePlayableCards);
                args.put("decksTopReigns", decksTopReigns);
                this.notifyAll();
            }
        }

        @Override
        public synchronized void matchResumed(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns, Objective secretObjective, Map<String, Map<Symbol, Integer>> availableResources, Map<String, Map<Pair<Integer, Integer>, PlacedCard>> placedCards, Map<String, Integer> playerPoints, String currentPlayer, boolean drawPhase) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("matchResumed")) {
                lastCall = "matchResumed";
                args = new HashMap<>();
                args.put("pawns", playersUsernamesAndPawns);
                args.put("hands", playersHands);
                args.put("objectives", visibleObjectives);
                args.put("playable", visiblePlayableCards);
                args.put("decksTopReigns", decksTopReigns);
                this.notifyAll();
            }
        }

        @Override
        public void receiveAvailableMatches(List<AvailableMatch> availableMatchs) throws RemoteException {
        }

        public synchronized void someoneDrewInitialCard(String someoneUsername, InitialCard card) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneDrewInitialCard")) {
                lastCall = "someoneDrewInitialCard";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                args.put("card", card);
                this.notifyAll();
            }
        }

        @Override
        public synchronized void someoneSetInitialSide(String someoneUsername, Side side, Map<Symbol, Integer> availableResources) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneSetInitialSide")) {
                lastCall = "someoneSetInitialSide";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                args.put("side", side);
                this.notifyAll();
            }
        }

        public synchronized void someoneDrewSecretObjective(String someoneUsername) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneDrewSecretObjective")) {

                lastCall = "someoneDrewSecretObjective";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                this.notifyAll();
            }
        }

        public synchronized void someoneChoseSecretObjective(String someoneUsername) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneChoseSecretObjective")) {
                lastCall = "someoneChoseSecretObjective";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                this.notifyAll();
            }
        }

        @Override
        public synchronized void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someonePlayedCard")) {
                lastCall = "someonePlayedCard";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                args.put("coords", coords);
                args.put("card", card);
                args.put("side", side);
                args.put("resources", availableResources);
                this.notifyAll();
            }
        }

        public synchronized void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard, Pair<Symbol, Symbol> deckTopReigns) throws RemoteException {
            lastCall = "someoneDrewCard";
            if (waitingCall != null && waitingCall.equals("someoneDrewCard")) {
                args = new HashMap<>();
                args.put("name", someoneUsername);
                args.put("source", source);
                args.put("card", card);
                args.put("replCard", replacementCard);
                args.put("deckReign", deckTopReigns);
                this.notifyAll();
            }
        }

        @Override
        public synchronized void someoneJoined(String someoneUsername, List<String> joinedPlayers) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneJoined")) {
                lastCall = "someoneJoined";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                args.put("names", joinedPlayers);
                this.notifyAll();
            }
        }

        @Override
        public synchronized void someoneQuit(String someoneUsername) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneQuit")) {
                lastCall = "someoneQuit";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                this.notifyAll();
            }
        }

        @Override
        public synchronized void matchFinished(List<LeaderboardEntry> ranking) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("matchFinished")) {
                lastCall = "matchFinished";
                args = new HashMap<>();
                args.put("ranking", ranking);
                this.notifyAll();
            }
        }

        public synchronized void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneSentBroadcastText")) {
                lastCall = "someoneSentBroadcastText";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                args.put("text", text);
                this.notifyAll();
            }
        }

        public synchronized void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException {
            if (waitingCall != null && waitingCall.equals("someoneSentPrivateText")) {
                lastCall = "someoneSentPrivateText";
                args = new HashMap<>();
                args.put("name", someoneUsername);
                args.put("text", text);
                this.notifyAll();
            }
        }

        public String getLastCall() {
            return lastCall;
        }

        public synchronized Map<String, Object> waitForCall(String call) {
            waitingCall = call;
            while (!lastCall.equals(call)) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return args;
        }

        public synchronized Map<String, Object> waitForCall(String call, Thread th) {
            waitingCall = call;
            th.start();
            while (!lastCall.equals(call)) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException();
                }
            }
            waitingCall = null;
            this.notifyAll();
            return args;
        }
        public synchronized Map<String, Object> waitForCallDestructive(String call) {
            while (!lastCall.equals(call)) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return getLastCallArguments();
        }
        public Map<String, Object> getLastCallArguments() {
            synchronized (this) {
                while (args == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                HashMap<String, Object> argsCopy = new HashMap<>();
                argsCopy.putAll(args);
                args = null;
                return argsCopy;
            }
        }
    }
}
