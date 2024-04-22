package it.polimi.ingsw.network.messages.responses;

/**
 * SomeoneChoseSecretObjectiveMessage
 */
public class SomeoneChoseSecretObjectiveMessage extends ResponseMessage {
    public Integer objectiveID;

    public SomeoneChoseSecretObjectiveMessage(String username, Integer objectiveID) {
        super(username);
        this.objectiveID = objectiveID;
    }
    
}
