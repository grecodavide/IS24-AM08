package it.polimi.ingsw.gamemodel;

/*
 * Card that does not require any conditions to be played
 * */
public class ResourceCard extends PlayableCard{
    public ResourceCard(CardFace front, CardFace back, int points) {
        this.front = front; this.back = back; this.points = points;
    }
}
