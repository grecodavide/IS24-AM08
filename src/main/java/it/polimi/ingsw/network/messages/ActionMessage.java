package it.polimi.ingsw.network.messages;

import java.util.Map;

/**
 * ActionMessage
 */
public class ActionMessage extends Message {
    public String actionType;
    public Map<String, Object> parameters;

    public ActionMessage(String actionType, Map<String, Object> parameters) {
        this.actionType = actionType;
        this.parameters = parameters;
    }
    
}
