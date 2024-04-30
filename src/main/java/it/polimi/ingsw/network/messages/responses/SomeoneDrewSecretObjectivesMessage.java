package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.utils.Pair;

/**
 * This response is sent to each user in the match when a user draws the two secret objectives.
 */
public final class SomeoneDrewSecretObjectivesMessage extends ResponseMessage {
    private final Integer firstID, secondID;

    /**
     * @return ID of the first objective card drawn. Is null if the player it is sent to not the current player
     */
    public Integer getFirstID() {
        return firstID;
    }

    /**
     * @return ID of the second objective card drawn. Is null if the player it is sent to not the current player
     */
    public Integer getSecondID() {
        return secondID;
    }

    public SomeoneDrewSecretObjectivesMessage(String username, Pair<Integer, Integer> IDs) {
        super(username);
        this.firstID = IDs.first();
        this.secondID = IDs.second();
    }
}
