package it.polimi.ingsw.model;

public class NextTurnState extends MatchState {

    public NextTurnState(Match match) {
        super(match);
        match.nextPlayer();
    }

    @Override
    public void proposeSecretObjectives() throws WrongStateException {
        if (match.isStarted()) {
            throw new WrongStateException("proposeSecretObjectives called after the match was started");
        } else {
            transition();
        }
    }

    @Override
    public void makeMove() throws WrongStateException {
        if (match.isStarted()) {
            transition();
        } else {
            throw new WrongStateException("makeMove called when match was not started yet");
        }
    }

    @Override
    public void transition() {
        MatchState nextState;
        if (match.isStarted())
            nextState = new UpdatePlayerStatusState(match);
        else
            nextState = new ChooseSecretObjectiveState(match);
        match.setState(nextState);
    }
}
