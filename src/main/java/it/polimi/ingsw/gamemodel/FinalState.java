package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

public class FinalState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public FinalState(Match match) {
        super(match);

        match.decideWinner();
    }

    @Override
    public void removePlayer() {
        // No more transition
    }

    @Override
    public void transition() {
        System.err.println("Transition tried in final state");
    }
}
