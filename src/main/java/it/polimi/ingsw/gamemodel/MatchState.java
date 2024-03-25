package it.polimi.ingsw.gamemodel;
import it.polimi.ingsw.exceptions.*;

public abstract class MatchState {
    Match match;

    public MatchState (Match match) {
        this.match = match;
    }

    public abstract void transition() throws Exception;

    public void addPlayer() throws Exception {
        throw new WrongStateException("addPlayer not allowed from the current match state!");
    }

    public void removePlayer() throws WrongStateException{
        throw new WrongStateException("removePlayer not allowed from the current match state!");
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

    public void chooseSecretObjectives() throws WrongStateException {
        throw new WrongStateException("chooseSecretObjective not allowed from the current match state!");
    }
}
