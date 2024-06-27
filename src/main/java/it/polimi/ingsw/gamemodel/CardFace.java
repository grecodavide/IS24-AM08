package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import it.polimi.ingsw.exceptions.CardException;

/**
 * Topological definition of a card's side
 */
public class CardFace implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Symbol topLeft;
    private Symbol topRight;
    private Symbol bottomLeft;
    private Symbol bottomRight;
    private Set<Symbol> center;

    
    /**
     * Class constructor.
     * 
     * @param topLeft Top left corner
     * @param topRight Top right corner
     * @param bottomLeft Bottom left corner
     * @param bottomRight Bottom right corner
     * @param center Center of the card
     */
    public CardFace(Symbol topLeft, Symbol topRight, Symbol bottomLeft, Symbol bottomRight, Set<Symbol> center) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.center = center;
    }

    /**
     * Used to get the symbol present in one of the four corners of a card
     *
     * @param corner which of the four corners we want
     * @return the symbol the specified corner contains
     * @throws CardException if the specified corner does not exist
     */
    public Symbol getCorner(Corner corner) throws CardException {
        return switch (corner) {
            case TOP_LEFT -> this.topLeft;
            case TOP_RIGHT -> this.topRight;
            case BOTTOM_LEFT -> this.bottomLeft;
            case BOTTOM_RIGHT -> this.bottomRight;
        };
    }

    /**
     * Getter for the center of the card
     *
     * @return the set containing all symbols the center of the card contains
     */
    public Set<Symbol> getCenter() {
        return center;
    }
}
