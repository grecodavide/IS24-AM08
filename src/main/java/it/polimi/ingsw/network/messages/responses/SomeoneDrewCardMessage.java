package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.DrawSource;

/**
 * SomeoneDrewCard
 */
public class SomeoneDrewCardMessage extends ResponseMessage {

    DrawSource source;
    Integer cardID;

    public SomeoneDrewCardMessage(String username, DrawSource source, Integer cardID) {
        super(username);
        this.source = source;
        this.cardID = cardID;
    }
}
