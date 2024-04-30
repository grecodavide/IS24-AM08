package it.polimi.ingsw.network.messages.responses;

import java.util.List;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.utils.Pair;

/**
 * MatchFinishedMessage
 */
public final class MatchFinishedMessage extends ResponseMessage {
    private final List<JsonObject> ranking;

    public List<JsonObject> getRanking() {
        return ranking;
    }

    public MatchFinishedMessage(List<Pair<Player, Boolean>> finalRanking) {
        super(null);
        ranking = finalRanking.stream().map(p -> createJsonObject(p.first(), p.second())).collect(Collectors.toList());
    }

    private JsonObject createJsonObject(Player p, Boolean b) {
        JsonObject result = new JsonObject();
        result.addProperty("username", p.getNickname());
        result.addProperty("points", p.getPoints());
        result.addProperty("winner", b);
        return result;
    }
    
}
