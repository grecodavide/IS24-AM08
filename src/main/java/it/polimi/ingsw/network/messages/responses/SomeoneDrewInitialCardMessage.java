package it.polimi.ingsw.network.messages.responses;

/**
 * This response is sent to each user in the match when a user draws an initial card.
 */
public final class SomeoneDrewInitialCardMessage extends ResponseMessage {
    private final Integer initialCardID;

    public SomeoneDrewInitialCardMessage(String username, Integer initialCardID) {
        super(username);
        this.initialCardID = initialCardID;
    }
    /* ID of the given initial card */
    public Integer getInitialCardID() {
        return initialCardID;
    }
    
}
