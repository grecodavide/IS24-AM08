package it.polimi.ingsw.network.messages.responses;

public final class PlayerJoinedMessage extends ResponseMessage{
    private final int joinedPlayers;
    private final int maxPlayers;

    public PlayerJoinedMessage(String username, int joinedPlayers, int maxPlayers) {
        super(username);
        this.joinedPlayers = joinedPlayers;
        this.maxPlayers = maxPlayers;
    }

    public int getJoinedPlayers() {
        return joinedPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
