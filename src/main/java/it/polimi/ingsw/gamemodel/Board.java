package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.exceptions.HandException;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Board is the class that contains all the information relative to a {@link Player}'s status
 */
public class Board {
    private final List<PlayableCard> currentHand;
    private final Map<Pair<Integer, Integer>, PlacedCard> placed;
    private final Map<Symbol, Integer> availableResources;

    private static final Map<Pair<Integer, Integer>, Corner> diagonalOffsets = Map.of(
            new Pair<>(-1, +1), Corner.BOTTOM_RIGHT,
            new Pair<>(+1, +1), Corner.BOTTOM_LEFT,
            new Pair<>(-1, -1), Corner.TOP_RIGHT,
            new Pair<>(+1, -1), Corner.TOP_LEFT
    );

    /**
     * Class constructor. No inputs taken as the board starts empty
     */
    public Board() {
        currentHand = new ArrayList<>();
        placed = new HashMap<>();
        availableResources = new HashMap<>();
        for (Symbol s : Symbol.getBasicResources()) {
            availableResources.put(s, 0);
        }
    }

    /**
     * Getter for the total resources of a player
     *
     * @return the resources of a player
     */
    public Map<Symbol, Integer> getAvailableResources() {
        return this.availableResources;
    }

    /**
     * Getter for the board's placed cards
     *
     * @return map containing all the placed cards indexed by their coordinates
     */
    public Map<Pair<Integer, Integer>, PlacedCard> getPlacedCards() {
        return this.placed;
    }

    /**
     * Getter for the hand of the player (which must be composed of three {@link PlayableCard}), which is visible
     * to every player
     *
     * @return the player's hand
     */
    public List<PlayableCard> getCurrentHand() {
        return this.currentHand;
    }

    /**
     * Removes a card from the hand of the player
     *
     * @param card the card that must be removed from the player's hand
     * @throws HandException if the player does not have exactly 3 cards in his hand
     */
    protected void removeHandCard(PlayableCard card) throws HandException {
        if (currentHand.size() != 3) {
            throw new HandException("Tried to remove a card from an empty hand!");
        }
        currentHand.remove(card);
    }

    /**
     * Adds a card to the player's hand (which is visible to every player)
     *
     * @param card the card to put in the hand
     * @throws HandException if the player already has 3 cards
     */
    protected void addHandCard(PlayableCard card) throws HandException {
        if (currentHand.size() > 2) { // la mano ha 3 carte max
            throw new HandException("Tried to draw a card with a full hand!");
        }
        currentHand.addLast(card);
    }

    /**
     * Places the initial card in the (0, 0) coordinates, on the desired side
     *
     * @param card the initial card
     * @param side the desired side
     * @throws CardException if the (0, 0) position is already occupied
     */
    protected void setInitialCard(InitialCard card, Side side) throws CardException {
        if (placed.get(new Pair<>(0, 0)) != null) {
            throw new CardException("Tried to add initial card, but one already exists!");
        }
        placed.put(new Pair<>(0, 0), new PlacedCard(card, side, 0));

        increaseResources(card, side);

        for (Symbol s : card.getSide(side).getCenter()) {
            if (Symbol.getBasicResources().contains(s)) {
                availableResources.put(s, availableResources.get(s) + 1);
            }
        }
    }

