package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.utils.Pair;


/*
- implement resource elaboration on placed cards
- Update Javadoc
- More elegant way for diagonl controls (they are very similar to resource update controls)
*/

/**
* Board is the class that contains all the informations relative to a {@link Player}'s status
*/
public class Board {
    private List<PlayableCard> currentHand;
    private Map<Pair<Integer, Integer>, PlacedCard> placed;
    private Map<Symbol, Integer> availableResources;

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
    */
    public Map<Symbol, Integer> getAvailableResources() {
        return this.availableResources;
    }

    /**
    * Getter for the hand of the player (which must be composed of three {@link PlayableCard}), which is visible
    * to every player
    */
    public List<PlayableCard> getCurrentHand() {
        return this.currentHand;
    }

    /**
    * Adds a card to the player's hand (which is visible to every player)
    * @param card the card to put in the hand
    */
    protected void addHandCard(PlayableCard card) throws HandException {
        if (currentHand.size() > 2) { // la mano ha 3 carte max
            throw new HandException("Tried to draw a card with a full hand!");
        }
        currentHand.addLast(card);
    }

    /**
    * The initial card will be added by {@link Match} at the start of the game, and it will be set on the front side by default.
    * During the first turn of the player, he will be asked if he wants to switch side with this method
    * @param side the desired side for the initial card
    */
    protected void setInitialCard(InitialCard card) throws CardException {
        if (placed.get(new Pair<>(0,0)) != null) {
            throw new CardException("Tried to add initial card, but one already exists!");
        }
        placed.put(new Pair<>(0, 0), new PlacedCard(card, Side.FRONT, 0));
    }
    /**
    * The initial card will be added by {@link Match} at the start of the game, and it will be set on the front side by default.
    * During the first turn of the player, he will be asked if he wants to switch side with this method
    * @param side the desired side for the initial card
    */
    protected void setInitialSide(Side side) {
        placed.get(new Pair<>(0, 0));
    }

    /**
    * Removes a card from the hand of the player
    * @param card the card that must be removed from the player's hand
    */
    protected void removeHandCard(PlayableCard card) throws HandException {
        if (currentHand.size() <= 0) {
            throw new HandException("Tried to remove a card from an empty hand!");
        }
        currentHand.remove(card);
    }

    /**
    * This method will add to the board the given card (assuming the positioning is valid), and update the player's resources
    * @param coord the x and y coordinates in which the card must be placed
    * @param card the card to be placed
    * @param side the side of the card to be placed
    * @param turn the turn of the game in which the card is played
    * @return the points gained from playing card
    */
    protected int placeCard(Pair<Integer, Integer> coord, PlayableCard card, Side side, int turn) throws CardException {
        PlacedCard last = new PlacedCard(card, side, turn);
        this.placed.put(coord, last);
        int points = 0;

        if (card instanceof GoldCard) {
            points = ((GoldCard)card).calculatePoints(this);
        } else if (card instanceof ResourceCard) {
            points = ((ResourceCard)card).getPoints();
        } else {
            throw new CardException("Unknow card type: " + card.getClass().toString() + "!");
        }



        return points;
    }

    private boolean hasDiagonalAdjacent(Pair<Integer, Integer> coord) {
        Integer[] offsets = {-1, +1};
        Pair<Integer, Integer> cmp;

        for (Integer xOffset : offsets) {
            for (Integer yOffset : offsets) {
                cmp = new Pair<>(coord.first() + xOffset, coord.second() + yOffset);
                if (placed.keySet().contains(cmp)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
    * Checks whether the given position is valid (ie there are no adjacent cards with an empty angle and there is at least one adjacent card), 
    * if the card is in the player's hand and if the card requirement is met
    * @param coord the x and y coordinates to check
    * @return whether the given coordinates are valid or not
    */
    public PlacementOutcome verifyCardPlacement(Pair<Integer, Integer> coord, Card card, Side side) throws CardException {
        if (coord.equals(new Pair<>(0, 0))) {
            return PlacementOutcome.INVALID_COORDS;
        }

        if (!currentHand.contains(card)) {
            throw new CardException("The card " + card.getClass().toString() + " is not in the player's hand!");
        }
        if (placed.keySet().contains(coord)) {
            return PlacementOutcome.INVALID_COORDS;
        }
        if (card instanceof GoldCard && !((GoldCard)card).getRequirement().isSatisfied(this)) {
            return PlacementOutcome.INVALID_ENOUGH_RESOURCES;
        }


        Integer[] offsets = {-1, +1};

        Pair<Integer, Integer> cmp;

        // cross check: none exists
        for (Integer offset : offsets) {
            cmp = new Pair<>(coord.first()+offset, coord.second());
            if (placed.keySet().contains(cmp)) {
                return PlacementOutcome.INVALID_COORDS;
            }

            cmp = new Pair<>(coord.first(), coord.second()+offset);
            if (placed.keySet().contains(cmp)) {
                return PlacementOutcome.INVALID_COORDS;
            }
        }

        // diagonal check: at least one exists and none has an empty corner
        if (!hasDiagonalAdjacent(coord)) {
            return PlacementOutcome.INVALID_COORDS;
        }

        Symbol adjacentCornerSymbol;
        cmp = new Pair<Integer, Integer>(coord.first()-1, coord.second()+1);
        if (placed.get(cmp) != null ) {
            adjacentCornerSymbol = placed.get(cmp).getPlayedCardFace().getCorner(Corner.BOTTOM_RIGHT);
            if (adjacentCornerSymbol == Symbol.EMPTY_CORNER) {
                return PlacementOutcome.INVALID_COORDS;
            }
        }

        cmp = new Pair<Integer, Integer>(coord.first()+1, coord.second()+1);
        if (placed.get(cmp) != null ) {
            adjacentCornerSymbol = placed.get(cmp).getPlayedCardFace().getCorner(Corner.BOTTOM_LEFT);
            if (adjacentCornerSymbol == Symbol.EMPTY_CORNER) {
                return PlacementOutcome.INVALID_COORDS;
            }
        }

        cmp = new Pair<Integer, Integer>(coord.first()-1, coord.second()-1);
        if (placed.get(cmp) != null ) {
            adjacentCornerSymbol = placed.get(cmp).getPlayedCardFace().getCorner(Corner.TOP_RIGHT);
            if (adjacentCornerSymbol == Symbol.EMPTY_CORNER) {
                return PlacementOutcome.INVALID_COORDS;
            }
        }

        cmp = new Pair<Integer, Integer>(coord.first()-1, coord.second()+1);
        if (placed.get(cmp) != null ) {
            adjacentCornerSymbol = placed.get(cmp).getPlayedCardFace().getCorner(Corner.BOTTOM_RIGHT);
            if (adjacentCornerSymbol == Symbol.EMPTY_CORNER) {
                return PlacementOutcome.INVALID_COORDS;
            }
        }

        return PlacementOutcome.VALID;
    }

}

