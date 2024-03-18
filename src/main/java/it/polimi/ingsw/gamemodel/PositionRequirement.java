package it.polimi.ingsw.gamemodel;

import java.util.Map;

import com.sun.tools.javac.util.Pair;

/**
* This class handles requirements involving relative positioning of cards, e.g. three red cards placed in the top right corner of each other
*/
public class PositionRequirement extends Requirement{
    private Map<Pair<Integer, Integer>, Color> reqs; // FIX: does not make sense to me: a PositionRequirement should check for relative positioning, not absolute

    public PositionRequirement(Map<Pair<Integer, Integer>, Color> reqs) {
        this.reqs = reqs;
    }

	@Override
	public boolean isSatisfied(Board board) {
        return true;
	}

}
