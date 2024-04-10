package it.polimi.ingsw.gamemodel;

import java.security.spec.ECField;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;

import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.utils.Pair;
import org.junit.Test;

import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.exceptions.WrongStateException;

import static org.junit.Assert.*;

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

        // Verify that Match throws an exception when the initial cards deck has less
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
            match.setState(new FinalState(match));
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
        catch (IllegalArgumentException e) {}
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
        } catch (WrongStateException e) {
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

        // Test in waitstate
        try {
            match.addPlayer(player1);
            match.addPlayer(player2);
            match.removePlayer(player1);
            assertFalse("Player not removed", match.getPlayers().contains(player1));
            match.addPlayer(player1);
            match.addPlayer(player3);
            match.addPlayer(player4);
        } catch (Exception e) {
            fail("No exception must be thrown, exception thrown:" + e.getMessage());
        }
        assertTrue("Match not in correct state", match.getCurrentState() instanceof NextTurnState);
        // Test outside of waitstate
        try {
            match.removePlayer(player1);
        } catch (Exception e) {
            fail("No exception must be thrown, exception thrown:" + e.getMessage());
        }
        assertFalse("Player was not removed", match.getPlayers().contains(player1));
        // TODO end match
        //assertTrue("Match has not ended after player quit in game", match.isFinished());
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
        match.setState(new FinalState(match));

        try {
            match.drawInitialCard();
            // An exception is supposed to be thrown here
            fail("drawInitialCard called in wrong state and has not given an exception");
        } catch (WrongStateException e) {}
    }

    @Test
    public void setInitialSide() {
        // Setup a basic game
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
                match.setInitialSide(side);
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
            match.makeMove(new Pair<>(1, 4), match.getCurrentPlayer().getBoard().getCurrentHand().get(0), Side.FRONT);
            fail("Should have thrown a WrongChoiceException");
        } catch (WrongChoiceException e) {
            // Good
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Try placing that is not in the player hand
        try {
            match.makeMove(new Pair<>(1, 1), generateRandomResourceCard(), Side.FRONT);
            fail("Should have thrown CardException");
        } catch (CardException e) {
            // Good
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Try placing the card in the right position
        PlayableCard card = match.getCurrentPlayer().getBoard().getCurrentHand().get(0);
        try {
            match.makeMove(new Pair<>(1, 1), card, Side.BACK);
        } catch (Exception e) {
            fail("Exception thrown when no exception expected");
        }

        assertEquals("Card not placed", card, match.getCurrentPlayer().getBoard().getPlacedCards().get(new Pair<>(1,1)).getCard());
        assertEquals("Card not removed from hand", 2, match.getCurrentPlayer().getBoard().getCurrentHand().size());

        assertTrue("State not changed after makeMove", match.getCurrentState() instanceof AfterMoveState);

    }

    private void initializeAllEqualDecks() {
        // Initialize Initial Cards deck
        initialsDeck = createDeterministicInitialsDeck(4);
        objectivesDeck = createDeterministicObjectivesDeck(10);
        goldsDeck = createGoldsDeck(40);
        resourcesDeck = createResourcesDeck(40);
    }

    private static GameDeck<Objective> createDeterministicObjectivesDeck(int size) {
        // Initialize Objective deck
        GameDeck<Objective> objectivesDeck = new GameDeck<>();
        Objective objective = null;
        HashMap<Symbol, Integer> resources = new HashMap<>();
        resources.put(Symbol.FUNGUS, 2);
        try {
            Requirement req = new QuantityRequirement(resources);
        } catch (Exception e) {
            throw new RuntimeException();
        }
        for (int i = 0; i < size; i++) {
            objective = new Objective(2, null);
            objectivesDeck.add(objective);
        }
        return objectivesDeck;
    }

    private static GameDeck<InitialCard> createDeterministicInitialsDeck(int size) {
        // Initialize Initial Cards deck
        GameDeck<InitialCard> initialsDeck = new GameDeck<InitialCard>();
        InitialCard card = null;
        for (int i = 0; i < size; i++) {
            card = new InitialCard(
                    new CardFace(Symbol.FUNGUS, Symbol.ANIMAL, Symbol.PLANT, Symbol.INSECT, Collections.emptySet()),
                    new CardFace(Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER,
                            Collections.emptySet()));
            initialsDeck.add(card);
        }
        return initialsDeck;
    }

    private static GameDeck<ResourceCard> createDeterministicResourcesDeck(int size) {
        GameDeck<ResourceCard> resourcesDeck = new GameDeck<>();
        ResourceCard card = null;
        for (int i = 0; i < size; i++) {
            try {
                card = new ResourceCard(
                        new CardFace(Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER,
                                Collections.emptySet()),
                        Symbol.FUNGUS, 0);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
        return resourcesDeck;
    }

    private static GameDeck<GoldCard> createDeterministicGoldsDeck(int size) {
        GameDeck<GoldCard> goldsDeck = new GameDeck<>();
        GoldCard card = null;
        HashMap<Symbol, Integer> resources = new HashMap<>();
        resources.put(Symbol.FUNGUS, 1);
        for (int i = 0; i < size; i++) {
            try {
                card = new GoldCard(new CardFace(Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER,
                        Symbol.EMPTY_CORNER, Collections.emptySet()), Symbol.FUNGUS, Symbol.FEATHER, 3,
                        new QuantityRequirement(resources));
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

    private ResourceCard generateRandomResourceCard() {
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

    private GoldCard generateRandomGoldCard() {
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

    private InitialCard generateRandomInitialCard() {
        EnumSet<Symbol> reigns = Symbol.getReigns();
        // Generate a random number between 0 and 3
        int index = (int) (Math.random() * 3);

        return new InitialCard(
                new CardFace(randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns),
                        Set.of(randomSymbol(reigns))),
                new CardFace(randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns),
                        Set.of(randomSymbol(reigns))));
    }

    private Objective generateRandomObjective() {
        EnumSet<Symbol> resources = Symbol.getBasicResources();
        try {
            return new Objective((int) (Math.random() * 2),
                    new QuantityRequirement(Map.of(randomSymbol(resources), 3)));
        } catch (InvalidResourceException e) {
            throw new RuntimeErrorException(null);
        }
    }

    private Symbol randomSymbol(EnumSet<Symbol> validSymbols) {
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

    private void initializeBlankMatch(int maxPlayers) {
        // Setup a basic game
        String names[] = {"Oingo", "Boingo", "Jotaro", "Polnareff"};
        Player players[] = new Player[4];

        initializeAllEqualDecks();

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

    private void initializeBlankStartedMatch(int maxPlayers) {
        // Setup a basic game
        initializeBlankMatch(maxPlayers);

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
}
