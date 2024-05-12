package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.utils.Pair;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * The front side of these cards always gives points, but needs a certain requirement to be met in order to be played
 *
 * @see CardFace
 */
public final class GoldCard extends PlayableCard {
    private final Symbol multiplier;
    private final QuantityRequirement req;

    /**
     * Class constructor. It needs to only take the front as an argument, since the back is handled by its superclass {@link PlayableCard}
     *
     * @param front      the front side of the card
     * @param reign      the reign of the card
     * @param multiplier the symbol whose number of resources multiplies the points parameter
     * @param points     the number every resource of the given type is worth
     * @param req        the requirement that must be met in order to be able to play the card
     * @throws InvalidResourceException if the passed resource is not in {@link Symbol#getReigns()}
     */
    public GoldCard(CardFace front, Symbol reign, Symbol multiplier, int points, QuantityRequirement req) throws InvalidResourceException {
        super(reign);
        this.front = front;
        this.points = points;
        this.req = req; // integrity check already provided in the constructor of QuantityRequirement

        // integrity check for allowed multipliers
        EnumSet<Symbol> validMultiplier = Symbol.getValidMultiplier();
        if (!validMultiplier.contains(multiplier)) {
            throw new InvalidResourceException("Resource " + multiplier.toString() + " is not valid for a " + this.getClass());
        }
        this.multiplier = multiplier;
    }

    /**
     * Getter for the GoldCard class
     *
     * @return the multiplier
     */
    public Symbol getMultiplier() {
        return this.multiplier;
    }

    /**
     * Getter for the GoldCard class
     *
     * @return the quantity requirement for the gold card to be played
     */
    public QuantityRequirement getRequirement() {
        return this.req;
    }

    /**
     * Getter for the GoldCard class
     *
     * @return points held by the card
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Will compute the total points this card gives based on the board it's played on.
     * It MUST be called AFTER the placement of the gold card
     *
     * @param board the board on which we want to compute the points this card will give
     * @param coord the coordinates of the card just placed (needed fot corner objectives)
     * @return the points gained from playing the gold card
     */
    public int calculatePoints(Board board, Pair<Integer, Integer> coord) {
        if (this.multiplier == Symbol.NO_MULT) {
            return this.points;
        }
        Map<Symbol, Integer> availableResources = board.getAvailableResources();

        int totalElements = 0;

        // multiplier is basic resource (subset of symbols)
        if (Symbol.getBasicResources().contains(this.multiplier)) {

            for (Symbol s : availableResources.keySet()) {
                if (s.equals(this.multiplier)) {
                    totalElements = availableResources.get(s);
                }
            }
        } else if (this.multiplier.equals(Symbol.CORNER_OBJ)) { //multiplier is a corner_objective kind

            // Pair<Integer, Integer> currentCoord = board.getCoordinatesPlacedCard();
            Set<Pair<Integer, Integer>> edges = getEdges(coord);

            Map<Pair<Integer, Integer>, PlacedCard> map = board.getPlacedCards();
            for (Pair<Integer, Integer> p : edges) {

                // check if the board has a value (card) associated to the key (coordinates)
                if (map.get(p) != null) {
                    totalElements++;
                }
            }
        }

        return totalElements * this.points;
    }

    private static Set<Pair<Integer, Integer>> getEdges(Pair<Integer, Integer> currentCoord) {
        Pair<Integer, Integer> tr = new Pair<>(currentCoord.first() + 1, currentCoord.second() + 1);
        Pair<Integer, Integer> br = new Pair<>(currentCoord.first() + 1, currentCoord.second() - 1);
        Pair<Integer, Integer> tl = new Pair<>(currentCoord.first() - 1, currentCoord.second() - 1);
        Pair<Integer, Integer> bl = new Pair<>(currentCoord.first() - 1, currentCoord.second() + 1);

        Set<Pair<Integer, Integer>> edges = new HashSet<>();
        edges.add(tr);
        edges.add(br);
        edges.add(tl);
        edges.add(bl);
        return edges;
    }
}
