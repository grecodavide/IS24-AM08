package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.Side;

/**
 * ChooseInitialCardSideMessage
 */
public final class ChooseInitialCardSideMessage extends ActionMessage {
    private Side side;

    public Side getSide() {
        return side;
    }

    public ChooseInitialCardSideMessage(String username, Side side) {
        super(username);
        this.side = side;
    }
}
