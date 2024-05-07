package it.polimi.ingsw.network.messages.actions;

/**
 * SendPrivateTextMessage
 */
public final class SendPrivateTextMessage extends ActionMessage {
    private final String recpient;
    private final String text;

    public SendPrivateTextMessage(String username, String recpient, String text) {
        super(username);
        this.recpient = recpient;
        this.text = text;
    }

    public String getRecpient() {
        return recpient;
    }

    public String getText() {
        return text;
    }
    
}
