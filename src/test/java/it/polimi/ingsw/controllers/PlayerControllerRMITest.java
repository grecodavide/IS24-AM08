// package it.polimi.ingsw.controllers;

// import static org.junit.Assert.assertEquals;
// import static org.junit.Assert.assertTrue;
// import static org.junit.Assert.fail;

// import java.lang.reflect.Field;
// import java.rmi.RemoteException;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.regex.PatternSyntaxException;

// import it.polimi.ingsw.utils.AvailableMatch;
// import it.polimi.ingsw.utils.LeaderboardEntry;
// import org.junit.Test;

// import it.polimi.ingsw.client.network.RemoteViewInterface;
// import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
// import it.polimi.ingsw.exceptions.WrongStateException;
// import it.polimi.ingsw.gamemodel.Color;
// import it.polimi.ingsw.gamemodel.DrawSource;
// import it.polimi.ingsw.gamemodel.GameDeck;
// import it.polimi.ingsw.gamemodel.GoldCard;
// import it.polimi.ingsw.gamemodel.InitialCard;
// import it.polimi.ingsw.gamemodel.Match;
// import it.polimi.ingsw.gamemodel.MatchTest;
// import it.polimi.ingsw.gamemodel.Objective;
// import it.polimi.ingsw.gamemodel.PlayableCard;
// import it.polimi.ingsw.gamemodel.Player;
// import it.polimi.ingsw.gamemodel.ResourceCard;
// import it.polimi.ingsw.gamemodel.Side;
// import it.polimi.ingsw.gamemodel.Symbol;
// import it.polimi.ingsw.utils.Pair;

// public class PlayerControllerRMITest {
//     private Match match;
//     private PlayerControllerRMI player1;
//     private PlayerControllerRMI player2;
//     private TestView view1;
//     private TestView view2;

//     @Test
//     public void constructor() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//         } catch (AlreadyUsedUsernameException | WrongStateException e) {
//             fail("player1 init shouldn't throw exception: " + e.getMessage());
//         }

//         try {
//             player2 = new PlayerControllerRMI("player1", match);
//             // An exception is supposed to be thrown here
//             fail("player 2 init should have thrown AlreadyUsedUsernameException");
//         } catch (AlreadyUsedUsernameException e) {
//             // this exception should be thrown
//         } catch (WrongStateException e) {
//             fail("player2 initialization shouldn't thrown this specific exception exception: " + e.getMessage());
//         }

//         try {
//             // The given match is in NextTurnState, should be in WaitState
//             initializeStartedMatch(2);
//             player1 = new PlayerControllerRMI("player3", match);
//             // An exception is supposed to be thrown here
//             fail("player 3 init should have thrown WrongStateException");
//         } catch (AlreadyUsedUsernameException e) {
//             fail("player2 initialization shouldn't thrown this specific exception exception: " + e.getMessage());
//         } catch (WrongStateException e) {
//             // this exception should be thrown
//         }
//     }

//     @Test
//     public void someoneJoined() {
//         this.initializeUnstartedMatch(3);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);

//             // Verify that adding player2 triggers player1.someoneJoined and it calls the view method someoneJoined
//             player2 = new PlayerControllerRMI("player2", match);

//             String name = (String) view1.getLastCallArguments().get("name");
//             assertEquals("someoneJoined: wrong last call in view1", "someoneJoined", view1.getLastCall());
//             assertEquals("someoneJoined: wrong args in view1", "player2", name);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneQuit() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);

//             // Verify that removing  player2 triggers player1.someoneQuit and it calls the view method someoneQuit
//             match.removePlayer(player2.getPlayer());

//             String name = (String) view1.getLastCallArguments().get("name");
//             assertEquals("someoneQuit: wrong last call in view1", "someoneQuit", view1.getLastCall());
//             assertEquals("someoneQuit: wrong args in view1", "player2", name);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneDrewInitialCard() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             // Let the current player call drawInitialCard
//             PlayerControllerRMI currentPlayer;
//             PlayerControllerRMI otherPlayer;

