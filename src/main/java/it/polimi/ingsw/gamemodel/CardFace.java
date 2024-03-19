package it.polimi.ingsw.gamemodel;

import java.util.Set;

/*
* Topological definition of a card's side
*/
public class CardFace {
    private Symbol topLeft;
    private Symbol topRight;
    private Symbol bottomLeft;
    private Symbol bottomRight;
    private Set<Symbol> center;

    public CardFace(Symbol topLeft, Symbol topRight, Symbol bottomLeft, Symbol bottomRight, Set<Symbol> center) {
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.center = center;
    }

    /**
    * Used to get the symbol present in one of the four corners of a card
    * @param corner which of the four corners we want
    * @return the symbol the specified corner contains
    */
    public Symbol getCorner(Corner corner) {
        return topLeft;
    }

    /**
    * @return the set containing all symbols the center of the card contains
    */
    public Set<Symbol> getCenter() {
        return center;
    }
}
