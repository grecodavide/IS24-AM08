package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

public class ChooseInitialSideState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public ChooseInitialSideState(Match match) {
        super(match);
    }

    @Override
    public void chooseInitialSide() {

    }

    @Override
    public void transition() {
        Player lastPlayer = match.getPlayers().getLast();

        if (match.getCurrentPlayer().equals(lastPlayer))
            match.doInitialTurnFinish();
        MatchState nextState = new NextTurnState(match);
        match.setState(nextState);
    }
}
