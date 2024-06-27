package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Message;

/**
 * Messages sent by clients to the server to express a user intention to do an
 * action
 */
public sealed abstract class ActionMessage extends Message permits ChooseInitialCardSideMessage, ChooseSecretObjectiveMessage,
        CreateMatchMessage, DrawCardMessage, DrawInitialCardMessage, DrawSecretObjectivesMessage, GetAvailableMatchesMessage,
        JoinMatchMessage, PlayCardMessage, SendBroadcastTextMessage, SendPrivateTextMessage {
    private String action;
    private String username;

    
    /**
     * Class constructor.
     * 
     * @param username The player who wants to perform the action
     */
    public ActionMessage(String username) {
        super();
        this.action = this.getClass().getSimpleName().replace("Message", "");
        this.username = username;
    }

    /**
     * @return name of the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @return username of the player
     */
    public String getUsername() {
        return username;
    }

}
