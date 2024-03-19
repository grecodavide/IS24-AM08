package it.polimi.ingsw.gamemodel;

/*
* Card that does not require any conditions to be played
*/
public class ResourceCard extends PlayableCard{
    /**
    * @param front the front side of the card
    * @param back the back side of the card
    * @param points the number of points the card gives (must be an absolute value)
    */
    public ResourceCard(CardFace front, CardFace back, int points) {
        this.front = front; this.back = back; this.points = points;
    }

}
