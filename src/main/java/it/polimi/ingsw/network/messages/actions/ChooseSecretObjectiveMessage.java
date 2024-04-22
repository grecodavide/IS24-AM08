package it.polimi.ingsw.network.messages.actions;

/**
 * ChooseSecretObjectiveMessage
 */
public class ChooseSecretObjectiveMessage extends ActionMessage {
    public Integer objectiveID;

    public ChooseSecretObjectiveMessage(String username, Integer objectiveID) {
        super(username);
        this.objectiveID = objectiveID;
    }
    
}
