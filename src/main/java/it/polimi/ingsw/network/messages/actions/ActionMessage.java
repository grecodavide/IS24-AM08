package it.polimi.ingsw.network.messages.actions;

import it.polimi.ingsw.network.messages.Message;

/**
 * ActionMessage
 */
public sealed abstract class ActionMessage extends Message permits
        ChooseInitialCardSideMessage, ChooseSecretObjectiveMessage, DrawCardMessage, DrawInitialCardMessage,
        DrawSecretObjectivesMessage, PlayCardMessage {
    private String action;
    private String username;

    public ActionMessage(String username) {
        super();
        this.action = this.getClass().getSimpleName().replace("Message", "");
        this.username = username;
    }

    public String getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

}
