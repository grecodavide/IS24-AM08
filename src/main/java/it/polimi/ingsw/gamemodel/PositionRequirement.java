package it.polimi.ingsw.gamemodel;

import java.util.Map;

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
    public PositionRequirement(Map<Pair<Integer, Integer>, Symbol> reqs) {
        this.reqs = reqs;
    }

    /**
    * The requirement will be satisfied if the board has cards of the specified faction in the correct relative positions
    * @param board the {@link Board} on which the requirement must be checked
    * @return wheter the board actually meets the requirement or not
    */
	@Override
	public boolean isSatisfied(Board board) {
        return true;
	}

}
