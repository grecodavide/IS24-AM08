package it.polimi.ingsw.gamemodel;

/**
* This class handles the card already placed on the board, since we need to remember which card covers which
*/
public class PlacedCard {
    private Card card;
    private int turn;


    /**
    * @param card the {@link Card} played
    * @param turn the turn said card is played. Needed to know which card covers which, since a card played in a certain turn will
    * always cover one played before
    */
    public PlacedCard(Card card, int turn) {
        this.card = card; this.turn = turn;
    }

    /**
    * Getter for the PlacedCard class
    * @return the card just placed.
    */
    public Card getCard() {
        return this.card;
    }

    /**
    * Getter for the PlacedCard class
    * @return turn in which the card was just placed.
    */
    public int getTurn() {
        return this.turn;
    }
}
