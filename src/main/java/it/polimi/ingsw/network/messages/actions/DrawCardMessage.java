package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.DrawSource;

/**
 * The action communicates the intention of a player to draw a card. It can only happen during the player's own turn.
 * If the action is successful, a {@link it.polimi.ingsw.network.messages.responses.SomeoneDrewInitialCardMessage} response is sent to every client.
 */
public final class DrawCardMessage extends ActionMessage {
    private DrawSource source;

    /**
     * @return chosen source for the draw
     */
    public DrawSource getSource() {
        return source;
    }

    public DrawCardMessage(String username, DrawSource source) {
        super(username);
        this.source = source;
    }
}
