package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

public class ChooseSecretObjectiveState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;


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
