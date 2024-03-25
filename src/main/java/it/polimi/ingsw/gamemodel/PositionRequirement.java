package it.polimi.ingsw.gamemodel;

import java.util.EnumSet;
import java.util.Map;

import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.utils.Pair;

/**
* This class handles requirements involving relative positioning of cards, e.g. three red cards placed in the top right corner of each other
*/
public class PositionRequirement extends Requirement{
    private Map<Pair<Integer, Integer>, Symbol> reqs;

    /**
    * @param reqs The relative positioning of the cards (of which we only care about the faction).
    * Note that, since this requirement only cares about relative positioning, there must always be
    * an element whose key is (0, 0)
    */
    public PositionRequirement(Map<Pair<Integer, Integer>, Symbol> reqs) throws InvalidResourceException {
        EnumSet<Symbol> validResources = Symbol.getReigns();
        for (Symbol s : reqs.values()) {
            if ( !validResources.contains(s) ) {
                throw new InvalidResourceException("Resource " + s.toString() + " is not valid for a " + this.getClass().toString());
            }
        }

        this.reqs = reqs;
    }

    /**
    * The requirement will be satisfied if the board has cards of the specified faction in the correct relative positions
    * @param board the {@link Board} on which the requirement must be checked
    * @return wheter the board actually meets the requirement or not
    */
	@Override
	public int isSatisfied(Board board) {
        Map<Pair<Integer, Integer>, PlacedCard> placedCards = board.getPlacedCards();

        Card cmpPlaced;

        boolean requirementMatched;
        int timesMet = 0;

        for (Pair<Integer, Integer> coord : placedCards.keySet()) {
            requirementMatched = true;
            for (Pair<Integer, Integer> offset : this.reqs.keySet()) {
                cmpPlaced = placedCards.get(new Pair<>(coord.first()+offset.first(), coord.second()+offset.second())).getCard();
                if ((!(cmpPlaced instanceof PlayableCard)) || ((PlayableCard)cmpPlaced).getReign() != this.reqs.get(offset)) {
                    requirementMatched = false;
                }
            }
            if (requirementMatched) {
                timesMet++;
            }
        }
        return timesMet;
	}

}

// (0, 0), FUNGUS
// (1, 1), INSECT
// (2, 2), FUNGUS
