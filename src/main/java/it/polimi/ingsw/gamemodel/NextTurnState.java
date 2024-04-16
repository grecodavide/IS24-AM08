package it.polimi.ingsw.gamemodel;
import it.polimi.ingsw.exceptions.WrongStateException;

public class NextTurnState extends MatchState {

    public NextTurnState(Match match) {
        super(match);
        match.nextPlayer();
    }

    @Override
    public void proposeSecretObjectives() throws WrongStateException {
        if (match.isStarted() || !match.isInitialTurnFinished())
            throw new WrongStateException("proposeSecretObjectives called after the match was started");
    }

    @Override
    public void makeMove() throws WrongStateException {
        if (!match.isStarted())
            throw new WrongStateException("makeMove called when match was not started yet");
    }

    @Override
    public void drawInitialCard() throws WrongStateException {
        if (match.isInitialTurnFinished())
            throw new WrongStateException("drawInitialCard called after the initial turn was finished");
    }

    @Override
    public void transition() {
        MatchState nextState;

        if (match.isStarted())
            nextState = new AfterMoveState(match);
        else if (!match.isInitialTurnFinished())
            nextState = new ChooseInitialSideState(match);
        else
            nextState = new ChooseSecretObjectiveState(match);

        match.setState(nextState);
    }
}
