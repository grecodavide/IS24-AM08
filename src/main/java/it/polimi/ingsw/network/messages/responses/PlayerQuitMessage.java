package it.polimi.ingsw.network.messages.responses;

public final class PlayerQuitMessage extends ResponseMessage{
    private final int joinedPlayers;
    private final boolean endMatch;
    public PlayerQuitMessage(String username, int joinedPlayers, boolean endMatch) {
        super(username);
        this.joinedPlayers = joinedPlayers;
        this.endMatch = endMatch;
    }

    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    public boolean isEndMatch() {
        return endMatch;
    }
}
