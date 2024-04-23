package it.polimi.ingsw.network.messages.responses;

import com.google.gson.JsonObject;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MatchFinishedMessage
 */
public final class MatchFinishedMessage extends ResponseMessage {
    public List<JsonObject> ranking;
    public MatchFinishedMessage(List<Pair<Player, Boolean>> finalRanking) {
        super("");
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
