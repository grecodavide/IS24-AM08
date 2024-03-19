package it.polimi.ingsw.gamemodel;

import java.util.Map;

import com.sun.tools.javac.util.Pair;

/**
* This class handles requirements involving relative positioning of cards, e.g. three red cards placed in the top right corner of each other
*/
public class PositionRequirement extends Requirement{
    private Map<Pair<Integer, Integer>, Color> reqs;

    /**
    * @param reqs The relative positioning of the cards (of which we only care about the faction).
    * Note that, since this requirement only cares about relative positioning, there must always be
    * an element whose key is (0, 0)
    */
    public PositionRequirement(Map<Pair<Integer, Integer>, Color> reqs) {
        this.reqs = reqs;
    }

    /**
    * @param board the {@link Board} on which the requirement must be checked
    * @return wheter the board actually meets the requirement or not
    */
	@Override
	public boolean isSatisfied(Board board) {
        return true;
	}

}
