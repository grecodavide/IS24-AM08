package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;

import javax.swing.plaf.synth.SynthButtonUI;

/**
 * This response is sent to each user in the match when a user draws a card.
 */
public final class SomeoneDrewCardMessage extends ResponseMessage {
    private final DrawSource drawSource;
    private final Integer cardID;
    private final Integer replacementCardID;
    private final Pair<Symbol, Symbol> deckTopReigns;

    /**
     * @return Source from which the card is drawn.
     */
    public DrawSource getDrawSource() {
        return drawSource;
    }

    /**
     * @return ID of the card drawn by the player
     */
    public Integer getCardID() {
        return cardID;
    }

    /**
     * @return ID of the card that replaced the drawn card. Not available if the source is GOLDS_DECK or RESOURCES_DECK
     */
    public Integer getReplacementCardID() {
        return replacementCardID;
    }

    /**
     * @return Reign of the replaced card
     */
    public Pair<Symbol, Symbol> getDeckTopReigns() {
        return deckTopReigns;
    }

    public SomeoneDrewCardMessage(String username, DrawSource source, Integer cardID, Integer replacementCardID, Pair<Symbol, Symbol> deckTopReigns) {
        super(username);
        this.drawSource = source;
        this.cardID = cardID;
        this.deckTopReigns = deckTopReigns;
        if (!source.equals(DrawSource.GOLDS_DECK) && !source.equals(DrawSource.RESOURCES_DECK)) {
            this.replacementCardID = replacementCardID;
        } else {
            this.replacementCardID = null;
        }
    }

}
