package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.DrawSource;

/**
 * DrawCardMessage
 */
public final class DrawCardMessage extends ActionMessage {
    private DrawSource source;

    public DrawSource getSource() {
        return source;
    }

    public DrawCardMessage(String username, DrawSource source) {
        super(username);
        this.source = source;
    }
}
