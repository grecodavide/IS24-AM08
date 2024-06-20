package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Highest abstraction of the card object, with common aspects for every card in
 * the game (except objectives).
 */
public abstract class Card implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected CardFace front;
    protected CardFace back;
    protected Integer id;

    public Card() {

    }

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

    
    /**
     * @return The card ID
     */
    public Integer getId() {
        return this.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Card card)) return false;

        return this.id.equals(card.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
