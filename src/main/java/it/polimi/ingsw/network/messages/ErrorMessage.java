package it.polimi.ingsw.network.messages;

/**
 * ErrorMessage
 */
public class ErrorMessage extends Message {
    public String error;
    public String message;

    public ErrorMessage(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
