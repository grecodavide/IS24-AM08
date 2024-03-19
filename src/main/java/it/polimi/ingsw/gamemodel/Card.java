package it.polimi.ingsw.gamemodel;

/*
* Highest abstraction of the card object, with common aspects for every card in the game (except objectives).
*/
public abstract class Card {
    protected CardFace front;
    protected CardFace back;

    /**
    * @param side the desired side
    * @return the structure of the specified side
    * @see CardFace
    */
    public CardFace getSide(Side side) {
        switch (side) {
            case FRONT:
                return this.front;

            case BACK:
                return this.back;

            default:
                return null;
        }
    }
}
