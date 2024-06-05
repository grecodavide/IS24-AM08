package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

/**
 * Every player has an initial card (which will automatically be placed in the
 * center of the board)
 */
public class InitialCard extends Card implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static Integer lastID = 0;

    /**
     * The initial card only gives corners and resources, never points, so we only
     * need to know its topological description
     *
     * @param front the front side of the card
     * @param back  the back side of the card
     */
    public InitialCard(CardFace front, CardFace back) {
        InitialCard.lastID++;
        this.id = InitialCard.lastID;
        this.front = front;
        this.back = back;
    }
}
