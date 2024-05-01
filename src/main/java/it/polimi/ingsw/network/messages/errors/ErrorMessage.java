package it.polimi.ingsw.network.messages.errors;

import it.polimi.ingsw.network.messages.Message;

/**
 * Sent to the clients when an error happens
 */
public class ErrorMessage extends Message {
    private final String message;
    private final String error;

    public ErrorMessage(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }
}
