package it.polimi.ingsw.network.messages.actions;

/**
 * The action communicates (to the server) the intention of a client to create a new match.
 */
public final class CreateMatchMessage extends ActionMessage {
    private final String matchName;
    private final int maxPlayers;

    public CreateMatchMessage(String username, String matchName, int maxPlayers) {
        super(username);
        this.matchName = matchName;
        this.maxPlayers = maxPlayers;
    }

    /***
     *
     * @return Name of the match
     */
    public String getMatchName() {
        return matchName;
    }

    /***
     *
     * @return Number of maximum players (must be between 2 and 4)
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }
}
