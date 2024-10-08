package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.InvalidResourceException;

import java.io.Serial;
import java.io.Serializable;

/**
 * Card that does not require any conditions to be played.
 */
public final class ResourceCard extends PlayableCard implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Class constructor. It needs to only take the front as an argument, since the back is handled by its superclass {@link PlayableCard}
     *
     * @param front  the front side of the card
     * @param reign  the reign of the card
     * @param points the number of points the card gives (must be an absolute value)
     * @throws InvalidResourceException if the passed resource is not in {@link Symbol#getReigns()}
     */
    public ResourceCard(CardFace front, Symbol reign, int points) throws InvalidResourceException {
        super(reign);
        this.front = front;
        this.points = points;
    }

    /**
     * Getter for the ResourceCard class
     *
     * @return points held by the card
     */
    public int getPoints() {
        return this.points;
    }
}
