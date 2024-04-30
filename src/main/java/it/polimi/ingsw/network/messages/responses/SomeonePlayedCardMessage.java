package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

/**
 * SomeonePlayedCardMessage
 */
public final class SomeonePlayedCardMessage extends ResponseMessage {
    private final Integer x, y;
    private final Integer cardID;
    private final Side side;
    private final Integer points;
    
    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    public Integer getCardID() {
        return cardID;
    }

    public Side getSide() {
        return side;
    }

    public Integer getPoints() {
        return points;
    }

    public SomeonePlayedCardMessage(String username, Pair<Integer, Integer> coords, Integer cardID, Side side, int points) {
        super(username);
        this.x = coords.first();
        this.y = coords.second();
        this.cardID = cardID;
        this.side = side;
        this.points = points;
    }

}
