package it.polimi.ingsw.gamemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.util.Map;
import java.util.Set;


import it.polimi.ingsw.utils.*;

public class PositionRequirementTest {
    Board board;
    PlayableCard plant;
    PlayableCard insect;
    PlayableCard fungus;

    public PositionRequirementTest() {
        board = new Board();

        try {
            plant = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of()),
                Symbol.PLANT, 0
            );
            insect = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of()),
                Symbol.INSECT, 0
            );

            fungus = new ResourceCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of()),
                Symbol.FUNGUS, 0
            );

            board.setInitialCard(new InitialCard(
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of()),
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of())
            ), Side.FRONT);

            board.placeCard(new Pair<>(1, -1), insect, Side.FRONT, 0);

            board.placeCard(new Pair<>(1, 1), insect, Side.FRONT, 0);
            board.placeCard(new Pair<>(1, 3), insect, Side.FRONT, 0);
            board.placeCard(new Pair<>(2, 4), plant, Side.FRONT, 0);

            board.placeCard(new Pair<>(2, 2), insect, Side.FRONT, 0);

            board.placeCard(new Pair<>(1, 5), insect, Side.FRONT, 0);
            board.placeCard(new Pair<>(2, 6), plant, Side.FRONT, 0);

            board.placeCard(new Pair<>(3, 1), insect, Side.FRONT, 0);

            board.placeCard(new Pair<>(3, 5), plant, Side.FRONT, 0);
            board.placeCard(new Pair<>(3, 7), plant, Side.FRONT, 0);
            board.placeCard(new Pair<>(4, 8), plant, Side.FRONT, 0);
            board.placeCard(new Pair<>(5, 9), plant, Side.FRONT, 0);

       } catch (Exception e) {
            System.err.println(e);
            assertTrue(false);
        }
    }

    @Test
    public void verifyPositionReqs() {
        try {
            assertEquals(1, new PositionRequirement(
                Map.of(
                    new Pair<>(0, 0), Symbol.INSECT,
                    new Pair<>(0, 2), Symbol.INSECT,
                    new Pair<>(1, 3), Symbol.PLANT
                )
            ).timesMet(board));

            assertEquals(0, new PositionRequirement(
                Map.of(
                    new Pair<>(0, 0), Symbol.PLANT,
                    new Pair<>(0, 2), Symbol.INSECT,
                    new Pair<>(1, 3), Symbol.INSECT
                )
            ).timesMet(board));

            assertEquals(1, new PositionRequirement(
                Map.of(
                    new Pair<>(0, 0), Symbol.PLANT,
                    new Pair<>(1, 1), Symbol.PLANT,
                    new Pair<>(2, 2), Symbol.PLANT
                )
            ).timesMet(board));

            assertEquals(1, new PositionRequirement(
                Map.of(
                    new Pair<>(0, 0), Symbol.INSECT,
                    new Pair<>(0, 2), Symbol.INSECT,
                    new Pair<>(1, 3), Symbol.INSECT
                )
            ).timesMet(board));

            assertEquals(1, new PositionRequirement(
                Map.of(
                    new Pair<>(0, 0), Symbol.INSECT,
                    new Pair<>(1, -1), Symbol.INSECT,
                    new Pair<>(2, -2), Symbol.INSECT
                )
            ).timesMet(board));
        } catch (Exception e) {
            System.err.println(e);
            assertTrue(false);
        }
    }
}
