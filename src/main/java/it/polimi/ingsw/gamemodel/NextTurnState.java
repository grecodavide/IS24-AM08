package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.WrongStateException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Subclass of {@link MatchState}. This is the state in which the match decides whether it must give initial cards, give
 * secret objectives, or wait for someone to play a card, according to the current match flow.
 */
public class NextTurnState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Initializes this instance.
     *
     * @param match The match in this state
     */
    public NextTurnState(Match match) {
        super(match);
        match.nextPlayer();
    }

    /**
     * This method call is allowed by this class instances if and only if the match hasn't started yet
     * and the initial card side choosing has already finished.
     *
     * @throws WrongStateException If called when the match has already started or the initial card side choosing
     *                             hasn't finished yet.
     */
    @Override
    public void proposeSecretObjectives() throws WrongStateException {
        if (match.isStarted() || !match.isInitialTurnFinished())
            throw new WrongStateException("proposeSecretObjectives called after the match was started");
    }

    /**
     * This method call is allowed by this class instances if and only if the match has already started.
     * @throws WrongStateException If called when the match hasn't started yet
     */
    @Override
    public void makeMove() throws WrongStateException {
        if (!match.isStarted())
            throw new WrongStateException("makeMove called when match was not started yet");
    }

    /**
     * This method call is allowed by this class instances if and only if the initial card side choosing
     * hasn't finished yet.
     * @throws WrongStateException If called when the initial card side choosing has already finished.
     */
    @Override
    public void drawInitialCard() throws WrongStateException {
        if (match.isInitialTurnFinished())
            throw new WrongStateException("drawInitialCard called after the initial turn was finished");
    }

    /**
     * Transitions to:
     *  - {@link AfterMoveState} if the match has already started;
     *  - {@link ChooseInitialSideState} if the initial card side choosing hasn't finished yet;
     *  - {@link ChooseSecretObjectiveState} if the match hasn't started yet and the initial card side choosing has
     *    already finished;
     */
    @Override
    public void transition() {
        MatchState nextState;

        if (match.isStarted())
            nextState = new AfterMoveState(match);
        else if (!match.isInitialTurnFinished())
            nextState = new ChooseInitialSideState(match);
        else
            nextState = new ChooseSecretObjectiveState(match);

        match.setState(nextState);
    }
}
