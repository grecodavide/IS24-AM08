package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * A condition must be met in order to play a golden card and to get points from the objectives. Those requirements are both represented by this class
 */
public abstract class Requirement implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Empty class constructor. The actual constructor resides in the subclasses, but this method is used to know
     * what to expect when creating a new Requirement, allowing the use of polymorphism
     *
     * @see Symbol
     */
    public Requirement() {
    }

    /**
     * Will be implemented on the concrete classes, as they have different kind of conditions
     *
     * @param board the board that will be used to check if the requirement is met
     * @return whether the requirement is met or not
     */
    public abstract int timesMet(Board board);
}
