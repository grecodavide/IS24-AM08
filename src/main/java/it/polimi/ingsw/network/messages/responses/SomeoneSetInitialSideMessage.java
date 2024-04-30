package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.Side;

/**
 * SomeoneSetInitialSideMessage
 */
public final class SomeoneSetInitialSideMessage extends ResponseMessage {

    private final Side side;

    public Side getSide() {
        return side;
    }

    public SomeoneSetInitialSideMessage(String username, Side side) {
        super(username);
        this.side = side;
    }
}