//             if (match.getCurrentPlayer().equals(player1.getPlayer())) {
//                 currentPlayer = player1;
//                 otherPlayer = player2;
//             } else {
//                 currentPlayer = player2;
//                 otherPlayer = player1;
//             }

//             TestView currentPlayerView = (TestView) currentPlayer.getView();
//             TestView otherPlayerView = (TestView) otherPlayer.getView();

//             currentPlayer.drawInitialCard();

//             // Verify that current player PlayerControllerRMI called the method giveInitialCard on its view
//             InitialCard card = (InitialCard) currentPlayerView.getLastCallArguments().get("card");
//             InitialCard drawnCard = (InitialCard) this.getPrivateAttribute(match, "currentGivenInitialCard");

//             assertEquals("someoneDrewInitialCard: wrong last call in currentPlayerView", "giveInitialCard", currentPlayerView.getLastCall());
//             assertEquals("someoneDrewInitialCard: wrong args in currentPlayerView", drawnCard, card);

//             // Verify that the other player PlayerControllerRMI called the method someoneDrewInitialCard on its view
//             card = (InitialCard) otherPlayerView.getLastCallArguments().get("card");
//             String name = (String) otherPlayerView.getLastCallArguments().get("name");


//             assertEquals("someoneDrewInitialCard: wrong last call in otherPlayerView", "someoneDrewInitialCard", otherPlayerView.getLastCall());
//             assertEquals("someoneDrewInitialCard: wrong card arg in otherPlayerView", drawnCard, card);
//             assertEquals("someoneDrewInitialCard: wrong name arg in otherPlayerView", currentPlayer.getPlayer().getUsername(), name);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneSetInitialSide() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             PlayerControllerRMI currentPlayer;
//             PlayerControllerRMI otherPlayer;

//             if (match.getCurrentPlayer().equals(player1.getPlayer())) {
//                 currentPlayer = player1;
//                 otherPlayer = player2;
//             } else {
//                 currentPlayer = player2;
//                 otherPlayer = player1;
//             }

//             TestView currentPlayerView = (TestView) currentPlayer.getView();
//             TestView otherPlayerView = (TestView) otherPlayer.getView();

//             currentPlayer.drawInitialCard();
//             currentPlayer.chooseInitialCardSide(Side.FRONT);

//             // Verify that current player PlayerControllerRMI called the method someoneSetInitialCard on its view
//             Side side = (Side) otherPlayerView.getLastCallArguments().get("side");
//             String name = (String) otherPlayerView.getLastCallArguments().get("name");

//             assertEquals("someoneSetInitialSide: wrong last call in currentPlayerView", "someoneSetInitialSide", currentPlayerView.getLastCall());
//             assertEquals("someoneSetInitialSide: wrong name arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
//             assertEquals("someoneSetInitialSide: wrong side arg in currentPlayerView", Side.FRONT, side);

//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneDrewSecretObjective() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             // Let the current player call drawInitialCard
//             PlayerControllerRMI currentPlayer;
//             PlayerControllerRMI otherPlayer;

//             if (match.getCurrentPlayer().equals(player1.getPlayer())) {
//                 currentPlayer = player1;
//                 otherPlayer = player2;
//             } else {
//                 currentPlayer = player2;
//                 otherPlayer = player1;
//             }

//             TestView currentPlayerView = (TestView) currentPlayer.getView();
//             TestView otherPlayerView = (TestView) otherPlayer.getView();

//             currentPlayer.drawInitialCard();
//             currentPlayer.chooseInitialCardSide(Side.FRONT);
//             otherPlayer.drawInitialCard();
//             otherPlayer.chooseInitialCardSide(Side.FRONT);
//             currentPlayer.drawSecretObjectives();

//             // Verify that current player PlayerControllerRMI called the method giveSecretObjectives on its view
//             Pair<Objective, Objective> objectives = (Pair<Objective, Objective>) currentPlayerView.getLastCallArguments().get("objectives");
//             Pair<Objective, Objective> actualObjectives = (Pair<Objective, Objective>) this.getPrivateAttribute(match, "currentProposedObjectives");

