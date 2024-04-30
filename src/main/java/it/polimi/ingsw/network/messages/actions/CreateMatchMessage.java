package it.polimi.ingsw.network.messages.actions;

public final class CreateMatchMessage extends ActionMessage {
    private final String matchName;
    private final int maxPlayers;

    public CreateMatchMessage(String username, String matchName, int maxPlayers) {
        super(username);
        this.matchName = matchName;
        this.maxPlayers = maxPlayers;
    }


    public String getMatchName() {
        return matchName;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
