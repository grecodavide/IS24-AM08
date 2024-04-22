package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.gamemodel.DrawSource;

/**
 * DrawCardMessage
 */
public class DrawCardMessage extends ActionMessage {
    public DrawSource source;
    
    public DrawCardMessage(String username, DrawSource source) {
        super(username);
        this.source = source;
    }
}
