package it.polimi.ingsw.network.messages.actions;

/**
 * SendBroadcastTextMessage
 */
public final class SendBroadcastTextMessage extends ActionMessage {
    private final String text;

    public SendBroadcastTextMessage(String username, String text) {
        super(username);
        this.text = text;
    }

    public String getText() {
        return text;
    }
    
}
