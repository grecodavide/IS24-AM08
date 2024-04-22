package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.network.messages.Message;

/**
 * ResponseMessage
 */
public class ResponseMessage extends Message {
    public String username;

    public ResponseMessage(String username) {
        this.username = username;
    }
    
}