//             assertEquals("someoneDrewSecretObjective: wrong last call in currentPlayerView", "giveSecretObjectives", currentPlayerView.getLastCall());
//             assertEquals("someoneDrewSecretObjective: wrong arg in currentPlayerView", actualObjectives, objectives);

//             // Verify that current player PlayerControllerRMI called the method giveSecretObjectives on its view
//             String name = (String) otherPlayerView.getLastCallArguments().get("name");

//             assertEquals("someoneDrewSecretObjective: wrong last call in otherPlayerView", "someoneDrewSecretObjective", otherPlayerView.getLastCall());
//             assertEquals("someoneDrewSecretObjective: wrong arg in otherPlayerView", currentPlayer.getPlayer().getUsername(), name);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneChoseSecretObjective() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             PlayerControllerRMI currentPlayer;
//             PlayerControllerRMI otherPlayer;

//             if (match.getCurrentPlayer().equals(player1.getPlayer())) {
//                 currentPlayer = player1;
//                 otherPlayer = player2;
//             } else {
//                 currentPlayer = player2;
//                 otherPlayer = player1;
//             }

//             TestView currentPlayerView = (TestView) currentPlayer.getView();
//             TestView otherPlayerView = (TestView) otherPlayer.getView();

//             currentPlayer.drawInitialCard();
//             currentPlayer.chooseInitialCardSide(Side.FRONT);
//             otherPlayer.drawInitialCard();
//             otherPlayer.chooseInitialCardSide(Side.FRONT);
//             currentPlayer.drawSecretObjectives();
//             Objective obj = ((Pair<Objective, Objective>) currentPlayerView.getLastCallArguments().get("objectives")).first();
//             currentPlayer.chooseSecretObjective(obj);

//             // Verify that current player PlayerControllerRMI called the method someoneChoseSecretObjective on its view
//             String name = (String) currentPlayerView.getLastCallArguments().get("name");

//             assertEquals("someoneChoseSecretObjective: wrong last call in currentPlayerView", "someoneChoseSecretObjective", currentPlayerView.getLastCall());
//             assertEquals("someoneChoseSecretObjective: wrong arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someonePlayedCard() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             PlayerControllerRMI currentPlayer;
//             PlayerControllerRMI otherPlayer;

//             if (match.getCurrentPlayer().equals(player1.getPlayer())) {
//                 currentPlayer = player1;
//                 otherPlayer = player2;
//             } else {
//                 currentPlayer = player2;
//                 otherPlayer = player1;
//             }

//             TestView currentPlayerView = (TestView) currentPlayer.getView();
//             TestView otherPlayerView = (TestView) otherPlayer.getView();

//             currentPlayer.drawInitialCard();
//             currentPlayer.chooseInitialCardSide(Side.FRONT);
//             otherPlayer.drawInitialCard();
//             otherPlayer.chooseInitialCardSide(Side.FRONT);
//             currentPlayer.drawSecretObjectives();
//             Objective currObj = ((Pair<Objective, Objective>) currentPlayerView.getLastCallArguments().get("objectives")).first();
//             currentPlayer.chooseSecretObjective(currObj);
//             otherPlayer.drawSecretObjectives();
//             Objective otherObj = ((Pair<Objective, Objective>) otherPlayerView.getLastCallArguments().get("objectives")).first();
//             otherPlayer.chooseSecretObjective(otherObj);
//             PlayableCard playedCard = currentPlayer.getPlayer().getBoard().getCurrentHand().stream().filter(c -> c instanceof ResourceCard).findAny().get();
//             currentPlayer.playCard(new Pair<>(-1, 1), playedCard, Side.FRONT);

//             // Verify that current player PlayerControllerRMI called the method someonePlayedCard on its view
//             String name = (String) currentPlayerView.getLastCallArguments().get("name");
//             Pair<Integer, Integer> coords = (Pair<Integer, Integer>) currentPlayerView.getLastCallArguments().get("coords");
//             Side side = (Side) currentPlayerView.getLastCallArguments().get("side");
//             PlayableCard card = (PlayableCard) currentPlayerView.getLastCallArguments().get("card");

