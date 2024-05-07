package it.polimi.ingsw.network.messages.responses;

/**
 * SomeoneSentBroadcastTextMessage
 */
public final class SomeoneSentBroadcastTextMessage extends ResponseMessage {
    private final String text;

    public SomeoneSentBroadcastTextMessage(String username, String text) {
        super(username);
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
}
