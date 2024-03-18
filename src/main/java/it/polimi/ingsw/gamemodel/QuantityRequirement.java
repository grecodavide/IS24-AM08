package it.polimi.ingsw.gamemodel;

import java.util.Map;

import com.sun.tools.javac.util.Pair;

/**
* This class handles requirements involving relative positioning of cards, e.g. three red cards placed in the top right corner of each other
*/
public class QuantityRequirement extends Requirement{
    private Map<Symbol, Integer> reqs;

    public QuantityRequirement(Map<Symbol, Integer> reqs) {
        this.reqs = reqs;
    }

	@Override
	public boolean isSatisfied(Board board) {
        return true;
	}
}
