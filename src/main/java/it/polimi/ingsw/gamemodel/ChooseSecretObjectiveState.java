package it.polimi.ingsw.gamemodel;

public class ChooseSecretObjectiveState extends MatchState {

    public ChooseSecretObjectiveState(Match match) {
        super(match);
    }

    @Override
    public void chooseSecretObjective() {

    }

    @Override
    public void transition() {
        Player lastPlayer = match.getPlayers().getLast();

        if (match.getCurrentPlayer().equals(lastPlayer))
            match.doStart();

        MatchState nextState = new NextTurnState(match);
        match.setState(nextState);
    }
}
