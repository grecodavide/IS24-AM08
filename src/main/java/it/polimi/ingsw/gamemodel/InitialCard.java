package it.polimi.ingsw.gamemodel;

/**
* Every player has an initial card (which will automatically be placed in the center of the board)
*/
public class InitialCard extends Card{
    /**
    * The initial card only gives corners and resources, never points, so we only need to know its topologic description
    * @param front the front side of the card
    * @param back the back side of the card
    */
    public InitialCard(CardFace front, CardFace back) {
        this.front = front; this.back = back;
    }
}
