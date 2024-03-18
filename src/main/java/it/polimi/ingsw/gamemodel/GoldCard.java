package it.polimi.ingsw.gamemodel;

/**
* The front side of these cards always gives points, but needs a certain requirement to be met in order to be played
* @see CardFace
*/
public class GoldCard extends PlayableCard{
    private Symbol multiplier;
    private QuantityRequirement req;

    public GoldCard(CardFace front, CardFace back, int points, Symbol multiplier, QuantityRequirement req) {
        this.front = front;
        this.back = back;
        this.points = points;
        this.multiplier = multiplier;
        this.req = req;
    }

    /**
     * Will compute the total points this card gives based on the board it's played on
     */
    public int totPoints(Board board) {
        return this.points; // will need to compute tot resources of board and get the tot resource
    }
}
