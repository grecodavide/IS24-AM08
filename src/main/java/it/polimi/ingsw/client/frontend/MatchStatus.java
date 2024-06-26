package it.polimi.ingsw.client.frontend;

import it.polimi.ingsw.gamemodel.MatchState;

/**
 * Represents the current match macro-state from the client point of view.
 * This is not supposed to substitute {@link MatchState}, but rather make it easier for clients
 * to keep track of the current state of match, which is to say whether it's unused (LOBBY),
 * in WaitState (WAIT_STATE) or being played (MATCH_STATE).
 */
public enum MatchStatus {
    LOBBY,
    WAIT_STATE,
    MATCH_STATE
}
