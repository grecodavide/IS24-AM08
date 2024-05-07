package it.polimi.ingsw.network.messages.responses;

/**
 * SomeoneSentPrivateTextMessage
 */
public final class SomeoneSentPrivateTextMessage extends ResponseMessage {
    private final String recipient;
    private final String text;

    public SomeoneSentPrivateTextMessage(String sender, String recipient, String text) {
        super(sender);
        this.recipient = recipient;
        this.text = text;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getText() {
        return text;
    }
    
}
