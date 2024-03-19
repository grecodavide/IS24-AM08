package it.polimi.ingsw.gamemodel;

/**
* Class that represents every kind of card that can be played during the game.
* All these cards have at least a side (the back) that does not require any resouce to be played.
* @see CardFace
*/

public abstract class PlayableCard extends Card{
    protected int points;
}

