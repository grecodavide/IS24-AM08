package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Subclass of {@link MatchState}. This is the state in which the match is when it's finished, so players are
 * allowed to leave.
 */
public class FinalState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Initializes this instance.
     *
     * @param match The match in this state
     */
    public FinalState(Match match) {
        super(match);

        match.decideWinner();
    }

    /**
     * This method call is allowed by this class instances.
     */
    @Override
    public void removePlayer() {
        // No more transition
    }

    /**
     * This call doesn't have any effect, apart from logging it to stderr.
     */
    @Override
    public void transition() {
        System.err.println("Transition tried in final state");
    }
}
