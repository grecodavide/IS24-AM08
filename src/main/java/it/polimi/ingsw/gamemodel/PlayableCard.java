package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.InvalidResourceException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * Class that represents every kind of card that can be played during the game.
 * All these cards have at least a side (the back) that does not require any
 * resource to be played.
 *
 * @see CardFace
 */
public abstract sealed class PlayableCard extends Card implements Serializable permits GoldCard, ResourceCard {
    @Serial
    private static final long serialVersionUID = 1L;

    protected int points;
    protected Symbol reign;
    private static Integer lastID = 0;

    /**
     * Constructor for PlayableCard. Since the only common aspects are that they
     * have a reign and that the back is made up of
     * only full corners (no resources) and the center gives a resource of their
     * reign
     *
     * @param reign the reign of the card
     * @throws InvalidResourceException if the passed resource is not in
     *                                  {@link Symbol#getReigns()}
     */
    public PlayableCard(Symbol reign) throws InvalidResourceException {
        PlayableCard.lastID++;
        this.id = PlayableCard.lastID;
        if (Symbol.getReigns().contains(reign)) {
            this.points = 0;
            this.reign = reign;
            this.back = new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER,
                    Set.of(reign));
        } else {
            throw new InvalidResourceException(
                    "Resource " + reign.toString() + " is not valid for a " + this.getClass());
        }
    }

    public PlayableCard() {

    }

    /**
     * Getter for the card reign
     *
     * @return the card's reign
     * @see Symbol#getReigns()
     */
    public Symbol getReign() {
        return this.reign;
    }
}
