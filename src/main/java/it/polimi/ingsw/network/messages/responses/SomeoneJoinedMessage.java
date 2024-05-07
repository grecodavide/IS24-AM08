package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.Player;

import java.util.List;

/**
 * This response is sent when a player joins the current match.
 */
public final class SomeoneJoinedMessage extends ResponseMessage{
    private final List<String> joinedPlayers;
    private final int maxPlayers;

    public SomeoneJoinedMessage(String username, List<Player> joinedPlayers, int maxPlayers) {
        super(username);
        this.joinedPlayers = joinedPlayers.stream().map(Player::getNickname).toList();
        this.maxPlayers = maxPlayers;
    }

    /**
     * @return Nicknames of players currently in the match
     */
    public List<String> getJoinedPlayers() {
        return joinedPlayers;
    }

    /**
     * @return Maximum amount of players the match can hold
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }
}
