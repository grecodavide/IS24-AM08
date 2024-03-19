package it.polimi.ingsw.gamemodel;

/**
* The front side of these cards always gives points, but needs a certain requirement to be met in order to be played
* @see CardFace
*/
public class GoldCard extends PlayableCard{
    private Symbol multiplier;
    private QuantityRequirement req;

    /**
    * The front side always gives points based on the quantity of a certain resource, while the back always gives a 
    * resource of the card's faction
    * @param front the front side of the card
    * @param back the back side of the card
    * @param multiplier the symbol whose number of resources multiplies the points parameter
    * @param points the number every resource of the given type is worth
    * @param req the requirement that must be met in order to be able to play the card
    */
    public GoldCard(CardFace front, CardFace back, Symbol multiplier, int points, QuantityRequirement req) {
        this.front = front;
        this.back = back;
        this.points = points;
        this.multiplier = multiplier;
        this.req = req;
    }

    /**
    * Will compute the total points this card gives based on the board it's played on
    * @param board The board on which we want to compute the points this card will give
    */
    public int totPoints(Board board) {
        return this.points; // will need to compute tot resources of board and get the tot resource
    }
}
