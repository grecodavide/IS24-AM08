package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.utils.Pair;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for class GoldCard.
 */
public class GoldCardTest
{
    /**
     * Unit test for method calculatePoints.
     */
    @Test
    public void testGoldCard(){
        Board board = new Board(); // created empty board

        try{
            // created initial card with full corners, a plant in the bottom right and a fungus in the center, for both front and back
            InitialCard initial_alpha = new InitialCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.PLANT, Set.of(Symbol.FUNGUS)),
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.PLANT, Set.of(Symbol.FUNGUS))
            );

            // created gold card with full corners, an inkwell on the bottom right and an insect in the center of the back
            // 1 point, multiplier is inkwell, requires 1 plant
            Map<Symbol, Integer> m1 = new HashMap<Symbol, Integer>();  m1.put(Symbol.PLANT, 1);
            PlayableCard gold_beta = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.INKWELL, Set.of(Symbol.INSECT)),
                    Symbol.INSECT,
                    Symbol.INKWELL,
                    1,
                    new QuantityRequirement(m1)

            );

            // created gold card with full corners and an insect in the center of the back
            // 2 points, multiplier is inkwell, requires 1 inkwell
            Map<Symbol, Integer> m2 = new HashMap<Symbol, Integer>();  m2.put(Symbol.INKWELL, 1);
            PlayableCard gold_gamma = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.INSECT)),
                    Symbol.INSECT,
                    Symbol.INKWELL,
                    2,
                    new QuantityRequirement(m2)
            );

            // created gold card with full corners and an insect in the center of the back
            // 2 points, multiplier is animal, requires 1 fungus
            Map<Symbol, Integer> m3 = new HashMap<Symbol, Integer>();  m3.put(Symbol.FUNGUS, 1);
            PlayableCard gold_delta = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.INSECT)),
                    Symbol.INSECT,
                    Symbol.ANIMAL,
                    3,
                    new QuantityRequirement(m3)
            );

            // created gold card with full corners and an insect in the center of the back
            // 2 points, multiplier is corner_object, requires 1 fungus
            PlayableCard gold_epsilon = new GoldCard(
                    new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Set.of(Symbol.INSECT)),
                    Symbol.INSECT,
                    Symbol.CORNER_OBJ,
                    2,
                    new QuantityRequirement(m3)
            );

            // placement phase: initial card and the four gold cards
            board.setInitialCard(initial_alpha, Side.FRONT);

            // case auto-point: gold_beta contains its multiplier on one corner
            Pair<Integer, Integer> coord_beta = new Pair<Integer,Integer>(-1, 1);
            board.placeCard(coord_beta, gold_beta, Side.FRONT, 0);
            assertEquals(1, ((GoldCard) gold_beta).calculatePoints(board, coord_beta));

            // case self-sabotage: gold_gamma placement covers the only corner containing the multiplier
            Pair<Integer, Integer> coord_gamma = new Pair<Integer,Integer>(-2, 2);
            board.placeCard(coord_gamma, gold_gamma, Side.FRONT, 0);
            assertEquals(0, ((GoldCard) gold_gamma).calculatePoints(board, coord_gamma));

            // case normal-none: there are no multipliers on board
            Pair<Integer, Integer> coord_delta = new Pair<Integer,Integer>(1, 1);
            board.placeCard(coord_delta, gold_delta, Side.FRONT, 0);
            assertEquals(0, ((GoldCard) gold_delta).calculatePoints(board, coord_delta));

            // case positioning: gold_epsilon covers 2 edges
            Pair<Integer, Integer> coord_epsilon = new Pair<Integer,Integer>(0, 2);
            board.placeCard(coord_epsilon, gold_epsilon, Side.FRONT, 0);
            assertEquals(4, ((GoldCard) gold_epsilon).calculatePoints(board, coord_epsilon));

        } catch (Exception e) {
            System.err.println(e);
            // fail("Error: " + e.getMessage());
        }
    }


}