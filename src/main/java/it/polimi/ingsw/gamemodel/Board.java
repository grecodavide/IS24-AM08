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
    * TODO: questo metodo può essere chiamato solo in seguito a una pop del mazzo: ci piace che sia pubblicamente disponibile??
    */
    public void addHandCard(PlayableCard card) {
        currentHand.addLast(card);
    }

    // FIX: if we dont have an attribute for initial card, how we gon set its side??
    public void setInitialSide(Side side) {

    }

    public void removeCardHand(PlayableCard card) {
        currentHand.remove(card);
    }

    public boolean checkRequirement(Requirement req) {
        return req.isSatisfied(this);
    }

    // will also update resources: check the 4 adjacent valid positions and see if there is a card. If there is it'll remove those corner's resouces.
    // Then it will add the card's resources
    public int placeCard(Pair<Integer, Integer> coord, Card card, Side side) {
        return 0; // points ??????
    }

    public boolean verifyCardPlacement(Pair<Integer, Integer> coord, Card card) {
        return true;
    }

}

