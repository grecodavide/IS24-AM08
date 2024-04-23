package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.Side;

/**
 * ChooseInitialCardSideMessage
 */
public final class ChooseInitialCardSideMessage extends ActionMessage {
    public Side side;
    
    public ChooseInitialCardSideMessage(String username, Side side) {
        super(username);
        this.side = side;
    }
}
