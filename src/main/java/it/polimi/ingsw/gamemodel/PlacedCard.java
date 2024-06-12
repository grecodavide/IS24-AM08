package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class handles the card already placed on the board, since we need to remember which card covers which
 */
public class PlacedCard implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Card card;
    private int turn;
    private Side playedSide;

    /**
     * Class constructor, which only needs to initialize the card and the turn it is played, as well as the side it was played on
     *
     * @param card       the {@link Card} played
     * @param playedSide the side the card was played on
     * @param turn       the turn said card is played. Needed to know which card covers which, since a card played in a certain turn will
     *                   always cover one played before
     */
    public PlacedCard(Card card, Side playedSide, int turn) {
        this.card = card;
        this.turn = turn;
        this.playedSide = playedSide;
    }

    /**
     * Getter for the PlacedCard class
     *
     * @return the card just placed.
     */
    public Card getCard() {
        return this.card;
    }

    /**
     * Getter for the PlacedCard class
     *
     * @return turn in which the card was just placed.
     */
    public int getTurn() {
        return this.turn;
    }

    /**
     * Getter for the PlacedCard class
     *
     * @return side the card was played
     */
    public Side getPlayedSide() {
        return this.playedSide;
    }


    /**
     * Getter for the Card face
     *
     * @return the topological description of the side the card was played on
     */
    public CardFace getPlayedCardFace() {
        return this.card.getSide(this.playedSide);
    }
}
