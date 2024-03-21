package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import it.polimi.ingsw.utils.Pair;

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
    public void setInitialSide(Side side) {
        
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
        PlacedCard last = new PlacedCard(card, turn);
        placed.put(coord, last);
        if (card instanceof GoldCard) {
            return ((GoldCard)card).calculatePoints(this);
        } else if (card instanceof ResourceCard) {
            return ((ResourceCard)card).getPoints();
        } else {
            throw new CardException("Unknow card type!");
        }
    }

    /**
    * Checks whether the given position is valid (ie there are no adjacent cards with an empty angle and there is at least one adjacent card), 
    * if the card is in the player's hand and if the card requirement is met
    * @param coord the x and y coordinates to check
    * @return whether the given coordinates are valid or not
    */
    public boolean verifyCardPlacement(Pair<Integer, Integer> coord, Card card, Side side) {
        if (placed.keySet().contains(coord)) {
            return false;
        }
        Pair<Integer, Integer> cmp = new Pair(coord.first()-1, coord.second());
        

        return true;
    }

}