//             assertEquals("someonePlayedCard: wrong last call in currentPlayerView", "someonePlayedCard", currentPlayerView.getLastCall());
//             assertEquals("someonePlayedCard: wrong name arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
//             assertEquals("someonePlayedCard: wrong card arg in currentPlayerView", playedCard, card);
//             assertEquals("someonePlayedCard: wrong side arg in currentPlayerView", Side.FRONT, side);
//             assertEquals("someonePlayedCard: wrong coords(x) arg in currentPlayerView", Integer.valueOf(-1), coords.first());
//             assertEquals("someonePlayedCard: wrong coords(y) arg in currentPlayerView", Integer.valueOf(1), coords.second());
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneDrewCard() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             PlayerControllerRMI currentPlayer;
//             PlayerControllerRMI otherPlayer;

//             if (match.getCurrentPlayer().equals(player1.getPlayer())) {
//                 currentPlayer = player1;
//                 otherPlayer = player2;
//             } else {
//                 currentPlayer = player2;
//                 otherPlayer = player1;
//             }

//             TestView currentPlayerView = (TestView) currentPlayer.getView();
//             TestView otherPlayerView = (TestView) otherPlayer.getView();

//             currentPlayer.drawInitialCard();
//             currentPlayer.chooseInitialCardSide(Side.FRONT);
//             otherPlayer.drawInitialCard();
//             otherPlayer.chooseInitialCardSide(Side.FRONT);
//             currentPlayer.drawSecretObjectives();
//             Objective currObj = ((Pair<Objective, Objective>) currentPlayerView.getLastCallArguments().get("objectives")).first();
//             currentPlayer.chooseSecretObjective(currObj);
//             otherPlayer.drawSecretObjectives();
//             Objective otherObj = ((Pair<Objective, Objective>) otherPlayerView.getLastCallArguments().get("objectives")).first();
//             otherPlayer.chooseSecretObjective(otherObj);
//             PlayableCard playedCard = currentPlayer.getPlayer().getBoard().getCurrentHand().stream().filter(c -> c instanceof ResourceCard).findAny().get();
//             currentPlayer.playCard(new Pair<>(-1, 1), playedCard, Side.FRONT);
//             PlayableCard drawnCard = ((Map<DrawSource, PlayableCard>) this.getPrivateAttribute(match, "visiblePlayableCards")).get(DrawSource.FIRST_VISIBLE);
//             currentPlayer.drawCard(DrawSource.FIRST_VISIBLE);
//             PlayableCard actualReplCard = ((Map<DrawSource, PlayableCard>) this.getPrivateAttribute(match, "visiblePlayableCards")).get(DrawSource.FIRST_VISIBLE);
//             Symbol actualReplReign = actualReplCard.getReign();

//             // Verify that current player PlayerControllerRMI called the method someoneDrewCard on its view
//             String name = (String) currentPlayerView.getLastCallArguments().get("name");
//             DrawSource source = (DrawSource) currentPlayerView.getLastCallArguments().get("source");
//             Side side = (Side) currentPlayerView.getLastCallArguments().get("side");
//             PlayableCard card = (PlayableCard) currentPlayerView.getLastCallArguments().get("card");
//             PlayableCard replCard = (PlayableCard) currentPlayerView.getLastCallArguments().get("replCard");
//             Symbol replReign = (Symbol) currentPlayerView.getLastCallArguments().get("replReign");

//             assertEquals("someoneDrewCard: wrong last call in currentPlayerView", "someoneDrewCard", currentPlayerView.getLastCall());
//             assertEquals("someoneDrewCard: wrong name arg in currentPlayerView", currentPlayer.getPlayer().getUsername(), name);
//             assertEquals("someoneDrewCard: wrong card arg in currentPlayerView", drawnCard, card);
//             assertEquals("someoneDrewCard: wrong replCard arg in currentPlayerView", actualReplCard, replCard);
//             assertEquals("someoneDrewCard: wrong replReign arg in currentPlayerView", actualReplReign, replReign);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneSentBroadcastText() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             player1.sendBroadcastText("test text :)");

