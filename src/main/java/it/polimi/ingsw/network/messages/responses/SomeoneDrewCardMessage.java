package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.DrawSource;

/**
 * SomeoneDrewCard
 */
public final class SomeoneDrewCardMessage extends ResponseMessage {

    DrawSource source;
    Integer cardID;
    Integer replacementCardID;

    // TODO: update table
    public SomeoneDrewCardMessage(String username, DrawSource source, Integer cardID, Integer replacementCardID) {
        super(username);
        this.source = source;
        this.cardID = cardID;
        this.replacementCardID = replacementCardID;
    }
}
