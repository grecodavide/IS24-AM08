package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

/**
 * PlayCardMessage
 */
public final class PlayCardMessage extends ActionMessage {
    public Integer x, y;
    public Integer cardID;
    public Side side;

    public PlayCardMessage(String username, Pair<Integer, Integer> coords, Integer cardID, Side side) {
        super(username);
        this.x = coords.first();
        this.y = coords.second();
        this.cardID = cardID;
        this.side = side;
    }
    
}
