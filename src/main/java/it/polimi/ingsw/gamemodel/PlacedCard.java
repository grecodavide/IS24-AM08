package it.polimi.ingsw.gamemodel;

/**
* This class handles the card already placed on the board, since we need to remember which card covers which
*/
public class PlacedCard {
    private Card card;
    private int turn;

    public PlacedCard(Card card, int turn) {
        this.card = card; this.turn = turn;
    }
}
