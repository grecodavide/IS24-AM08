package it.polimi.ingsw.network.messages.responses;

/**
 * SomeoneChoseSecretObjectiveMessage
 */
public final class SomeoneChoseSecretObjectiveMessage extends ResponseMessage {
    private final Integer objectiveID;

    public Integer getObjectiveID() {
        return objectiveID;
    }

    public SomeoneChoseSecretObjectiveMessage(String username, Integer objectiveID) {
        super(username);
        this.objectiveID = objectiveID;
    }
    
}
