package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

public class AfterDrawState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public AfterDrawState(Match match) {
        super(match);
    }

    @Override
    public void transition() {
        MatchState nextState;

        if (match.isFinished())
            nextState = new FinalState(match);
        else
            nextState = new NextTurnState(match);

        match.setState(nextState);
    }
}
