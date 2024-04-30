package it.polimi.ingsw.network.messages.actions;

public final class SendTextMessage extends ActionMessage {

    private final String text;
    public SendTextMessage(String username, String text) {
        super(username);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
