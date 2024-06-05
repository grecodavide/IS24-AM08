package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Every player has a secret objective, and at the start of the game two objectives common to every
 * player get randomly chosen. The objective asks for a certain requirement to be satisfied and gives points only when
 * the game ends, and does not stack on itself (e.g. if an objective requires three feathers and a player has 6, he will receive the points only once)
 */
public class Objective implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private int points;
    private Requirement req;
    private static Integer lastID = 0;
    private Integer id;

    /**
     * Class constructor. It is composed only of a requirement (using polymorphism) and the points it gives
     *
     * @param points the number of points the objective will give (which is always an absolute number, it never depends on any resource)
     * @param req    the requirement to satisfy in order to receive the points
     */
    public Objective(int points, Requirement req) {
        Objective.lastID++;
        this.id = Objective.lastID;
        this.points = points;
        this.req = req;
    }

    /**
     * Getter for the Objective class
     *
     * @return the points held by the objective card.
     */
    public int getPoints() {
        return this.points;
    }

    /**
     * Getter for the Objective class
     *
     * @return the requirement for the objective card to be placed.
     */
    public Requirement getReq() {
        return this.req;
    }

    public Integer getID() {
        return this.id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (!(obj instanceof Objective))
            return false;

        Objective other = (Objective) obj;
        return other.getID().equals(this.id);
    }
}

