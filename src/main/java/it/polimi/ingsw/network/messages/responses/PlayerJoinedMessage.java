package it.polimi.ingsw.network.messages.responses;

/**
 * This response is sent when a player joins the current match.
 */
public final class PlayerJoinedMessage extends ResponseMessage{
    private final int joinedPlayers;
    private final int maxPlayers;

    public PlayerJoinedMessage(String username, int joinedPlayers, int maxPlayers) {
        super(username);
        this.joinedPlayers = joinedPlayers;
        this.maxPlayers = maxPlayers;
    }

    /**
     * @return Number of players currently in the match
     */
    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    /**
     * @return Maximum amount of players the match can hold
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }
}
