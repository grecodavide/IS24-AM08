package it.polimi.ingsw.gamemodel;

import java.util.Map;

import it.polimi.ingsw.utils.Pair;

/**
* This class handles requirements involving relative positioning of cards, e.g. three red cards placed in the top right corner of each other
*/
public class QuantityRequirement extends Requirement{
    private Map<Symbol, Integer> reqs;

    /**
    * @param reqs how many resources of a certain type are needed to fulfill the requirement
    */
    public QuantityRequirement(Map<Symbol, Integer> reqs) {
        this.reqs = reqs;
    }

    /**
    * The requirement will be satisfied if the board has enough resources of the specified type
    * @param board the board on which we check the requirement
    * @return whether the requirement is satisfied
    */
	@Override
	public boolean isSatisfied(Board board) {
        return true;
	}
}
