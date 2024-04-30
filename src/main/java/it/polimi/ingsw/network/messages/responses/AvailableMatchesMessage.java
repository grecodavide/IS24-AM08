package it.polimi.ingsw.network.messages.responses;

import com.google.gson.JsonObject;
import it.polimi.ingsw.gamemodel.Match;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AvailableMatchesMessage extends ResponseMessage{

    private final List<JsonObject> matches;
    public AvailableMatchesMessage(Map<String, Match> availableMatches) {
        super(null);
        matches = new ArrayList<>();
        availableMatches.forEach((n, m) -> matches.add(encodeMatch(n, m)));
    }

    public List<JsonObject> getMatches() {
        return matches;
    }

    private final JsonObject encodeMatch(String name, Match match) {
        JsonObject result = new JsonObject();
        result.addProperty("name", name);
        result.addProperty("maxPlayers", match.getMaxPlayers());
        result.addProperty("joinedPlayers", match.getPlayers().size());
        return result;
    }
}
