package it.polimi.ingsw.gamemodel;

import java.util.EnumSet;
import java.util.Map;

import it.polimi.ingsw.exceptions.InvalidResourceException;

/**
* This class handles requirements involving relative positioning of cards, e.g. three red cards placed in the top right corner of each other
*/
public class QuantityRequirement extends Requirement{
    private Map<Symbol, Integer> reqs;

    /**
    * Class constructor. Only valid symbols are the ones returned by {@link Symbol#getBasicResources()}
    * @param reqs how many resources of a certain type are needed to fulfill the requirement.
    * @throws InvalidResourceException if a requirement is not made up only of those symbols
    */
    public QuantityRequirement(Map<Symbol, Integer> reqs) throws InvalidResourceException {
        EnumSet<Symbol> validResources = Symbol.getBasicResources();
        for (Symbol s : reqs.keySet()) {
            if ( !validResources.contains(s) ) {
                throw new InvalidResourceException("Resource " + s.toString() + " is not valid for a " + this.getClass().toString());
            }
        }
        this.reqs = reqs;
    }

    /**
    * The requirement will be satisfied if the board has enough resources of the specified type
    * @param board the board on which we check the requirement
    * @return whether the requirement is satisfied or not
    */
	@Override
	public int timesMet(Board board) {
        Map<Symbol, Integer> availableResources = board.getAvailableResources();
        Integer min = null;
        Integer curr;

        for (Symbol req : reqs.keySet()) {
            curr = availableResources.get(req)/reqs.get(req);
            if (min == null || curr < min) {
                min = curr;
            }
        }

        return min;
	}
}
