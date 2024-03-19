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
}