//             // Verify that current player PlayerControllerRMI called the method someonePlayedCard on its view
//             String name = (String) view1.getLastCallArguments().get("name");
//             String text = (String) view1.getLastCallArguments().get("text");

//             assertEquals("someoneSentBroadcastText: wrong last call in currentPlayerView", "someoneSentBroadcastText", view1.getLastCall());
//             assertEquals("someoneSentBroadcastText: wrong name arg in currentPlayerView", player1.getPlayer().getUsername(), name);
//             assertEquals("someoneSentBroadcastText: wrong text arg in currentPlayerView", "test text :)", text);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void someoneSentPrivateText() {
//         this.initializeUnstartedMatch(2);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             view1 = new TestView();
//             player1.registerView(view1);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();
//             player2.registerView(view2);

//             player1.sendPrivateText("player2", "test text :)");

//             // Verify that other player PlayerControllerRMI called the method someoneSentPrivateText on its view
//             String sender = (String) view2.getLastCallArguments().get("name");
//             String text = (String) view2.getLastCallArguments().get("text");

//             assertEquals("someoneSentPrivateText: wrong last call in currentPlayerView", "someoneSentPrivateText", view2.getLastCall());
//             assertEquals("someoneSentPrivateText: wrong name arg in currentPlayerView", player1.getPlayer().getUsername(), sender);
//             assertEquals("someoneSentPrivateText: wrong text arg in currentPlayerView", "test text :)", text);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     @Test
//     public void matchFinished() throws RemoteException, WrongStateException, AlreadyUsedUsernameException {
//         this.initializeTwoPlayerFinishedMatch();
//         Map<String, Object> args = view1.getLastCallArguments();
//         List<LeaderboardEntry> ranking = (List<LeaderboardEntry>) args.get("ranking");
//         List<Pair<Player, Boolean>> matchRaking = match.getPlayersFinalRanking();

//         for (int i =0; i < ranking.size(); i++) {
//             assertEquals(matchRaking.get(i).first().getUsername(), ranking.get(i).username());
//             assertEquals(matchRaking.get(i).second(), ranking.get(i).winner());
//         }
//     }

//     @Test
//     public void matchStarted() {
//         this.initializeUnstartedMatch(2);

//         this.addTwoPlayerWithView();
//         player1.matchStarted();
//         player2.matchStarted();
//         Map<String, Object> args = view1.getLastCallArguments();
//         Map<Color, String> pawns = (Map<Color, String>) args.get("pawns");
//         Map<String, List<PlayableCard>> hands = (Map<String, List<PlayableCard>>) args.get("hands");
//         Map<DrawSource, PlayableCard> visbile = (Map<DrawSource, PlayableCard>) args.get("playable");

//         assertEquals(match.getVisibleObjectives().first(), ((Pair<Objective, Objective>)args.get("objectives")).first());
//         assertEquals(match.getVisibleObjectives().second(), ((Pair<Objective, Objective>)args.get("objectives")).second());
//         assertEquals(match.getDecksTopReigns().first(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).first());
//         assertEquals(match.getDecksTopReigns().second(), ((Pair<Symbol, Symbol>) args.get("decksTopReigns")).second());
//         for (Player p : match.getPlayers()) {
//             assertEquals(p.getPawnColor(), pawns.get(p.getUsername()));
//             for (PlayableCard c : p.getBoard().getCurrentHand()) {
//                 assertTrue(hands.get(p.getUsername()).contains(c));
//             }
//         }
//         for (DrawSource s : visbile.keySet()) {
//             assertEquals(match.getVisiblePlayableCards().get(s), visbile.get(s));
//         }
//     }

//     @Test
//     public void registerView() {
//         this.initializeUnstartedMatch(3);

//         try {
//             player1 = new PlayerControllerRMI("player1", match);
//             player2 = new PlayerControllerRMI("player2", match);
//             view2 = new TestView();

//             // Verify that registering a view to player2 triggers player.registerView and it calls the view method getLobbyInfo
//             player1.registerView(view2);

//             List<String> names = (List<String>) view2.getLastCallArguments().get("names");

