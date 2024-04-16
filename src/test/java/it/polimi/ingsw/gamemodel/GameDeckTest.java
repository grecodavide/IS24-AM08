package it.polimi.ingsw.gamemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Set;

public class GameDeckTest {
    GameDeck<ResourceCard> deck;
    ResourceCard base; // sadge, no PlayableCard

    public GameDeckTest() {
        try {
            deck = new GameDeck<>();
            base = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of()),
                Symbol.PLANT, 0
            );

            deck.add(base);
            deck.add(base);
        } catch (Exception e) {
            System.err.println(e);
            assertTrue(false);
        }
    }

    @Test
    public void testAll() {
        try {
            assertEquals(2, deck.getSize());
            assertEquals(base, deck.pop());
            assertEquals(base, deck.poll());
            assertEquals(0, deck.getSize());
            assertEquals(null, deck.poll());
            deck.pop();
        } catch (Exception e) {
            assertEquals("Tried to draw from an empty deck!", e.getMessage());
        }
    }
}
