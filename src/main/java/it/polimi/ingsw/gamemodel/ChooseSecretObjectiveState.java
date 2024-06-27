package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Subclass of {@link MatchState}. This is the state in which the match is giving two secret objectives to a player and
 * waiting him to choose one of them.
 */
public class ChooseSecretObjectiveState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Initializes this instance.
     *
     * @param match The match in this state
     */
    public ChooseSecretObjectiveState(Match match) {
        super(match);
    }

    /**
     * This method call is allowed by this class instances.
     */
    @Override
    public void chooseSecretObjective() {

    }

    /**
     * Transitions to {@link NextTurnState}
     */
    @Override
    public void transition() {
        Player lastPlayer = match.getPlayers().getLast();

        if (match.getCurrentPlayer().equals(lastPlayer))
            match.doStart();

        MatchState nextState = new NextTurnState(match);
        match.setState(nextState);
    }
}
