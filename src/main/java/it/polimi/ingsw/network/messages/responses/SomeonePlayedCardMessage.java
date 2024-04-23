package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

/**
 * SomeonePlayedCardMessage
 */
public final class SomeonePlayedCardMessage extends ResponseMessage {

    public Integer x, y;
    public Integer cardID;
    public Side side;
    public Integer points;
    
    public SomeonePlayedCardMessage(String username, Pair<Integer, Integer> coords, Integer cardID, Side side, int points) {
        super(username);
        this.x = coords.first();
        this.y = coords.second();
        this.cardID = cardID;
        this.side = side;
        this.points = points;
    }
}
