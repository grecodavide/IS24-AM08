package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

/**
 * The action communicates the intention of the player to place a card on its board. It can only happen during the player's own turn.
 * If the action is successful, a {@link it.polimi.ingsw.network.messages.responses.SomeonePlayedCardMessage} response is sent to every client.
 */
public final class PlayCardMessage extends ActionMessage {
    private Integer x, y;
    private Integer cardID;
    private Side side;

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
     * @return id of teh played card
     */
    public Integer getCardID() {
        return cardID;
    }

    /**
     * @return side of the played card
     */
    public Side getSide() {
        return side;
    }

    public PlayCardMessage(String username, Pair<Integer, Integer> coords, Integer cardID, Side side) {
        super(username);
        this.x = coords.first();
        this.y = coords.second();
        this.cardID = cardID;
        this.side = side;
    }

}
