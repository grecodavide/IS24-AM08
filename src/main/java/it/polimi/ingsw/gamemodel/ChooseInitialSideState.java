package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Subclass of {@link MatchState}. This is the state in which the match is giving an initial card to a player and
 * waiting him to choose its side.
 */
public class ChooseInitialSideState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Initializes this instance.
     *
     * @param match The match in this state
     */
    public ChooseInitialSideState(Match match) {
        super(match);
    }

    /**
     * This method call is allowed by this class instances.
     */
    @Override
    public void chooseInitialSide() {

    }

    /**
     * Transitions to {@link NextTurnState}
     */
    @Override
    public void transition() {
        Player lastPlayer = match.getPlayers().getLast();

        if (match.getCurrentPlayer().equals(lastPlayer))
            match.doInitialTurnFinish();
        MatchState nextState = new NextTurnState(match);
        match.setState(nextState);
    }
}
