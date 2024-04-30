package it.polimi.ingsw.network.messages.responses;

/**
 * This response is sent when another player sends a message in the chat. If the message is set to be private, the response is only sent to the interested user.
 */
public final class SomeoneSentTextMessage extends ResponseMessage {
    private final String text;
    private final boolean isPrivate;

    public SomeoneSentTextMessage(String username, String text, boolean isPrivate) {
        super(username);
        this.text = text;
        this.isPrivate = isPrivate;
    }

    /**
     * @return Text of the message sent
     */
    public String getText() {
        return text;
    }

    /**
     * @return true if the message is private, false otherwise
     */
    public boolean isPrivate() {
        return isPrivate;
    }
}
