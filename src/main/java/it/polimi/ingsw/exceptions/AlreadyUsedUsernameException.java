package it.polimi.ingsw.exceptions;

public class AlreadyUsedUsernameException extends Exception {
    public AlreadyUsedUsernameException(String message) {
        super(message);
    }
}
