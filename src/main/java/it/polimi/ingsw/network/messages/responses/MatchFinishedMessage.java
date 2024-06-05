package it.polimi.ingsw.network.messages.responses;

import java.util.List;
import java.util.stream.Collectors;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

/**
 * This response is sent to each player when the match is finished
 */
public final class MatchFinishedMessage extends ResponseMessage {
    private final List<LeaderboardEntry> ranking;

    /**
     * @return a list of JSONObject with properties
     * username (String) - username of the current player
     * points (Integer) - total number of points gained during the match
     * winner (boolean) - if the current player is also the winner of the game
     */
    public List<LeaderboardEntry> getRanking() {
        return ranking;
    }

    public MatchFinishedMessage(List<Pair<Player, Boolean>> finalRanking) {
        super(null);
        ranking = finalRanking.stream().map(p -> createLeaderboardEntry(p.first(), p.second())).collect(Collectors.toList());
    }

    private LeaderboardEntry createLeaderboardEntry(Player p, Boolean b) {
        return new LeaderboardEntry(p.getUsername(), p.getPoints(), b);
    }

}
