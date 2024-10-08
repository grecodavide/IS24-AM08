package it.polimi.ingsw.gamemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.*;
import javax.management.RuntimeErrorException;
import org.junit.Test;
import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.responses.MatchStartedMessage;
import it.polimi.ingsw.utils.MessageJsonParser;
import it.polimi.ingsw.utils.Pair;

public class MatchTest {

    private Match match;
    private Player player1, player2, player3, player4;
    private GameDeck<InitialCard> initialsDeck;
    private GameDeck<ResourceCard> resourcesDeck;
    private GameDeck<GoldCard> goldsDeck;
    private GameDeck<Objective> objectivesDeck;

    @Test
    public void constructor() {
        player1 = new Player("Oingo", match);
        player2 = new Player("Boingo", match);
        player3 = new Player("Foingo", match);
        player4 = new Player("Francesco Edoardo Caracciolo", match);

        // Verify that Match throws an exception when the initial cards deck has fewer
        // cards than the number of players (3 cards in this case, minus than 4
        // players in the match)
        initializeAllEqualDecks();
        initialsDeck = createInitialsDeck(3);

        try {
            match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            // An exception is supposed to be thrown here
            fail("Match constructor: Exception about initials deck not thrown");
        } catch (IllegalArgumentException e) {}

        // Verify that Match throws an exception when the gold cards deck has less
        // than 9 cards
        initializeAllEqualDecks();
        goldsDeck = createGoldsDeck(5);

        try {
            match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            // An exception is supposed to be thrown here
            fail("Match constructor: Exception about golds deck not thrown");
        } catch (IllegalArgumentException e) {}

        // Verify that Match throws an exception when the initial cards deck has less
        // than 10 cards
        initializeAllEqualDecks();
        resourcesDeck = createResourcesDeck(9);

        try {
            match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            // An exception is supposed to be thrown here
            fail("Match constructor: Exception about resources deck not thrown");
        } catch (IllegalArgumentException e) {}

        // Verify that Match throws an exception when the number of players is more or
        // less than expected
        initializeAllEqualDecks();
        objectivesDeck = createObjectivesDeck(5);

        try {
            match = new Match(1, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            // An exception is supposed to be thrown here
            fail("Match constructor: Exception about players not thrown");
        } catch (IllegalArgumentException e) {}
    }

    @Test
    public void addPlayer() {
        initializeAllEqualDecks();

        player1 = new Player("Oingo", match);
        player2 = new Player("Boingo", match);
        player3 = new Player("Miozio", match);

        match = new Match(2, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        // Verify that Match throws an exception when a plyer is added while the match
        // is in a state which is not valid (i.e. not equal to WaitState)
        try {
            match.setState(new AfterMoveState(match));
            match.addPlayer(player1);
            // An exception is supposed to be thrown here
            fail("Exception about wrong state not thrown ");
        }
        catch (WrongStateException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        match = new Match(2, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        // Verify that all players are correctly added
        try {
            match.addPlayer(player1);
            match.addPlayer(player2);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        assertTrue("1st player not added", match.getPlayers().contains(player1));
        assertTrue("2nd player not added", match.getPlayers().contains(player2));

        // Verify that Match throws an exception when a player already in the match
        // is added again
        try {
            match.addPlayer(player1);
            // An exception is supposed to be thrown here
            fail("Exception about duplicated player not thrown");
        }
        catch (AlreadyUsedUsernameException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify that Match throws an exception when a player is added even if the
        // math is full
        try {
            match.addPlayer(player3);
            // An exception is supposed to be thrown here
            fail("Exception about too many players not thrown");
        }
        catch (WrongStateException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void nextPlayer() {
        initializeAllEqualDecks();

        player1 = new Player("Oingo", match);
        player2 = new Player("Boingo", match);
        player3 = new Player("Miozio", match);

        match = new Match(3, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        try {
            match.addPlayer(player1);
            match.addPlayer(player2);
            // a third player is not added, otherwise Match would be full and go to NextTurnState, then call nextTurn
            // automatically
        } catch (WrongStateException|AlreadyUsedUsernameException e) {
            throw new RuntimeException(e);
        }

        // Verify that match sets the current player as the first in the players List
        match.nextPlayer();
        assertEquals("Current player not equal to 1st player", match.getCurrentPlayer(), match.getPlayers().get(0));

        // Verify that match switches to the next player in the List, the 2nd
        match.nextPlayer();
        assertEquals("Current player not equal to 2st player", match.getCurrentPlayer(), match.getPlayers().get(1));

        // Verify that match switches to the next player in the list, back to the 1st
        match.nextPlayer();
        assertEquals("Current player not equal to 1st player again", match.getCurrentPlayer(), match.getPlayers().get(0));
    }

    @Test
    public void removePlayer() {
        initializeAllEqualDecks();

        match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        player1 = new Player("Oingo", match);
        player2 = new Player("Boingo", match);
        player3 = new Player("Jotaro", match);
        player4 = new Player("Polnareff", match);

        // Tests while Match is in WaitState: removePlayer can be called
        try {
            match.addPlayer(player1);
            match.addPlayer(player2);

            // Verify that Match correctly removes a player
            match.removePlayer(player1);
            assertFalse("Player not removed", match.getPlayers().contains(player1));

            // Match should now be full and switch to NextTurnState
            match.addPlayer(player1);
            match.addPlayer(player3);
            match.addPlayer(player4);
        } catch (Exception e) {
            fail("No exception must be thrown, exception thrown:" + e.getMessage());
        }

        // Verify that Match is in the right state, that is NextTurnState
        assertTrue("Match not in correct state", match.getCurrentState() instanceof NextTurnState);

        // Match is not in NextTurnState
        // Tests while Match is NOT in WaitState: removePlayer stops the match
        try {
            // Verify that Match throws an exception when the method is called while being in the wrong state
            match.removePlayer(player1);
            // An exception is supposed to be thrown here
        } catch (Exception e) {
            fail("Wrong exception: " + e.getMessage());
        }
        // Verify that Match, after a player quit in the wrong state, is in FinalState
        assertTrue("Match is not in FinalState even if a player quit in the wrong state", match.getCurrentState() instanceof FinalState);

        match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        // Verify that players with the same username can't join
        player1 = new Player("Doppio", match);
        player2 = new Player("Doppio", match);
        try {
            match.addPlayer(player1);
            match.addPlayer(player2);
            fail("AlreadyUsedUsernameException not thrown");
        } catch (AlreadyUsedUsernameException e) {
            // Good
        } catch (Exception e) {
            fail("Wrong exception returned: " + e);
        }

    }

    @Test
    public void drawInitialCard() {
        initializeBlankMatch(2);

        InitialCard card = null;

        try {
            card = match.drawInitialCard();
        } catch (WrongStateException e) {
            throw new RuntimeException(e);
        }

        // Verify that Match draws the inital card
        assertNotNull(card);

        // Verify that Match has gone to the next state
        assertTrue("State not changed after drawInitialCard", match.getCurrentState() instanceof ChooseInitialSideState);

        // Verify that Match throws an exception when the method is called while being in the wrong state
        match.setState(new AfterMoveState(match));

        try {
            match.drawInitialCard();
            // An exception is supposed to be thrown here
            fail("drawInitialCard called in wrong state and has not given an exception");
        } catch (WrongStateException e) {}
    }

    @Test
    public void setInitialSide() {
        // Set up a basic game
        initializeBlankMatch(2);

        // For each player in the match
        for (int i = 0; i < 2; i++) {
            Player current = match.getCurrentPlayer();
            Side side = Side.values()[i];
            InitialCard card = null;

            try {
                card = current.drawInitialCard();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // Verify that Match doesn't throw a WrongStateException if the current state is in the right state
            // i.e. ChooseInitialSideState
            try {
                match.setInitialSide(side, null);
            } catch (WrongStateException e) {
                fail("Exception thrown even if in ChooseInitialSideState" + e.getMessage());
            }

            PlacedCard playedCard = current.getBoard().getPlacedCards().get(new Pair<>(0,0));
            Side playedSide = playedCard.getPlayedSide();

            assertEquals("Side not applied correctly", playedSide, side);
            assertEquals("Wrong card played", card, playedCard.getCard());
            assertTrue("Wrong state after setInitialSide", match.getCurrentState() instanceof NextTurnState);
        }

    }

    @Test
    public void proposeSecretObjectives() {
        // Initialize a match
        initializeBlankMatch(2);

        // Setup Match so that, in the end, should be in NextTurnState and ready to go to ChooseSecretObjectiveState
        try {
            for (int i = 0; i < 2; i++) {
                match.getCurrentPlayer().drawInitialCard();
                match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Start actual testing
        // For each player in the match
        for (int i = 0; i < 2; i++) {
            try {
                // Should trigger match state transition to ChooseSecretObjectiveState
                Pair<Objective, Objective> obj = match.proposeSecretObjectives();

                assertNotNull(obj);
                assertNotNull(obj.first());
                assertNotNull(obj.second());
                assertTrue("State change has not happened", match.getCurrentState() instanceof ChooseSecretObjectiveState);

                // Trigger match state transition to NextTurnState
                match.setSecretObjective(obj.first());
            } catch (Exception e) {
                fail("Exception should not have been thrown " + e.getMessage());
            }
        }

        // Match should now be in AfterMoveState
        try {
            match.proposeSecretObjectives();
            // An exception is supposed to be thrown here
            fail("Exception not thrown when called in the wrong state");
        } catch (WrongStateException e) {}
    }

    @Test
    public void setSecretObjective() {
        initializeBlankMatch(2);
        try {
            for (int i = 0; i < 2; i++) {
                match.getCurrentPlayer().drawInitialCard();
                match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Start actual testing
        // Call in wrong state
        try {
            match.setSecretObjective(generateRandomObjective());
            fail("setSecretObjective has not thrown a wrong state exception");
        } catch (WrongStateException e) {
            assertTrue("Exception thrown correctly", true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < 2; i++) {
            Pair<Objective, Objective> obj = null;
            try {
                obj = match.proposeSecretObjectives();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // Test setting the objective to a non given one
            try {
                match.setSecretObjective(generateRandomObjective());
                fail("Should have thrown a WrongChoiceException");
            } catch (WrongChoiceException e) {
                // Good
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            // Test the correct behavoir
            try {
                int size = objectivesDeck.getSize();
                match.setSecretObjective(obj.first());
                assertEquals("The card has not been put back in the deck",objectivesDeck.getSize(), size+1);
            } catch (Exception e) {
                fail("An exception should not have been thrown" + e.getMessage());
            }
        }
    }

    @Test
    public void makeMove() {
        initializeBlankStartedMatch(2);

        // Try placing a card in the wrong coordinates
        try {
            match.makeMove(new Pair<>(1, 4), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.FRONT);
            fail("Should have thrown a WrongChoiceException");
        } catch (WrongChoiceException e) {
            // Good
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Try placing that is not in the player hand
        try {
            match.makeMove(new Pair<>(1, 1), generateRandomResourceCard(), Side.FRONT);
            fail("Should have thrown WrongChoiceException");
        } catch (WrongChoiceException e) {
            // Good
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Try placing a card that the user does not enough resources to
        PlayableCard card = match.getCurrentPlayer().getBoard().getCurrentHand().get(0);
        try {
            match.makeMove(new Pair<>(1, 1), card, Side.FRONT);
            fail("Card placed even though there were not enough resources available");
        } catch (Exception e) {
            // Good
        }

        // Try placing the card in the right position
        card = match.getCurrentPlayer().getBoard().getCurrentHand().get(1);
        // Note: now user has 2 Fungus

        try {
            match.makeMove(new Pair<>(1, 1), card, Side.BACK);
        } catch (Exception e) {
            fail("Exception thrown when no exception expected" + e.getMessage());
        }

        assertEquals("Card not placed", card, match.getCurrentPlayer().getBoard().getPlacedCards().get(new Pair<>(1,1)).getCard());
        assertEquals("Card not removed from hand", 2, match.getCurrentPlayer().getBoard().getCurrentHand().size());
        assertTrue("State not changed after makeMove", match.getCurrentState() instanceof AfterMoveState);

        // Make the game go on
        try {
            match.getCurrentPlayer().drawCard(DrawSource.RESOURCES_DECK);
            match.makeMove(new Pair<>(1,1), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
            match.getCurrentPlayer().drawCard(DrawSource.RESOURCES_DECK);
        } catch (Exception e) {
            fail(match.getCurrentState().getClass().getSimpleName());
            throw new RuntimeException(e);
        }
        // Test placing a gold card with enough resources, should not fail
        PlayableCard card2 = match.getCurrentPlayer().getBoard().getCurrentHand().get(0);
        try {
            Player curr = match.getCurrentPlayer();
            int oldplayerpoints = curr.getPoints();
            match.makeMove(new Pair<>(2,2), card2, Side.FRONT);
            assertEquals(oldplayerpoints + card2.points, match.getCurrentPlayer().getPoints());
        } catch (Exception e) {
            fail("No exception should be thrown: " + e.getMessage());
        }
    }

    @Test
    public void drawCard() {
        initializeBlankStartedMatch(2, 2, 10, 9, 7);

        // Try drawing card in the wrong state
        try {
            match.drawCard(DrawSource.FIRST_VISIBLE);
            // An exception is supposed to be thrown here
            fail("Should have thrown WrongStateException");
        }
        catch (WrongStateException e) {}
        catch (Exception e) {
            fail("Wrong exception launched: " + e.getMessage());
        }
        assertEquals(3, goldsDeck.getSize());
        assertEquals(3, resourcesDeck.getSize());
        // NOTE: at this point of the game
        // resourcesDeck: 3 card goldsDeck: 3 cards
        // Try drawing a card from FIRST_VISIBLE
        // Draw from every source
        DrawSource[] sources = DrawSource.values();
        int j = 1;
        for (int i = 0; i < 6; i++) {
            try {
                Player p = match.getCurrentPlayer();

                p.playCard(new Pair<>(j, j), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                // Increment indexes for the next player
                if (match.getPlayers().getLast().equals(match.getCurrentPlayer())) j++;
                PlayableCard card = match.drawCard(sources[i]);
                p.getBoard().addHandCard(card);
                assertNotNull(card);
                // Check right replacement
                if (i == 0) {
                    assertTrue("Wrong state reached after draw", match.getCurrentState() instanceof NextTurnState);
                    assertEquals(2, goldsDeck.getSize());
                } else if (i == 1) {
                    assertTrue("Wrong state reached after draw", match.getCurrentState() instanceof NextTurnState);
                    assertEquals(2, resourcesDeck.getSize());
                } else if (i <= 3) {
                    assertTrue("Wrong state reached after draw", match.getCurrentState() instanceof NextTurnState);
                    assertTrue("Wrong card placed", match.getVisiblePlayableCards().get(sources[i]) instanceof GoldCard);
                } else {
                    if (i == 4) {
                        assertTrue("Wrong state reached after draw", match.getCurrentState() instanceof NextTurnState);
                    } else {
                        assertTrue("Wrong state reached after draw", match.getCurrentState() instanceof FinalState);
                    }
                    assertTrue("Wrong card placed", match.getVisiblePlayableCards().get(sources[i]) instanceof ResourceCard);
                }
            } catch (Exception e) {
                fail("Should not have launched an exception: " + e.getMessage());
            }
        }
        // Check the state
        // Now all the decks should be empty and the match should be finished
        assertEquals(0, goldsDeck.getSize());
        assertEquals(0, resourcesDeck.getSize());
        assertTrue(match.isFinished());

        // Try drawing from an empty deck
        initializeBlankStartedMatch(2, 2, 10, 6, 4);
        j=1;
        assertFalse(match.isFinished());
        // Draw from the two empty decks
        for (int i = 0; i < 2; i++) {
            try {
                match.getCurrentPlayer().playCard(new Pair<>(j, j), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.drawCard(sources[i]);
            } catch (WrongChoiceException e) {
                // Go on with the state, correct exception launched
                try {
                    match.drawCard(sources[i + 2]);
                } catch (Exception e2) {
                    throw new RuntimeException(e2);
                }
            } catch (Exception e) {
                fail("Wrong exception launched: " + e.getMessage() + " " + match.getCurrentState().getClass().getSimpleName());
            }
        }
        assertTrue(match.isFinished());
    }

    @Test
    public void decideWinners() {
        // Note: match and because of deck end has already been tested
        // in drawCard testing
        // Create a match where both players win
        initializeBlankStartedMatch(2, 2, 10, 9, 7);
        // The objectives used as a test give
        // two points for every two fungus
        Integer i = 1;
        // For every move, a fungus is placed, the index is the number of fungus a player has
        while (!match.isFinished()) {
            try {
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        int pair_i = i%2==0 ? i :  i-1;
        // Since there are three objectives,
        // the expected number of points from each player is
        // pair_i * 3
        for (Player p : match.getPlayers()) {
            assertEquals(i, p.getBoard().getAvailableResources().get(Symbol.FUNGUS));
            assertEquals("Wrong points given to players",pair_i * 3, p.getPoints());
        }
        // Check if the ranking is right
        List<Pair<Player, Boolean>> ranking = match.getPlayersFinalRanking();
        assertTrue("Player is not marked as winner", ranking.get(0).second());
        assertTrue("Player is not marked as winner", ranking.get(1).second());
        // Create a match where only one player wins
        initializeBlankMatch(2, 2, 6, 9, 7);
        try {
            // Finish initials card turn
            match.getCurrentPlayer().drawInitialCard();
            match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);
            match.getCurrentPlayer().drawInitialCard();
            match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);

            // Change objectives deck
            Objective[] normalObjectives = {objectivesDeck.poll(), objectivesDeck.poll()};
            while (!objectivesDeck.isEmpty()) objectivesDeck.poll();
            HashMap<Symbol, Integer> impossibleRequirement = new HashMap<>();
            impossibleRequirement.put(Symbol.ANIMAL, 900000);
            Objective impossible = new Objective(0, new QuantityRequirement(impossibleRequirement));
            objectivesDeck.add(impossible);
            objectivesDeck.add(normalObjectives[0]);
            objectivesDeck.add(normalObjectives[1]);

            // Let players "choose" their objectives
            match.getCurrentPlayer().drawSecretObjectives();
            match.getCurrentPlayer().chooseSecretObjective(impossible);
            match.getCurrentPlayer().drawSecretObjectives();
            match.getCurrentPlayer().chooseSecretObjective(normalObjectives[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        i = 1;
        // For every move, a fungus is placed, the index is the number of fungus a player has
        while (!match.isFinished()) {
            try {
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        player1 = match.getPlayers().get(0);
        player2 = match.getPlayers().get(1);
        assertEquals("Wrong points given to players",pair_i * 3, player2.getPoints());
        // player 1 has only two objectives
        assertEquals("Wrong points given to players",pair_i * 2, player1.getPoints());
        // Check if the ranking is right
        ranking = match.getPlayersFinalRanking();
        // Player 2 wins, player 1 lost
        assertEquals(player2, ranking.get(0).first());
        assertEquals(player1, ranking.get(1).first());
        assertTrue("Player is not marked as winner", ranking.get(0).second());
        assertFalse("Player is marked as winner", ranking.get(1).second());

        // This variable is useful for next test
        int pointDifference = player2.getPoints() - player1.getPoints();
        // Test that the winner is choosen correctly when
        // they have the same amount of points but different amount of objectives


        // Same as before...
        initializeBlankMatch(2, 2, 6, 9, 7);
        player1 = match.getPlayers().get(0);
        player2 = match.getPlayers().get(1);
        try {
            // Finish initials card turn
            match.getCurrentPlayer().drawInitialCard();
            match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);
            match.getCurrentPlayer().drawInitialCard();
            match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);

            // Change objectives deck
            Objective[] normalObjectives = {objectivesDeck.poll(), objectivesDeck.poll()};
            while (!objectivesDeck.isEmpty()) objectivesDeck.poll();
            HashMap<Symbol, Integer> impossibleRequirement = new HashMap<>();
            impossibleRequirement.put(Symbol.ANIMAL, 900000);
            Objective impossible = new Objective(0, new QuantityRequirement(impossibleRequirement));
            objectivesDeck.add(impossible);
            objectivesDeck.add(normalObjectives[0]);
            objectivesDeck.add(normalObjectives[1]);

            // Let players "choose" their objectives
            match.getCurrentPlayer().drawSecretObjectives();
            match.getCurrentPlayer().chooseSecretObjective(impossible);
            match.getCurrentPlayer().drawSecretObjectives();
            match.getCurrentPlayer().chooseSecretObjective(normalObjectives[1]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        i = 1;
        // For every move, a fungus is placed, the index is the number of fungus a player has
        // Let's add a cheat gold card to player1 hand
        GoldCard cheatCard = null;
        HashMap<Symbol, Integer> noreq = new HashMap<>();
        noreq.put(Symbol.FUNGUS, 1);
        try {
            player1.getBoard().removeHandCard(player1.getBoard().getCurrentHand().get(1));
            cheatCard = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FUNGUS, Symbol.FULL_CORNER, Collections.emptySet()),
                    Symbol.FUNGUS,
                    Symbol.NO_MULT,
                    pointDifference,
                    new QuantityRequirement(noreq)
            );
            player1.getBoard().addHandCard(cheatCard);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Simulate the match
        if (match.getCurrentPlayer().equals(player1)) {
            try {
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(2), Side.FRONT);
                match.getCurrentPlayer().drawCard(decideDrawSource());
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i++;
        } else {
            try {
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(2), Side.FRONT);
                match.getCurrentPlayer().drawCard(decideDrawSource());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        player1.getPoints();
        player2.getPoints();
        while (!match.isFinished()) {
            try {
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
                match.getCurrentPlayer().playCard(new Pair<>(i, i), match.getCurrentPlayer().getBoard().getCurrentHand().get(1), Side.BACK);
                match.getCurrentPlayer().drawCard(decideDrawSource());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i++;
        }
        // Check that the players have the same amount of points
        assertEquals(player1.getPoints(), player2.getPoints());
        // Check if the ranking is right
        ranking = match.getPlayersFinalRanking();
        // Player 2 wins, player 1 lost
        assertEquals(player2, ranking.get(0).first());
        assertEquals(player1, ranking.get(1).first());
        assertTrue("Player is not marked as winner", ranking.get(0).second());
        assertFalse("Player is marked as winner", ranking.get(1).second());
        MessageJsonParser parser = new MessageJsonParser();
    }

    @Test
    public void generateJson() {
        initializeBlankStartedMatch(3);
        Message m = new MatchStartedMessage(match.getVisibleObjectives(), match.getVisiblePlayableCards(), match.getDecksTopReigns(), match.getPlayers());
        MessageJsonParser parser = new MessageJsonParser();
        System.out.println(parser.toJson(m));
    }
    // Private helper Methods
    private DrawSource decideDrawSource() {
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

    private void initializeAllEqualDecks() {
        // Initialize Initial Cards deck
        initializeAllEqualDecks(4, 10, 40, 40);
    }

    private void initializeAllEqualDecks(int initialsNum, int objectsNum, int resourcesNum, int goldsNum) {
        initialsDeck = createDeterministicInitialsDeck(initialsNum);
        objectivesDeck = createDeterministicObjectivesDeck(objectsNum);
        goldsDeck = createDeterministicGoldsDeck(goldsNum);
        resourcesDeck = createDeterministicResourcesDeck(resourcesNum);
    }

    public static GameDeck<Objective> createDeterministicObjectivesDeck(int size) {
        // Initialize Objective deck
        GameDeck<Objective> objectivesDeck = new GameDeck<>();
        Objective objective = null;
        HashMap<Symbol, Integer> resources = new HashMap<>();
        resources.put(Symbol.FUNGUS, 2);
        Requirement req;
        try {
            req = new QuantityRequirement(resources);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        for (int i = 0; i < size; i++) {
            objective = new Objective(2, req);
            objectivesDeck.add(objective);
        }
        return objectivesDeck;
    }

    public static GameDeck<InitialCard> createDeterministicInitialsDeck(int size) {
        // Initialize Initial Cards deck
        GameDeck<InitialCard> initialsDeck = new GameDeck<InitialCard>();
        InitialCard card = null;
        for (int i = 0; i < size; i++) {
            card = new InitialCard(
                    new CardFace(Symbol.FUNGUS, Symbol.ANIMAL, Symbol.PLANT, Symbol.INSECT, Collections.emptySet()),
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER,
                            Collections.emptySet()));
            initialsDeck.add(card);
        }
        return initialsDeck;
    }

    public static GameDeck<ResourceCard> createDeterministicResourcesDeck(int size) {
        GameDeck<ResourceCard> resourcesDeck = new GameDeck<>();
        ResourceCard card = null;
        for (int i = 0; i < size; i++) {
            try {
                card = new ResourceCard(
                        new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER,
                                Collections.emptySet()),
                        Symbol.FUNGUS, 0);
                resourcesDeck.add(card);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return resourcesDeck;
    }

    public static GameDeck<GoldCard> createDeterministicGoldsDeck(int size) {
        GameDeck<GoldCard> goldsDeck = new GameDeck<>();
        GoldCard card = null;
        HashMap<Symbol, Integer> resources = new HashMap<>();
        resources.put(Symbol.FUNGUS, 2);
        for (int i = 0; i < size; i++) {
            try {
                card = new GoldCard(new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER,
                        Symbol.FULL_CORNER, Collections.emptySet()), Symbol.FUNGUS, Symbol.NO_MULT, 3,
                        new QuantityRequirement(resources));
                goldsDeck.add(card);
            } catch (Exception e) {
                throw new RuntimeErrorException(null);
            }
        }
        return goldsDeck;
    }

    private GameDeck<Objective> createObjectivesDeck(int size) {
        GameDeck<Objective> objectivesDeck = new GameDeck<Objective>();
        for (int i = 0; i < size; i++) {
            objectivesDeck.add(generateRandomObjective());
        }
        return objectivesDeck;
    }

    private GameDeck<ResourceCard> createResourcesDeck(int size) {
        GameDeck<ResourceCard> resourcesDeck = new GameDeck<ResourceCard>();
        for (int i = 0; i < size; i++) {
            resourcesDeck.add(generateRandomResourceCard());
        }
        return resourcesDeck;
    }

    private GameDeck<GoldCard> createGoldsDeck(int size) {
        GameDeck<GoldCard> goldsDeck = new GameDeck<GoldCard>();
        for (int i = 0; i < size; i++) {
            goldsDeck.add(generateRandomGoldCard());
        }
        return goldsDeck;
    }

    private GameDeck<InitialCard> createInitialsDeck(int size) {
        GameDeck<InitialCard> initialsDeck = new GameDeck<InitialCard>();
        for (int i = 0; i < size; i++) {
            initialsDeck.add(generateRandomInitialCard());
        }
        return initialsDeck;
    }

    protected static ResourceCard generateRandomResourceCard() {
        EnumSet<Symbol> reigns = Symbol.getReigns();
        EnumSet<Symbol> corners = Symbol.getValidCorner();
        try {
            return new ResourceCard(
                    new CardFace(randomSymbol(corners), randomSymbol(corners), randomSymbol(corners),
                            randomSymbol(corners), Collections.emptySet()),
                    randomSymbol(reigns),
                    (int) Math.random() * 2);
        } catch (InvalidResourceException e) {
            throw new RuntimeException();
        }
    }

    protected static GoldCard generateRandomGoldCard() {
        EnumSet<Symbol> reigns = Symbol.getReigns();
        EnumSet<Symbol> corners = Symbol.getValidCorner();
        EnumSet<Symbol> multipliers = Symbol.getValidMultiplier();
        EnumSet<Symbol> resources = Symbol.getBasicResources();
        try {
            return new GoldCard(
                    new CardFace(randomSymbol(corners), randomSymbol(corners), randomSymbol(corners),
                            randomSymbol(corners),
                            Collections.emptySet()),
                    randomSymbol(reigns),
                    randomSymbol(multipliers),
                    (int) Math.random() * 2,
                    new QuantityRequirement(Map.of(randomSymbol(resources), 1)));
        } catch (InvalidResourceException e) {
            throw new RuntimeException();
        }
    }

    public static InitialCard generateRandomInitialCard() {
        EnumSet<Symbol> reigns = Symbol.getReigns();
        // Generate a random number between 0 and 3
        int index = (int) (Math.random() * 3);

        return new InitialCard(
                new CardFace(randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns),
                        Set.of(randomSymbol(reigns))),
                new CardFace(randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns),
                        Set.of(randomSymbol(reigns))));
    }

    public static Objective generateRandomObjective() {
        EnumSet<Symbol> resources = Symbol.getBasicResources();
        try {
            return new Objective((int) (Math.random() * 2),
                    new QuantityRequirement(Map.of(randomSymbol(resources), 3)));
        } catch (InvalidResourceException e) {
            throw new RuntimeErrorException(null);
        }
    }

    public static Symbol randomSymbol(EnumSet<Symbol> validSymbols) {
        int size = validSymbols.size();
        // Generate a random number between 0 and 3
        int index = (int) (Math.random() * size);
        int i = 0;
        for (Symbol element : validSymbols) {
            if (i == index) {
                return element;
            }
            i++;
        }
        return null;
    }

    private void initializeBlankMatch(int maxPlayers, int initialsNum, int objectivesNum, int resourcesNum, int goldsNum) {
        // Set up a basic game
        String names[] = {"Oingo", "Boingo", "Jotaro", "Polnareff"};
        Player players[] = new Player[4];

        initializeAllEqualDecks(initialsNum, objectivesNum, resourcesNum, goldsNum);

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

    private void initializeBlankMatch(int maxPlayers) {
        initializeBlankMatch(maxPlayers, 4, 10, 40, 40);
    }

    private void initializeBlankStartedMatch(int maxPlayers, int initialsNum, int objectivesNum, int resourcesNum, int goldsNum) {
        // Set up a basic game
        initializeBlankMatch(maxPlayers, initialsNum, objectivesNum, resourcesNum, goldsNum);

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
    }

    private void initializeBlankStartedMatch(int maxPlayers) {
        initializeBlankStartedMatch(maxPlayers, 4, 10, 40, 40);
    }
}
