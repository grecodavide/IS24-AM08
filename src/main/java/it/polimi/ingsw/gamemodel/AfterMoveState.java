package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.*;

public class AfterMoveState extends MatchState {

    public AfterMoveState(Match match) {
        super(match);
    }

    @Override
    public void drawCard() {
        this.transition();
    }

    @Override
    public void transition() {
        MatchState nextState = new AfterDrawState(match);
        match.setState(nextState);
    }
}
