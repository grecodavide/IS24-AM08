package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;

public class WaitState extends MatchState implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public WaitState(Match match) {
        super(match);
    }

    @Override
    public void transition() {
        synchronized (match) {
            if (match.isFull()) {
                match.setupDecks();
                match.setupPlayers();
                match.setupBoards();

                MatchState nextState = new NextTurnState(match);
                match.setState(nextState);

                // Notify observers
                match.notifyMatchStart();
            }
        }
    }

    @Override
    public void removePlayer() {
    }

    @Override
    public void addPlayer() {
    }
}
