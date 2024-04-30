package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.Side;

/**
 * The action communicates the player's choice of the initial card's side. It can only happen before the first turn of the game.
 * If the action is successful, {@link it.polimi.ingsw.network.messages.responses.SomeoneDrewCardMessage} response is sent to every client.
 */
public final class ChooseInitialCardSideMessage extends ActionMessage {
    private Side side;

    /**
     * @return chosen side
     */
    public Side getSide() {
        return side;
    }

    public ChooseInitialCardSideMessage(String username, Side side) {
        super(username);
        this.side = side;
    }
}
