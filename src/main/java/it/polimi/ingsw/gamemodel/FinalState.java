package it.polimi.ingsw.gamemodel;

public class FinalState extends MatchState {

    public FinalState(Match match) {
        super(match);
    }

    @Override
    public void transition() {
        System.err.println("ERROR: State transition tried in the final state!");
    }
}
