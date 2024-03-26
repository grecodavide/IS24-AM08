package it.polimi.ingsw.gamemodel;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import it.polimi.ingsw.utils.Pair;
import org.junit.Test;

import it.polimi.ingsw.exceptions.WrongStateException;

import static org.junit.Assert.*;

public class MatchTest {

    @Test
    public void shouldAnswerWithTrue() {
        GameDeck<InitialCard> initialsDeck = createInitialsDeck();
        GameDeck<ResourceCard> resourcesDeck = createResourcesDeck();
        GameDeck<GoldCard> goldsDeck = createGoldsDeck();
        GameDeck<Objective> objectivesDeck = createObjectivesDeck();

        Match match = new Match(2, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
        Player p1 = new Player("Oingo", match);
        Player p2 = new Player("Boingo", match);
        Player p3 = new Player("Foingo", match);
        
        try {
            match.addPlayer(p1);
            match.addPlayer(p2); 
        } catch (WrongStateException e) {
            fail("Players not added correctly");
        }
        assertTrue("Match should have been full", match.isFull());
        assertTrue("Wrong state: " + match.getCurrentState(), match.getCurrentState() instanceof NextTurnState);
        try {
            match.addPlayer(p3);
            fail("Should have thrown an exception");
        } catch (WrongStateException e) {
            System.out.println(e.getMessage());
        }
        for (int i = 0; i < match.getMaxPlayers(); i++) {
            try {
                System.out.println("Player: " + match.getCurrentPlayer().getNickname() + "Chooses the initial card");
                InitialCard initial = match.getCurrentPlayer().drawInitialCard();
                assertTrue("Wrong state: " + match.getCurrentState(), match.getCurrentState() instanceof ChooseInitialSideState);
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
                assertTrue("Wrong state: " + match.getCurrentState(), match.getCurrentState() instanceof ChooseSecretObjectiveState);
                match.getCurrentPlayer().chooseSecretObjective(objectives.first());
            } catch (Exception e) {
                fail("Player " + i + e.getMessage());
            }
        }
        assertTrue("Wrong state: " + match.getCurrentState(), match.getCurrentState() instanceof NextTurnState);
        assertTrue( true );
    }
    
    private GameDeck<Objective> createObjectivesDeck() {
        GameDeck<Objective> objectivesDeck = new GameDeck<Objective>(8);
        for (int i = 0; i < 8; i++) {
            objectivesDeck.add(generateRandomObjective());
        }
        return objectivesDeck;
    }

    private GameDeck<ResourceCard> createResourcesDeck() {
        GameDeck<ResourceCard> resourcesDeck = new GameDeck<ResourceCard>(40);
        for (int i = 0; i < 40; i++) {
            resourcesDeck.add(generateRandomResourceCard());
        }
        return resourcesDeck;
    }

    private GameDeck<GoldCard> createGoldsDeck() {
        GameDeck<GoldCard> goldsDeck = new GameDeck<GoldCard>(40);
        for (int i = 0; i < 40; i++) {
            goldsDeck.add(generateRandomGoldCard());
        }
        return goldsDeck;
    }

    private GameDeck<InitialCard> createInitialsDeck() {
        GameDeck<InitialCard> initialsDeck = new GameDeck<InitialCard>(4);
        for (int i = 0; i < 4; i++) {
            initialsDeck.add(generateRandomInitialCard());
        }
        return initialsDeck;
    }

    private ResourceCard generateRandomResourceCard() {
        EnumSet<Symbol> reigns = Symbol.getReigns();
        EnumSet<Symbol> corners = Symbol.getValidCorner();
        return new ResourceCard(
            new CardFace(randomSymbol(corners), randomSymbol(corners), randomSymbol(corners), randomSymbol(corners), Set.of(randomSymbol(reigns))),
            new CardFace(Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Set.of(randomSymbol(reigns))),
            (int) Math.random() * 2
        );
    }

    private GoldCard generateRandomGoldCard() {
        EnumSet<Symbol> reigns = Symbol.getReigns();
        EnumSet<Symbol> corners = Symbol.getValidCorner();
        EnumSet<Symbol> multipliers = Symbol.getValidMultiplier();
        EnumSet<Symbol> resources = Symbol.getBasicResources();
        return new GoldCard(
            new CardFace(randomSymbol(corners), randomSymbol(corners), randomSymbol(corners), randomSymbol(corners), Set.of(randomSymbol(reigns))),
            new CardFace(Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER, Set.of(randomSymbol(reigns))),
            randomSymbol(multipliers),
            (int) Math.random() * 2,
            new QuantityRequirement(Map.of(randomSymbol(resources), 1))
        );
    } 
    
    private InitialCard generateRandomInitialCard() {
        EnumSet<Symbol> reigns = Symbol.getReigns(); 
        // Generate a random number between 0 and 3
        int index = (int) (Math.random() * 3);

        return new InitialCard(
            new CardFace(randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), Set.of(randomSymbol(reigns))),
            new CardFace(randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), randomSymbol(reigns), Set.of(randomSymbol(reigns)))
        );
    }

    private Objective generateRandomObjective() {
        EnumSet<Symbol> resources = Symbol.getBasicResources();
        return new Objective((int) (Math.random() * 2), new QuantityRequirement(Map.of(randomSymbol(resources), 3)));
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

