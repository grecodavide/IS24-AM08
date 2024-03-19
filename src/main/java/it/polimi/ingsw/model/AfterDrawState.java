package it.polimi.ingsw.model;

public class AfterDrawState extends MatchState {

    public AfterDrawState(Match match) {
        super(match);
    }

    @Override
    public void transition() {
        MatchState nextState;

        if(match.isFinished())
            nextState = new FinalState(match);
        else
            nextState = new NextTurnState(match);

        match.setState(nextState);
    }
}
