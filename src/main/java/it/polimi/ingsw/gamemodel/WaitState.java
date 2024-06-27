package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Subclass of {@link MatchState}. This is the state in which the match is when accepting new players or them leaving,
 * that is to say: before the match is full and so it starts.
 */
public class WaitState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Initializes this instance.
     *
     * @param match The match in this state
     */
    public WaitState(Match match) {
        super(match);
    }

    /**
     * Transitions to {@link NextTurnState}.
     */
    @Override
    public void transition() {
        synchronized (match) {
            if (match.isFull()) {
                match.setupDecks();
                match.setupPlayers();
                match.setupBoards();

                MatchState nextState = new NextTurnState(match);
                match.setState(nextState);

                // Notify observers
                match.notifyMatchStart();
            }
        }
    }

    /**
     * This method call is allowed by this class instances.
     */
    @Override
    public void removePlayer() {
    }

    /**
     * This method call is allowed by this class instances.
     */
    @Override
    public void addPlayer() {
    }
}