//             assertEquals("registerView: wrong last call in view2", "giveLobbyInfo", view2.getLastCall());
//             assertTrue("registerView: wrong args in view2", names.contains("player1"));
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     // Utility methods

//     private Object getPrivateAttribute(Object object, String privateAttr) throws NoSuchFieldException, IllegalAccessException {
//         Field privateField = object.getClass().getDeclaredField(privateAttr);
//         privateField.setAccessible(true);

//         return privateField.get(object);
//     }

//     private void initializeUnstartedMatch(int maxPlayers) {
//         match = new Match(maxPlayers, MatchTest.createDeterministicInitialsDeck(10), MatchTest.createDeterministicResourcesDeck(40), MatchTest.createDeterministicGoldsDeck(40), MatchTest.createDeterministicObjectivesDeck(6));
//     }

//     private void initializeStartedMatch(int maxPlayers) {
//         GameDeck<InitialCard> initialsDeck;
//         GameDeck<ResourceCard> resourcesDeck;
//         GameDeck<GoldCard> goldsDeck;
//         GameDeck<Objective> objectivesDeck;

//         initialsDeck = MatchTest.createDeterministicInitialsDeck(10);
//         objectivesDeck = MatchTest.createDeterministicObjectivesDeck(6);
//         goldsDeck = MatchTest.createDeterministicGoldsDeck(40);
//         resourcesDeck = MatchTest.createDeterministicResourcesDeck(40);

//         // Set up a basic game
//         String[] names = {"Oingo", "Boingo", "Jotaro", "Polnareff"};
//         Player[] players = new Player[4];

//         match = new Match(maxPlayers, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

//         for (int i = 0; i < maxPlayers; i++) {
//             players[i] = new Player(names[i], match);
//             try {
//                 match.addPlayer(players[i]);
//             } catch (Exception e) {
//                 throw new RuntimeException(e);
//             }
//         }
//     }

//     public void initializeTwoPlayerFinishedMatch() throws WrongStateException, AlreadyUsedUsernameException, RemoteException {
//         int maxPlayers = 2;

//         GameDeck<InitialCard> initialsDeck;
//         GameDeck<ResourceCard> resourcesDeck;
//         GameDeck<GoldCard> goldsDeck;
//         GameDeck<Objective> objectivesDeck;

//         initialsDeck = MatchTest.createDeterministicInitialsDeck(10);
//         objectivesDeck = MatchTest.createDeterministicObjectivesDeck(6);
//         goldsDeck = MatchTest.createDeterministicGoldsDeck(40);
//         resourcesDeck = MatchTest.createDeterministicResourcesDeck(40);

//         match = new Match(maxPlayers, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

//         player1 = new PlayerControllerRMI("Oingo", match);
//         view1 = new TestView();
//         player1.registerView(view1);

//         player2 = new PlayerControllerRMI("Boingo", match);
//         view2 = new TestView();
//         player2.registerView(view2);

//         try {
//             for (int i = 0; i < maxPlayers; i++) {
//                 match.getCurrentPlayer().drawInitialCard();
//                 match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);
//             }

//             for (int i = 0; i < maxPlayers; i++) {
//                 Pair<Objective, Objective> obj = match.getCurrentPlayer().drawSecretObjectives();
//                 match.getCurrentPlayer().chooseSecretObjective(obj.first());
//             }
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//         int i = 1;
//         while (!match.isFinished()) {
//             try {
//                 match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
//                 match.getCurrentPlayer().drawCard(decideDrawSource(resourcesDeck, goldsDeck));
//                 match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
//                 match.getCurrentPlayer().drawCard(decideDrawSource(resourcesDeck, goldsDeck));
//             } catch (Exception e) {
//                 throw new RuntimeException(e);
//             }
//             i++;
//         }
//     }

//     private void addTwoPlayerWithView() {
//         try {
//             player1 = new PlayerControllerRMI("Oingo", match);
//             view1 = new TestView();
//             player1.registerView(view1);

