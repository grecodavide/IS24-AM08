package it.polimi.ingsw.network.messages;

import java.util.Map;

/**
 * ResponseMessage
 */
public class ResponseMessage extends Message {
    public String responseType;
    public Map<String, Object> parameters;

    public ResponseMessage(String actionType, Map<String, Object> parameters) {
        this.responseType = actionType;
        this.parameters = parameters;
    }
    
}
