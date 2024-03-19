package it.polimi.ingsw.gamemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.tools.javac.util.Pair;

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
    protected void addHandCard(PlayableCard card) {
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
    protected void removeCardHand(PlayableCard card) {
        currentHand.remove(card);
    }

    /**
    * Used to know if it is possible to place a golden card and if an objective is accomplished
    * @param req the requirement to check
    * @return whether the given requirement is met or not.
    */
    public boolean checkRequirement(Requirement req) {
        return req.isSatisfied(this);
    }

    /**
    * This method will add to the board the given card (if requirements are met and the position is valid), and update the player's resources
    * @param coord the x and y coordinates in which the card must be placed
    * @param card the card to be placed
    * @param side the side of the card to be placed
    * @return the points gained from playing card
    */
    protected int placeCard(Pair<Integer, Integer> coord, Card card, Side side) {

        return 0;
    }

    /**
    * Checks whether the given position is valid (ie there are no adjacent cards with an empty angle and there is at least one adjacent card), 
    * if the card is in the player's hand and if the card requirement is met
    * @param coord the x and y coordinates to check
    * @return whether the given coordinates are valid or not
    */
    public boolean verifyCardPlacement(Pair<Integer, Integer> coord) {
        return true;
    }

}

