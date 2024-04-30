package it.polimi.ingsw.network.messages.responses;

/**
 * This response is sent to each user in the match when a user chooses his secret objective.
 */
public final class SomeoneChoseSecretObjectiveMessage extends ResponseMessage {
    private final Integer objectiveID;

    /**
     * @return ID of the chosen objective. Is null if the player it is sent to not the current player
     */
    public Integer getObjectiveID() {
        return objectiveID;
    }

    public SomeoneChoseSecretObjectiveMessage(String username, Integer objectiveID) {
        super(username);
        this.objectiveID = objectiveID;
    }
    
}
