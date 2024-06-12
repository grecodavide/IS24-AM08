package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

public class AfterMoveState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public AfterMoveState(Match match) {
        super(match);
    }

    @Override
    public void drawCard() {

    }

    @Override
    public void transition() {
        MatchState nextState = new AfterDrawState(match);
        match.setState(nextState);

        nextState.transition();
    }
}
