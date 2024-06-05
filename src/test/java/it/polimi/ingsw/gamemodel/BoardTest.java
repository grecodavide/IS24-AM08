package it.polimi.ingsw.gamemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Map;
import java.util.Set;


import it.polimi.ingsw.utils.*;

public class BoardTest {
        InitialCard initial;
        PlayableCard base;
        PlayableCard validGold;
        PlayableCard invalidGold;
        PlayableCard brempty;

        PlayableCard nores;

        Board board;
    public BoardTest() {
        board = new Board();

        try {
            initial = new InitialCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.PLANT)),
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.PLANT))
            );

            base = new ResourceCard(
                new CardFace(Symbol.PLANT, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.PLANT)),
                Symbol.PLANT, 1
            );

            brempty = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER, Set.of()),
                Symbol.PLANT, 1
            );

            nores = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of()),
                Symbol.PLANT, 1
            );

            Map<Symbol, Integer> valid = Map.of(Symbol.PLANT, 5);

            Map<Symbol, Integer> invalid = Map.of(Symbol.FUNGUS, 2);

            validGold = new GoldCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER, Set.of(Symbol.PLANT)),
                Symbol.PLANT, Symbol.INKWELL, 2, new QuantityRequirement(valid)
            );

            invalidGold = new GoldCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER, Set.of(Symbol.PLANT)),
                Symbol.PLANT, Symbol.INKWELL, 2, new QuantityRequirement(invalid)
            );

            board.setInitialCard(initial, Side.FRONT); // +1 plant
            board.placeCard(new Pair<>(1,1),    brempty,    Side.FRONT, 0); // +0 plant
            board.placeCard(new Pair<>(-1,-1),  base,       Side.FRONT, 0); // +2 plant
            board.placeCard(new Pair<>(0,-2),   base,       Side.FRONT, 0); // +2 plant
            board.placeCard(new Pair<>(1,-3),   base,       Side.FRONT, 0); // +2 plant
            board.placeCard(new Pair<>(2,-2),   base,       Side.FRONT, 0); // +2 plant
            board.placeCard(new Pair<>(3,-1),   base,       Side.FRONT, 0); // +2 plant
            board.placeCard(new Pair<>(-2,0),   base,       Side.FRONT, 0); // +2 plant -1 plant

            board.placeCard(new Pair<>(0,-4),   nores,      Side.FRONT, 0); // +0
            board.placeCard(new Pair<>(-1,-5),  nores,      Side.FRONT, 0); // +0
            board.placeCard(new Pair<>(0,-6),   nores,      Side.FRONT, 0); // +0
            board.placeCard(new Pair<>(1,-7),   nores,      Side.FRONT, 0); // +0
            board.placeCard(new Pair<>(2,-6),   nores,      Side.FRONT, 0); // +0
            board.placeCard(new Pair<>(3,-5),   nores,      Side.FRONT, 0); // +0
            board.placeCard(new Pair<>(2,-4),   nores,      Side.FRONT, 0); // +0

            board.addHandCard(base);
            board.addHandCard(brempty);
            board.addHandCard(validGold);

            verifyResources(Symbol.PLANT, 12);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void verifyBasicPlacement() {
        try {
            // (0, 0) is reserved to initial
            assertEquals(PlacementOutcome.INVALID_COORDS,           board.verifyCardPlacement(new Pair<>(0, 0),         base, Side.FRONT));
            // (1, 1) is already occupied
            assertEquals(PlacementOutcome.INVALID_COORDS,           board.verifyCardPlacement(new Pair<>(0, 0),         base, Side.FRONT));
            // (2, 0) is invalid since in (1, 1) there is a bottom right corner empty
            assertEquals(PlacementOutcome.INVALID_COORDS,           board.verifyCardPlacement(new Pair<>(2, 0),         base, Side.FRONT));
            // (1, -1) is a valid placement (covers 4 corners)
            assertEquals(PlacementOutcome.VALID,                    board.verifyCardPlacement(new Pair<>(1, -1),        base, Side.FRONT));
            // (1, -5) is a valid placement
            assertEquals(PlacementOutcome.VALID,                    board.verifyCardPlacement(new Pair<>(1, -5),        base, Side.FRONT));
            // no adjacent
            assertEquals(PlacementOutcome.INVALID_COORDS,           board.verifyCardPlacement(new Pair<>(5, 5),         base, Side.FRONT));
            // enough res
            assertEquals(PlacementOutcome.VALID,                    board.verifyCardPlacement(new Pair<>(1, -5),        validGold, Side.FRONT));

            board.removeHandCard(base);
            board.addHandCard(invalidGold);
            // not enough res
            assertEquals(PlacementOutcome.INVALID_ENOUGH_RESOURCES, board.verifyCardPlacement(new Pair<>(1, -5),        invalidGold, Side.FRONT));
            board.removeHandCard(invalidGold);
            board.addHandCard(base);

            board.verifyCardPlacement(new Pair<>(1, -5), invalidGold, Side.FRONT);
        } catch (Exception e) {
            assertEquals("The card is not in the player's hand!", e.getMessage());
        }
    }

    public void verifyResources(Symbol s, Integer expected) {
        assertEquals(expected, board.getAvailableResources().get(s));
    }
}
