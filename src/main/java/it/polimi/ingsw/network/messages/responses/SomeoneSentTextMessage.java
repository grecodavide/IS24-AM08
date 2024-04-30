package it.polimi.ingsw.network.messages.responses;

public final class SomeoneSentTextMessage extends ResponseMessage {
    private final String text;
    private final boolean isPrivate;
    public SomeoneSentTextMessage(String username, String text, boolean isPrivate) {
        super(username);
        this.text = text;
        this.isPrivate = isPrivate;
    }

    public String getText() {
        return text;
    }

    public boolean isPrivate() {
        return isPrivate;
    }
}
