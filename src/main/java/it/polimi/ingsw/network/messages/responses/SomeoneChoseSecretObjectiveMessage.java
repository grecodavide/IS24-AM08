package it.polimi.ingsw.network.messages.responses;

/**
 * SomeoneChoseSecretObjectiveMessage
 */
public final class SomeoneChoseSecretObjectiveMessage extends ResponseMessage {
    public Integer objectiveID;

    public SomeoneChoseSecretObjectiveMessage(String username, Integer objectiveID) {
        super(username);
        this.objectiveID = objectiveID;
    }
    
}