    /**
     * This method will add to the board the given card (assuming the positioning is valid), and update the player's resources
     *
     * @param coord the x and y coordinates in which the card must be placed
     * @param card  the card to be placed
     * @param side  the side of the card to be placed
     * @param turn  the turn of the game in which the card is played
     * @return the points gained from playing card
     * @throws CardException if the card type is not known (neither a {@link ResourceCard} nor a {@link GoldCard})
     */
    protected int placeCard(Pair<Integer, Integer> coord, PlayableCard card, Side side, int turn) throws CardException {
        PlacedCard last = new PlacedCard(card, side, turn);
        this.placed.put(coord, last);
        int points = 0;

        Symbol cornerSymbol;
        Integer x = coord.first();
        Integer y = coord.second();

        for (Pair<Integer, Integer> diagOffset : diagonalOffsets.keySet()) {
            try {
                cornerSymbol = this.getSymbolIfPresent(new Pair<>(x + diagOffset.first(), y + diagOffset.second()), diagonalOffsets.get(diagOffset));
                if (cornerSymbol != null) {
                    if (Symbol.getBasicResources().contains(cornerSymbol)) {
                        availableResources.put(cornerSymbol, availableResources.get(cornerSymbol) - 1);
                    }
                }
            } catch (CardException e) {
                System.err.println(e.getMessage());
            }
        }

        increaseResources(card, side);

        for (Symbol s : card.getSide(side).getCenter()) {
            if (Symbol.getBasicResources().contains(s)) {
                availableResources.put(s, availableResources.get(s) + 1);
            }
        }
        if (side.equals(Side.FRONT)) {
            switch (card) {
                case GoldCard gold -> points = gold.calculatePoints(this, coord);
                case ResourceCard resource -> points = resource.getPoints();
                default -> throw new CardException("Unknown card type: " + card.getClass() + "!");
            }
        }

        return points;
    }

    private Symbol getSymbolIfPresent(Pair<Integer, Integer> coord, Corner corner) throws CardException {
        PlacedCard placedCard = placed.get(coord);
        if (placedCard == null) {
            return null;
        }
        return placedCard.getCard().getSide(placedCard.getPlayedSide()).getCorner(corner);
    }

    /**
     * Checks whether the positioning is valid: the card has to be in the player's hand (note that this method won't be called on the initial card),
     * the given coordinates must be valid, and if the card has a requirement it must be met
     *
     * @param coord the coordinates in which the card should be played
     * @param card  the card to check on
     * @param side  the side of the card (needed for requirement check)
     * @return the outcome for the placement, which is valid only if all conditions are met
     * @throws CardException if the card is not in the player's hand
     */
    public PlacementOutcome verifyCardPlacement(Pair<Integer, Integer> coord, Card card, Side side) throws CardException {
        if (coord.equals(new Pair<>(0, 0))) {
            return PlacementOutcome.INVALID_COORDS;
        }
        if (!currentHand.contains(card)) {
            throw new CardException("The card is not in the player's hand!");
        }
        if (placed.containsKey(coord)) {
            return PlacementOutcome.INVALID_COORDS;
        }
        // TODO: To test again
        if (card instanceof GoldCard gold && side == Side.FRONT) {
            if (gold.getRequirement().timesMet(this) == 0)
                return PlacementOutcome.INVALID_ENOUGH_RESOURCES;
        }


        Integer[] offsets = {-1, +1};

        Pair<Integer, Integer> cmp;

        // cross-check: none exists
        for (Integer offset : offsets) {
            cmp = new Pair<>(coord.first() + offset, coord.second());
            if (placed.containsKey(cmp)) {
                return PlacementOutcome.INVALID_COORDS;
            }

            cmp = new Pair<>(coord.first(), coord.second() + offset);
            if (placed.containsKey(cmp)) {
                return PlacementOutcome.INVALID_COORDS;
            }
        }

        boolean hasAdjacent = false;

        Integer x = coord.first();
        Integer y = coord.second();

        for (Pair<Integer, Integer> diagOffset : Board.diagonalOffsets.keySet()) {
            cmp = new Pair<>(x + diagOffset.first(), y + diagOffset.second());

            if (placed.get(cmp) != null) {
                hasAdjacent = true;
                if (placed.get(cmp).getPlayedCardFace().getCorner(diagonalOffsets.get(diagOffset)) == Symbol.EMPTY_CORNER) {
                    return PlacementOutcome.INVALID_COORDS;
                }
            }
        }

        if (!hasAdjacent) {
            return PlacementOutcome.INVALID_COORDS;
        }

        return PlacementOutcome.VALID;
    }

    private void increaseResources(Card card, Side side) throws CardException {
        Symbol cornerSymbol;
        for (Corner c : Corner.values()) {
            cornerSymbol = card.getSide(side).getCorner(c);
            if (Symbol.getBasicResources().contains(cornerSymbol)) {
                availableResources.put(cornerSymbol, availableResources.get(cornerSymbol) + 1);
            }
        }
    }

}
