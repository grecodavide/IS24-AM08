package it.polimi.ingsw.model;

public class ChooseSecretObjectiveState extends MatchState {
    public ChooseSecretObjectiveState(Match match) {
        super(match);
    }

    @Override
    public void chooseSecretObjectives() {
        Player lastPlayer = match.getPlayers().getLast();
        if (match.getCurrentPlayer().equals(lastPlayer)) {
            match.doStart();
        }

        transition();
    }

    @Override
    public void transition() {
        MatchState nextState = new NextTurnState(match);
        match.setState(nextState);
    }
}