//             player2 = new PlayerControllerRMI("Boingo", match);
//             view2 = new TestView();
//             player2.registerView(view2);
//         } catch (Exception e) {
//             throw new RuntimeException(e);
//         }
//     }

//     private DrawSource decideDrawSource(GameDeck<ResourceCard> resourcesDeck, GameDeck<GoldCard> goldsDeck) {
//         Map<DrawSource, PlayableCard> visible = match.getVisiblePlayableCards();
//         if (!resourcesDeck.isEmpty()) {
//             return DrawSource.RESOURCES_DECK;
//         } else if (!goldsDeck.isEmpty()) {
//             return DrawSource.GOLDS_DECK;
//         } else {
//             DrawSource[] values = DrawSource.values();
//             for (int i=2; i < 6; i++) {
//                 if (visible.get(values[i]) != null) {
//                     return values[i];
//                 }
//             }
//         }
//         return null;
//     }

//     private static class TestView implements RemoteViewInterface {
//         private String lastCall;
//         private Map<String, Object> args;

//         public void giveInitialCard(InitialCard initialCard) {
//             lastCall = "giveInitialCard";
//             args = new HashMap<>();
//             args.put("card", initialCard);
//         }

//         public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
//             lastCall = "giveSecretObjectives";
//             args = new HashMap<>();
//             args.put("objectives", secretObjectives);
//         }

//         public void giveLobbyInfo(List<String> playersUsernames) throws RemoteException {
//             lastCall = "giveLobbyInfo";
//             args = new HashMap<>();
//             args.put("names", playersUsernames);
//         }

//         public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {
//             lastCall = "matchStarted";
//             args = new HashMap<>();
//             args.put("pawns", playersUsernamesAndPawns);
//             args.put("hands", playersHands);
//             args.put("objectives", visibleObjectives);
//             args.put("playable", visiblePlayableCards);
//             args.put("decksTopReigns", decksTopReigns);
//         }

//         @Override
//         public void receiveAvailableMatches(List<AvailableMatch> availableMatchs) throws RemoteException {

//         }

//         public void someoneDrewInitialCard(String someoneUsername, InitialCard card) throws RemoteException {
//             lastCall = "someoneDrewInitialCard";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//             args.put("card", card);
//         }

//         public void someoneSetInitialSide(String someoneUsername, Side side) throws RemoteException {
//             lastCall = "someoneSetInitialSide";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//             args.put("side", side);
//         }

//         public void someoneDrewSecretObjective(String someoneUsername) throws RemoteException {
//             lastCall = "someoneDrewSecretObjective";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//         }

//         public void someoneChoseSecretObjective(String someoneUsername) throws RemoteException {
//             lastCall = "someoneChoseSecretObjective";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//         }

//         @Override
//         public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) throws RemoteException {
//             lastCall = "someonePlayedCard";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//             args.put("coords", coords);
//             args.put("card", card);
//             args.put("side", side);
//             args.put("resources", availableResources);
//         }

//         public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard, Symbol replacementReign) throws RemoteException {
//             lastCall = "someoneDrewCard";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//             args.put("source", source);
//             args.put("card", card);
//             args.put("replCard", replacementCard);
//             args.put("replReign", replacementReign);
//         }

//         public void someoneJoined(String someoneUsername) throws RemoteException {
//             lastCall = "someoneJoined";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//         }

//         public void someoneQuit(String someoneUsername) throws RemoteException {
//             lastCall = "someoneQuit";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//         }

//         @Override
//         public void matchFinished(List<LeaderboardEntry> ranking) throws RemoteException {
//             lastCall = "matchFinished";
//             args = new HashMap<>();
//             args.put("ranking", ranking);
//         }

//         public void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException {
//             lastCall = "someoneSentBroadcastText";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//             args.put("text", text);
//         }

//         public void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException {
//             lastCall = "someoneSentPrivateText";
//             args = new HashMap<>();
//             args.put("name", someoneUsername);
//             args.put("text", text);
//         }

//         public String getLastCall() {
//             return lastCall;
//         }

//         public Map<String, Object> getLastCallArguments() {
//             return args;
//         }
//     }
// }
