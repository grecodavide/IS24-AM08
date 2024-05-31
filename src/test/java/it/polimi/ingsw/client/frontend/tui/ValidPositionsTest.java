package it.polimi.ingsw.client.frontend.tui;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Set;
import org.junit.Test;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

/**
 * ValidPositionsTest
 */

public class ValidPositionsTest {
    private Pair<Integer, Integer> createCoords(Integer first, Integer second) {
        return new Pair<>(first, second);
    }

    @Test
    public void testBasic() throws Exception {
        ValidPositions positions = new ValidPositions();

        Card initial = new InitialCard(
            new CardFace(
                Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of()
            ),
            null
        );

        Card emptyTR = new ResourceCard(
            new CardFace(
                Symbol.FULL_CORNER, 
                Symbol.EMPTY_CORNER, 
                Symbol.FULL_CORNER, 
                Symbol.FULL_CORNER,
                Set.of()
            ), Symbol.PLANT, 0);


        positions.addCard(new ShownCard(initial, Side.FRONT, createCoords(0, 0)));
        assertFalse("The cell in which a card is played should be not valid!", positions.isValid(createCoords(0, 0)));

        assertTrue("TR not marked as valid!", positions.isValid(createCoords(1, 1)));
        assertTrue("BL not marked as valid!", positions.isValid(createCoords(-1, -1)));
        assertTrue("TL not marked as valid!", positions.isValid(createCoords(-1, 1)));
        assertTrue("BR not marked as valid!", positions.isValid(createCoords(1, -1)));

        positions.addCard(new ShownCard(emptyTR, Side.FRONT, createCoords(1, 1)));
        assertFalse("The cell in which a card is played should be not valid!", positions.isValid(createCoords(1, 1)));

        assertFalse("TR marked as valid with empty corner!", positions.isValid(createCoords(2, 2)));
        assertTrue("BL not marked as valid!", positions.isValid(createCoords(0, 2)));
        assertTrue("BR not marked as valid!", positions.isValid(createCoords(2, 0)));

        positions.addCard(new ShownCard(emptyTR, Side.FRONT, createCoords(1, -1)));
        assertFalse("The cell in which a card is played should be not valid!", positions.isValid(createCoords(1, -1)));

        assertFalse("TR marked as valid with empty corner!", positions.isValid(createCoords(2, 0)));
        assertTrue("BR marked as not valid with corner!", positions.isValid(createCoords(2, -2)));
        assertTrue("BL marked as not valid with corner!", positions.isValid(createCoords(0, -2)));

    }
}
