package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Symbol;

/**
 * SomeoneDrewCard
 */
public final class SomeoneDrewCardMessage extends ResponseMessage {

    DrawSource drawSource;
    Integer cardID;
    Integer replacementCardID;
    Symbol replacementCardReign;

    public SomeoneDrewCardMessage(String username, DrawSource source, Integer cardID, Integer replacementCardID, Symbol replacementCardSymbol) {
        super(username);
        this.drawSource = source;
        this.cardID = cardID;
        this.replacementCardID = null;
        this.replacementCardReign = replacementCardSymbol;
        if (!source.equals(DrawSource.GOLDS_DECK) && !source.equals(DrawSource.RESOURCES_DECK)) {
            this.replacementCardID = replacementCardID;
        }
    }
}
