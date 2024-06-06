package it.polimi.ingsw.gamemodel;

public class FinalState extends MatchState {

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
