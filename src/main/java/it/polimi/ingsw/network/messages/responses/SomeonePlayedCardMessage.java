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

    /**
     * @return x coordinate of the played card
     */
    public Integer getX() {
        return x;
    }

    /**
     * @return y coordinate of the played card
     */
    public Integer getY() {
        return y;
    }

    /**
     * @return ID of the played card
     */
    public Integer getCardID() {
        return cardID;
    }

    /**
     * @return Side in which the card has been played. It can be either FRONT or BACK
     */
    public Side getSide() {
        return side;
    }

    /**
     * @return Amount of points earned from the move
     */
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
