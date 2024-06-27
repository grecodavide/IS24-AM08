package it.polimi.ingsw.network.messages.actions;

/**
 * SendPrivateTextMessage
 */
public final class SendPrivateTextMessage extends ActionMessage {
    private final String recipient;
    private final String text;

    public SendPrivateTextMessage(String username, String recipient, String text) {
        super(username);
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
