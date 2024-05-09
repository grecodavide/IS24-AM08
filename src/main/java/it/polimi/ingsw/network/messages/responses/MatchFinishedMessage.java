package it.polimi.ingsw.network.messages.responses;

import com.google.gson.JsonObject;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.utils.Pair;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This response is sent to each player when the match is finished
 */
public final class MatchFinishedMessage extends ResponseMessage {
    private final List<JsonObject> ranking;

    /**
     * @return a list of JSONObject with properties
     * username (String) - username of the current player
     * points (Integer) - total number of points gained during the match
     * winner (boolean) - if the current player is also the winner of the game
     */
    public List<JsonObject> getRanking() {
        return ranking;
    }

    public MatchFinishedMessage(List<Pair<Player, Boolean>> finalRanking) {
        super(null);
        ranking = finalRanking.stream().map(p -> createJsonObject(p.first(), p.second())).collect(Collectors.toList());
    }

    private JsonObject createJsonObject(Player p, Boolean b) {
        JsonObject result = new JsonObject();
        result.addProperty("username", p.getUsername());
        result.addProperty("points", p.getPoints());
        result.addProperty("winner", b);
        return result;
    }

}
