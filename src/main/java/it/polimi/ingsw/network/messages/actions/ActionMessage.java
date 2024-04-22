package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Message;

/**
 * ActionMessage
 */
public class ActionMessage extends Message {
    public String username;
    public String action = this.getClass().getSimpleName().replace("Message", "");

    public ActionMessage(String username) {
        super();
        this.username = username;
    }

    public String toString() {
        return this.getClass().getName();
    }
    
}
