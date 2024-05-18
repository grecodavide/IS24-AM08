package it.polimi.ingsw.network.messages.responses;

import java.util.Map;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;

/**
 * SomeonePlayedCardMessage
 */
public final class SomeonePlayedCardMessage extends ResponseMessage {
    private final Integer x, y;
    private final Integer cardID;
    private final Side side;
    private final Integer points;
    private final Map<Symbol, Integer> availableResources;

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
     * @return Total points of the player after he placed the card
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * @return Resources possessed by the player after he placed the card
     */
    public Map<Symbol, Integer> getAvailableResources() {
        return availableResources;
    }

    public SomeonePlayedCardMessage(String username, Pair<Integer, Integer> coords, Integer cardID, Side side, int points, Map<Symbol, Integer> availableResources) {
        super(username);
        this.x = coords.first();
        this.y = coords.second();
        this.cardID = cardID;
        this.side = side;
        this.points = points;
        this.availableResources = availableResources;
    }

}
