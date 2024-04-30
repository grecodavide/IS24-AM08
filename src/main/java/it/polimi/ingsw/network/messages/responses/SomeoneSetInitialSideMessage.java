package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.Side;

/**
 * This response is sent to each user in the match when a user chosees the initial side of a card.
 */
public final class SomeoneSetInitialSideMessage extends ResponseMessage {

    private final Side side;

    /**
     * @return Side of the initial card.
     */
    public Side getSide() {
        return side;
    }

    public SomeoneSetInitialSideMessage(String username, Side side) {
        super(username);
        this.side = side;
    }
}
