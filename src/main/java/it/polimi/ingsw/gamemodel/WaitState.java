package it.polimi.ingsw.gamemodel;

public class WaitState extends MatchState{

    public WaitState(Match match) {
        super(match);
    }

    @Override
    public void transition() throws Exception {
        MatchState nextState = new SetupState(match);
        match.setState(nextState);
    }

    @Override
    public void removePlayer() {
        //TBD
    }

    @Override
    public void addPlayer() throws Exception {
        if (match.isFull())
            transition();
    }
}
