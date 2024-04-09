package it.polimi.ingsw.gamemodel;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.RuntimeErrorException;

import it.polimi.ingsw.utils.Pair;
import org.junit.Test;

import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.exceptions.WrongStateException;

import static org.junit.Assert.*;

public class MatchTest {

    private Match match;
    private Player player1, player2, player3, player4;
    GameDeck<InitialCard> initialsDeck;
    GameDeck<ResourceCard> resourcesDeck;
    GameDeck<GoldCard> goldsDeck;
    GameDeck<Objective> objectivesDeck;

    @Test
    public void shouldAnswerWithTrue() {
        GameDeck<InitialCard> initialsDeck = createInitialsDeck(4);
        GameDeck<ResourceCard> resourcesDeck = createResourcesDeck(40);
        GameDeck<GoldCard> goldsDeck = createGoldsDeck(40);
        GameDeck<Objective> objectivesDeck = createObjectivesDeck(16);

        match = new Match(2, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
        player1 = new Player("Oingo", match);
        player2 = new Player("Boingo", match);
        player3 = new Player("Foingo", match);

        try {
            match.addPlayer(player1);
            match.addPlayer(player2);
        } catch (Exception e) {
            fail("Players not added correctly");
        }
        assertTrue("Match should have been full", match.isFull());
        assertTrue("Wrong state: " + match.getCurrentState(), match.getCurrentState() instanceof NextTurnState);
        try {
            match.addPlayer(player3);
            fail("Should have thrown an exception");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        for (int i = 0; i < match.getMaxPlayers(); i++) {
            try {
                System.out.println("Player: " + match.getCurrentPlayer().getNickname() + "Chooses the initial card");
                InitialCard initial = match.getCurrentPlayer().drawInitialCard();
                assertTrue("Wrong state: " + match.getCurrentState(),
                        match.getCurrentState() instanceof ChooseInitialSideState);
                match.getCurrentPlayer().chooseInitialCardSide(Side.FRONT);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        }
        assertFalse("Match should not have started", match.isStarted());

        for (int i = 0; i < match.getMaxPlayers(); i++) {
            try {
                System.out.println("Player: " + match.getCurrentPlayer().getNickname() + "Chooses their objective");
                Pair<Objective, Objective> objectives = match.getCurrentPlayer().drawSecretObjectives();
                assertTrue("Wrong state: " + match.getCurrentState(),
                        match.getCurrentState() instanceof ChooseSecretObjectiveState);
                match.getCurrentPlayer().chooseSecretObjective(objectives.first());
            } catch (Exception e) {
                fail("Player " + i + e.getMessage());
            }
        }
        assertTrue("Wrong state: " + match.getCurrentState(), match.getCurrentState() instanceof NextTurnState);
        assertTrue(true);
    }

    @Test
    public void constructor() {
        player1 = new Player("Oingo", match);
        player2 = new Player("Boingo", match);
        player3 = new Player("Foingo", match);
        player4 = new Player("Francesco Edoardo Caracciolo", match);

        // Verify that match throws an exception when the initial cards deck has less
        // cards than the number of players (3 cards in this case, minus than 4
        // players in the match)
        initializeAllEqualDecks();
        initialsDeck = createInitialsDeck(3);

        try {
            match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            fail("Match constructor: Exception about initials deck not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue("", true);
        }

        // Verify that match throws an exception when the gold cards deck has less
        // than 9 cards
        initializeAllEqualDecks();
        goldsDeck = createGoldsDeck(5);

        try {
            match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            fail("Match constructor: Exception about golds deck not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue("", true);
        }

        // Verify that match throws an exception when the initial cards deck has less
        // than 10 cards
        initializeAllEqualDecks();
        resourcesDeck = createResourcesDeck(9);

        try {
            match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            fail("Match constructor: Exception about resources deck not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue("", true);
        }

        // Verify that match throws an exception when the number of players is more or
        // less than expected
        initializeAllEqualDecks();
        objectivesDeck = createObjectivesDeck(5);

        try {
            match = new Match(1, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
            fail("Match constructor: Exception about players not thrown");
        } catch (IllegalArgumentException e) {
            assertTrue("", true);
        }
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
}
