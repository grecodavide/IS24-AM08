package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.Side;

/**
 * SomeoneSetInitialSideMessage
 */
public class SomeoneSetInitialSideMessage extends ResponseMessage {

    public Side side;
    public SomeoneSetInitialSideMessage(String username, Side side) {
        super(username);
        this.side = side;
    }
}
