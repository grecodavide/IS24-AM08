package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.WrongStateException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an appendix of {@link Match}.
 * Match fully delegates to this class, through composition, the role of keeping track of the current game state: it
 * throws exceptions when a Match method is called while being in the wrong state and triggers some Match behavior
 * (calling match.someMethod(...)) when a certain transition has occurred.
 * Each method is supposed to be overridden by a subclass of MatchState if and only if it is allowed to be called from
 * that specific subclass; if not overridden, the MatchState version of the method will be called and thus an exception
 * will be thrown.
 */
public abstract class MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected Match match;

    /**
     * Empty constructor needed for deserialization.
     */
    public MatchState() {

    }

    protected MatchState(Match match) {
        this.match = match;
    }

    /**
     * Triggers the transition in Match from the current state to the next one, this means changing the current Match's
     * MatchState instance to a new one, having the same static type (MatchState) but a different dynamic type (a subclass
     * of MatchState).
     */
    public abstract void transition();

    /**
     * Checks dynamically if a player can be added in the current state, otherwise throws an exception.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState object from which it's called.
     *
     * @throws WrongStateException if the method cannot be called in the current state
     */
    public void addPlayer() throws WrongStateException {
        throw new WrongStateException("addPlayer not allowed from the current match state!");
    }

    /**
     * Checks dynamically if a player can be removed in the current state, otherwise it forces the Match to go to the
     * FinalState.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState instance from which it's called (just WaitState overrides it).
     */
    public void removePlayer() {
        // Exceptionally force the match to go to FinalState
        // since a player has quit in a state that wasn't WaitState
        match.setState(new FinalState(match));
    }

    /**
     * Checks dynamically if an initial card can be drawn in the current state, otherwise throws an exception.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState object from which it's called.
     *
     * @throws WrongStateException if the method cannot be called in the current state
     */
    public void drawInitialCard() throws WrongStateException {
        throw new WrongStateException("drawInitialCard not allowed in the current match state!");
    }

    /**
     * Checks dynamically if a player can choose the initial card side in the current state, otherwise throws an exception.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState object from which it's called.
     *
     * @throws WrongStateException if the method cannot be called in the current state
     */
    public void chooseInitialSide() throws WrongStateException {
        throw new WrongStateException("chooseInitialSide not allowed in the current match state!");
    }

    /**
     * Checks dynamically if a player can make a move in the current state, otherwise throws an exception.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState object from which it's called.
     *
     * @throws WrongStateException if the method cannot be called in the current state
     */
    public void makeMove() throws WrongStateException {
        throw new WrongStateException("makeMove not allowed in the current match state!");
    }

    /**
     * Checks dynamically if a player can draw a playable card in the current state, otherwise throws an exception.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState object from which it's called.
     *
     * @throws WrongStateException if the method cannot be called in the current state
     */
    public void drawCard() throws WrongStateException {
        throw new WrongStateException("drawCard not allowed in the current match state!");
    }

    /**
     * Checks dynamically if a secret objective can be proposed to the current player in the current state, otherwise
     * throws an exception.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState object from which it's called.
     *
     * @throws WrongStateException if the method cannot be called in the current state
     */
    public void proposeSecretObjectives() throws WrongStateException {
        throw new WrongStateException("proposeSecretObjective not allowed in the current match state!");
    }

    /**
     * Checks dynamically if a player can choose their secret objective in the current state, otherwise throws an exception.
     * The check is performed implicitly, since this version of the method is called if and only if it hasn't been
     * overridden by the MatchState object from which it's called.
     *
     * @throws WrongStateException if the method cannot be called in the current state
     */
    public void chooseSecretObjective() throws WrongStateException {
        throw new WrongStateException("chooseSecretObjective not allowed in the current match state!");
    }
}
