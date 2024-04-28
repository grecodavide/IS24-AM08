package it.polimi.ingsw.network.messages.actions;

/**
 * ChooseSecretObjectiveMessage
 */
public final class ChooseSecretObjectiveMessage extends ActionMessage {
    private Integer objectiveID;

    public Integer getObjectiveID() {
        return objectiveID;
    }

    public ChooseSecretObjectiveMessage(String username, Integer objectiveID) {
        super(username);
        this.objectiveID = objectiveID;
    }

}
