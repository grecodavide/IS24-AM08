package it.polimi.ingsw.model;

public class WaitState extends MatchState{

    public WaitState(Match match) {
        super(match);
    }
    @Override
    public void transition() {
        MatchState nextState = new SetupState(match);
        match.setState(nextState);
    }

    @Override
    public void removePlayer() {
        //TBD
    }

    @Override
    public void addPlayer() {
        if (match.isFull()) {
            transition();
        }
    }
}
