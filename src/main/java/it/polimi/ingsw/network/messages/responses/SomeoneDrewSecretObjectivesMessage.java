package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.utils.Pair;

/**
 * SomeoneDrewSecretObjectivesMessage
 */
public final class SomeoneDrewSecretObjectivesMessage extends ResponseMessage {
    private final Integer firstID, secondID;

    public Integer getFirstID() {
        return firstID;
    }

    public Integer getSecondID() {
        return secondID;
    }

    public SomeoneDrewSecretObjectivesMessage(String username, Pair<Integer, Integer> IDs) {
        super(username);
        this.firstID = IDs.first();
        this.secondID = IDs.second();
    }
}
