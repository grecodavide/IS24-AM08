package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Subclass of {@link MatchState}. This is the state in which the match allows a player to draw a card.
 */
public class AfterMoveState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Initializes this instance.
     *
     * @param match The match in this state
     */
    public AfterMoveState(Match match) {
        super(match);
    }

    /**
     * This method call is allowed by this class instances.
     */
    @Override
    public void drawCard() {

    }

    /**
     * Transitions to {@link AfterDrawState}.
     */
    @Override
    public void transition() {
        MatchState nextState = new AfterDrawState(match);
        match.setState(nextState);

        nextState.transition();
    }
}
