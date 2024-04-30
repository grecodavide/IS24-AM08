package it.polimi.ingsw.network.messages.actions;

/**
 * The action sends a text message in the chat.
 * A {@link it.polimi.ingsw.network.messages.responses.SomeoneSentTextMessage} response is sent to every client
 */
public final class SendTextMessage extends ActionMessage {
    private final String recipient;
    private final String text;

    public SendTextMessage(String username, String recipient, String text) {
        super(username);
        this.text = text;
        this.recipient = recipient;
    }

    /**
     * @return Text of the message sent
     */
    public String getText() {
        return text;
    }

    /**
     * @return Recipient of the message, null if the message is global
     */
    public String getRecipient() {
        return recipient;
    }
}
