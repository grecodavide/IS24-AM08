package it.polimi.ingsw.gamemodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sun.tools.javac.util.Pair;

/**
 * Board is the class that contains all the informations relative to a {@link it.polimi.ingsw.gamemodel.Player}'s status
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
    * Adds a card to the player's hand (which is public)
    * @param card the card to put in the hand
    */
    // WARN: questo metodo può essere chiamato solo in seguito a una pop del mazzo: ci piace che sia pubblicamente disponibile??
    public void addHandCard(PlayableCard card) {
        currentHand.addLast(card);
    }

    // FIX: if we dont have an attribute for initial card, how we gon set its side??
    public void setInitialSide(Side side) {

    }

    /**
    *
    */
    public void removeCardHand(PlayableCard card) {
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
    * This method will add to the board the given card (if requirements are met and the position is valid), and it will update the player's resources
    * @param coord the x and y coordinates in which the card must be placed
    * @param card the card to be placed
    * @param side the side of the card to be placed
    * @return the points gained from the card
    */
    // WARN: viene chiamato da match, quindi perché è pubblico?
    public int placeCard(Pair<Integer, Integer> coord, Card card, Side side) {
        return 0; // points ??????
    }

    /**
    * Checks whether the given position is valid (ie there are no adjacent cards with an empty angle and there is at least one adjacent card)
    * @param coord the x and y coordinates to check
    * @return whether the given coordinates are valid or not
    */
    public boolean verifyCardPlacement(Pair<Integer, Integer> coord) {
        return true;
    }

}

