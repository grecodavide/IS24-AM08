package it.polimi.ingsw.network.messages.responses;

/**
 * SomeoneDrewInitialCardMessage
 */
public final class SomeoneDrewInitialCardMessage extends ResponseMessage {
    private final Integer initialCardID;

    public SomeoneDrewInitialCardMessage(String username, Integer initialCardID) {
        super(username);
        this.initialCardID = initialCardID;
    }

    public Integer getInitialCardID() {
        return initialCardID;
    }
    
}
