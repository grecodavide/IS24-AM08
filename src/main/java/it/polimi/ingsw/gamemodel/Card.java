package it.polimi.ingsw.gamemodel;

/**
 * Highest abstraction of the card object, with common aspects for every card in
 * the game (except objectives).
 */
public abstract class Card {
    protected CardFace front;
    protected CardFace back;
    protected Integer id;

    /**
     * Getter for the required side of the card
     *
     * @param side the required side
     * @return the structure of the specified side
     * @see CardFace
     */

    public CardFace getSide(Side side) {
        return switch (side) {
            case FRONT -> this.front;
            case BACK -> this.back;
        };
    }

    public Integer getId() {
        return this.id;
    }
}
