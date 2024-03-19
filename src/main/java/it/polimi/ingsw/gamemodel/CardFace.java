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
}
