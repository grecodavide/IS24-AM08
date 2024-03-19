package it.polimi.ingsw.gamemodel;

public class AfterMoveState extends MatchState {

    public AfterMoveState(Match match) {
        super(match);
    }

    @Override
    public void drawCard() throws WrongStateException {
        this.transition();
    }

    @Override
    public void transition() {
        MatchState nextState = new AfterDrawState(match);
        match.setState(nextState);
    }
}
