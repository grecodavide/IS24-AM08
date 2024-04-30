package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Symbol;

/**
 * SomeoneDrewCard
 */
public final class SomeoneDrewCardMessage extends ResponseMessage {
    private final DrawSource drawSource;
    private final Integer cardID;
    private final Integer replacementCardID;
    private final Symbol replacementCardReign;

    public DrawSource getDrawSource() {
        return drawSource;
    }

    public Integer getCardID() {
        return cardID;
    }

    public Integer getReplacementCardID() {
        return replacementCardID;
    }

    public Symbol getReplacementCardReign() {
        return replacementCardReign;
    }

    public SomeoneDrewCardMessage(String username, DrawSource source, Integer cardID, Integer replacementCardID, Symbol replacementCardSymbol) {
        super(username);
        this.drawSource = source;
        this.cardID = cardID;
        this.replacementCardReign = replacementCardSymbol;
        if (!source.equals(DrawSource.GOLDS_DECK) && !source.equals(DrawSource.RESOURCES_DECK)) {
            this.replacementCardID = replacementCardID;
        } else {
            this.replacementCardID = null;
        }
    }

}
