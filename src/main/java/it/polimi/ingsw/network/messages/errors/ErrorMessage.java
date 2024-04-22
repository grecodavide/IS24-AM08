package it.polimi.ingsw.network.messages.errors;

import it.polimi.ingsw.network.messages.Message;

/**
 * ErrorMessage
 */
public class ErrorMessage extends Message {
    public String message;
    public String error;

    public ErrorMessage(String message, String error) {
        this.message = message;
        this.error = error;
    }
    
}
