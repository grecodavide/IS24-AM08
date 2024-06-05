package it.polimi.ingsw.network.messages.actions;

/**
 * The action communicates the intention of a client to join a match.
 */
public final class JoinMatchMessage extends ActionMessage {
    private final String matchName;

    public JoinMatchMessage(String username, String matchName) {
        super(username);
        this.matchName = matchName;
    }

    /**
     * @return Name of the match to join
     */
    public String getMatchName() {
        return matchName;
    }
}
