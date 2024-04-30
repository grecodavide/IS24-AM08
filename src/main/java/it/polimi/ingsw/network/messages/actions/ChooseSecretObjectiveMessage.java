package it.polimi.ingsw.network.messages.actions;

/**
 * The action communicates the intention of a player to choose his secret objective. It can only happen before the first turn of the game.
 * If the action is successful, a {@link it.polimi.ingsw.network.messages.responses.SomeoneChoseSecretObjectiveMessage} response is sent to every client.
 */
public final class ChooseSecretObjectiveMessage extends ActionMessage {
    private Integer objectiveID;

    /**
     * @return Chosen objective
     */
    public Integer getObjectiveID() {
        return objectiveID;
    }

    public ChooseSecretObjectiveMessage(String username, Integer objectiveID) {
        super(username);
        this.objectiveID = objectiveID;
    }

}
