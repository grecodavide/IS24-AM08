package it.polimi.ingsw.gamemodel;

public class WaitState extends MatchState{

    public WaitState(Match match) {
        super(match);
    }

    @Override
    public void transition() {
        if (match.isFull()) {
            match.setupDecks();
            match.setupPlayers();
            match.setupBoards();
            
            MatchState nextState = new NextTurnState(match);
            match.setState(nextState);
        }
    }

    @Override
    public void removePlayer() {
        //TBD
    }

    @Override
    public void addPlayer() {
        //TBD
    }
}
