package it.polimi.ingsw.gamemodel;
import it.polimi.ingsw.exceptions.WrongStateException;

public abstract class MatchState {
    Match match;

    public MatchState (Match match) {
        this.match = match;
    }

    public abstract void transition();

    public void addPlayer() throws WrongStateException {
        throw new WrongStateException("addPlayer not allowed from the current match state!");
    }

    // TODO: Add PlayerAbortedState
    public void removePlayer() {
        // throw new WrongStateException("removePlayer not allowed from the current match state!");
        match.setState(new FinalState(match));
    }
    
    public void drawInitialCard() throws WrongStateException{
        throw new WrongStateException("deawInitialCard not allowed from the current match state!");
    }
   
    public void chooseInitialSide() throws WrongStateException{
        throw new WrongStateException("chooseInitialSide not allowed from the current match state!");
    }

    public void makeMove() throws WrongStateException{
        throw new WrongStateException("makeMove not allowed from the current match state!");
    }

    public void drawCard() throws WrongStateException{
        throw new WrongStateException("drawCard not allowed from the current match state!");
    }

    public void proposeSecretObjectives() throws WrongStateException{
        throw new WrongStateException("proposeSecretObjective not allowed from the current match state!");
    }

    public void chooseSecretObjective() throws WrongStateException {
        throw new WrongStateException("chooseSecretObjective not allowed from the current match state!");
    }
}
