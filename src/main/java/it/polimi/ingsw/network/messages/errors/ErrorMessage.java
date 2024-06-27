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

    /**
     * Getter for the error code of the message
     * @return error code of the message
     */
    public String getError() {
        return error;
    }

    /**
     * Getter for the message in human language
     * @return the mesage
     */
    public String getMessage() {
        return message;
    }
}
