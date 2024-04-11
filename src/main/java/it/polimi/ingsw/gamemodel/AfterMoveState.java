package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.WrongStateException;

public class AfterMoveState extends MatchState {

    public AfterMoveState(Match match) {
        super(match);
    }

    @Override
    public void drawCard() throws WrongStateException {

    }

    @Override
    public void transition() {
        MatchState nextState = new AfterDrawState(match);
        match.setState(nextState);

        nextState.transition();
    }
}
