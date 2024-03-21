package it.polimi.ingsw.gamemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.*;

/**
 * Unit test for simple App.
 */
public class BoardTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        Board board = new Board();


        try {

            InitialCard initial = new InitialCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.PLANT)),
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.PLANT))
            );

            PlayableCard init = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.PLANT)),
                Symbol.PLANT, 1
            );

            PlayableCard empty = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER, Set.of(Symbol.PLANT)),
                Symbol.PLANT, 1
            );
            Map<Symbol, Integer> m = new HashMap<Symbol, Integer>();
            m.put(Symbol.PLANT, 2);

            PlayableCard gold = new GoldCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER, Set.of(Symbol.PLANT)),
                Symbol.PLANT, Symbol.INKWELL, 2, new QuantityRequirement(m)
            );

            board.addHandCard(init);
            board.addHandCard(empty);
            board.addHandCard(gold);
            assertEquals(board.verifyCardPlacement(new Pair<>(0, 0), initial, Side.FRONT), PlacementOutcome.INVALID_COORDS);
            board.setInitialCard(initial);
            board.placeCard(new Pair<Integer,Integer>(1, 1), init, Side.FRONT, 0);
            board.placeCard(new Pair<Integer,Integer>(2, 0), empty, Side.FRONT, 0);
            board.placeCard(new Pair<Integer,Integer>(-1, -1), init, Side.FRONT, 0);
            board.placeCard(new Pair<Integer,Integer>(0, -2), init, Side.FRONT, 0);
            board.placeCard(new Pair<Integer,Integer>(1, -3), init, Side.FRONT, 0);
            board.placeCard(new Pair<Integer,Integer>(2, -2), init, Side.FRONT, 0);
            assertEquals(board.verifyCardPlacement(new Pair<>(3, -1), init, Side.FRONT), PlacementOutcome.INVALID_COORDS);
            assertEquals(board.verifyCardPlacement(new Pair<>(-1, -1), init, Side.FRONT), PlacementOutcome.INVALID_COORDS);
            assertEquals(board.verifyCardPlacement(new Pair<>(0, -1), init, Side.FRONT), PlacementOutcome.INVALID_COORDS);
            assertEquals(board.verifyCardPlacement(new Pair<>(2, 2), empty, Side.FRONT), PlacementOutcome.VALID);
            assertEquals(board.verifyCardPlacement(new Pair<>(2, 2), gold, Side.FRONT), PlacementOutcome.INVALID_ENOUGH_RESOURCES);

        } catch (Exception e) {
            System.err.println(e);
            assertTrue(false); // not expecting any exception for now, if exception was expected remove this and adapt
        }
    }
}
