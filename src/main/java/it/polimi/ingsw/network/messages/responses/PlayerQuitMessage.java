package it.polimi.ingsw.network.messages.responses;

/**
 * This response is sent when a player quits the current match.
 */
public final class PlayerQuitMessage extends ResponseMessage{
    private final int joinedPlayers;
    private final boolean endMatch;
    public PlayerQuitMessage(String username, int joinedPlayers, boolean endMatch) {
        super(username);
        this.joinedPlayers = joinedPlayers;
        this.endMatch = endMatch;
    }

    /**
     * @return Username of the player that just quit the match
     */
    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    /**
     * @return true if the quit caused the match to interrupt, false otherwise
     */
    public boolean isEndMatch() {
        return endMatch;
    }
}
