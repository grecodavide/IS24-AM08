package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Subclass of {@link MatchState}. This is the state in which the match recognize if it's finished or it has to continue.
 */
public class AfterDrawState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Initializes this instance.
     *
     * @param match The match in this state
     */
    public AfterDrawState(Match match) {
        super(match);
    }

    /**
     * If the match isn't finished, transitions to {@link NextTurnState}, otherwise to {@link FinalState}.
     */
    @Override
    public void transition() {
        MatchState nextState;

        if (match.isFinished())
            nextState = new FinalState(match);
        else
            nextState = new NextTurnState(match);

        match.setState(nextState);
    }
}
