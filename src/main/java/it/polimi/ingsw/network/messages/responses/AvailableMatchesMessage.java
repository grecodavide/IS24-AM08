package it.polimi.ingsw.network.messages.responses;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.utils.AvailableMatch;

/**
 * This response is sent when a user is connected to the server.
 */
public final class AvailableMatchesMessage extends ResponseMessage {

    private final List<AvailableMatch> matches;

    public AvailableMatchesMessage(Map<String, Match> availableMatches) {
        super(null);
        matches = new ArrayList<>();
        availableMatches.forEach((n, m) -> matches.add(encodeMatch(n, m)));
    }

    /**
     * @return a list containing a JsonObject for each match with properties:
     * name - name of the match
     * maxPlayers - maximum number of players
     * joinedPlayers - number of players in the match
     */
    public List<AvailableMatch> getMatches() {
        return matches;
    }

    private final AvailableMatch encodeMatch(String name, Match match) {
        return new AvailableMatch(name, match.getMaxPlayers(), match.getPlayers().size());
    }
}
