package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.utils.Pair;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for class GoldCard.
 */
public class GoldCardTest
{
    Board board;
    InitialCard initialCard;
    PlayableCard goldAutopoint;
    PlayableCard goldCovering;
    PlayableCard goldNoPoints;
    PlayableCard goldPositioning;

    Pair<Integer, Integer> coordAutopoint;
    Pair<Integer, Integer> coordCovering;
    Pair<Integer, Integer> coordNoPoints;
    Pair<Integer, Integer> coordPositioning;

    /**
    * Unit test for method calculatePoints.
    */
    public GoldCardTest(){
        board = new Board(); // created empty board

        try{
            // map of symbols for the center of the card
            Map<Symbol, Integer> mapGoldAutopoint = Map.of(Symbol.PLANT, 1);
            Map<Symbol, Integer> mapGoldCovering = Map.of(Symbol.INKWELL, 1);
            Map<Symbol, Integer> mapGoldNoPoints_Positioning = Map.of(Symbol.FUNGUS, 1);

            // coordinates for each card to be placed
            coordAutopoint = new Pair<Integer,Integer>(1, -1);
            coordCovering = new Pair<Integer,Integer>(2, -2);
            coordNoPoints = new Pair<Integer,Integer>(1, 1);
            coordPositioning = new Pair<Integer,Integer>(2, 0);

            // creation of the cards
            initialCard = new InitialCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.PLANT, Set.of(Symbol.FUNGUS)),
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.PLANT, Set.of(Symbol.FUNGUS))
            );

            goldAutopoint = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.INKWELL, Set.of(Symbol.INSECT)),
                    Symbol.INSECT, Symbol.INKWELL, 1, new QuantityRequirement(mapGoldAutopoint)
            );

            goldCovering = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.INSECT)),
                    Symbol.INSECT, Symbol.INKWELL, 2, new QuantityRequirement(mapGoldCovering)
            );

            goldNoPoints = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.INSECT)),
                    Symbol.INSECT, Symbol.ANIMAL, 3, new QuantityRequirement(mapGoldNoPoints_Positioning)
            );

            goldPositioning = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.INSECT)),
                    Symbol.INSECT, Symbol.CORNER_OBJ, 2, new QuantityRequirement(mapGoldNoPoints_Positioning)
            );

            // placement phase: initial card
            board.setInitialCard(initialCard, Side.FRONT);

        } catch (Exception e) {
            System.err.println(e);
            assertTrue(false);
        }
    }

    @Test
    public void verifyPointCalculation(){
        try{
            assertEquals(1, board.placeCard(coordAutopoint, goldAutopoint, Side.FRONT, 0));
            assertEquals(0, board.placeCard(coordCovering, goldCovering, Side.FRONT, 0));
            assertEquals(0, board.placeCard(coordNoPoints, goldNoPoints, Side.FRONT, 0));
            assertEquals(4, board.placeCard(coordPositioning, goldPositioning, Side.FRONT, 0));
        } catch (Exception e) {
            System.err.println(e);
            assertTrue(false);
        }
    }


/* info:
    // initial card has full corners, a plant in the bottom right and a fungus in the center, for both front and back

    // goldAutopoint contains its multiplier on one corner
        // created gold card with full corners, an inkwell on the bottom right and an insect in the center of the back
        // 1 point, multiplier is inkwell, requires 1 plant

    // goldCovering placement covers the only corner containing the multiplier
        // created gold card with full corners and an insect in the center of the back
        // 2 points, multiplier is inkwell, requires 1 inkwell

    // goldNoPoints has no multipliers on board
        // created gold card with full corners and an insect in the center of the back
        // 2 points, multiplier is animal, requires 1 fungus

    // goldPositioning covers 2 edges
        // created gold card with full corners and an insect in the center of the back
        // 2 points, multiplier is corner_object, requires 1 fungus
*/
}