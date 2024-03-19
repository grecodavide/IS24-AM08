package it.polimi.ingsw.model;

public class SetupState extends MatchState{

    SetupState(Match match) {
        super(match);

        match.setupDecks();
        match.setupPlayers();
        match.setupBoards();

        this.transition();
    }

    @Override
    public void transition() {
        MatchState nextState = new NextTurnState(match);
        match.setState(nextState);
    }
}
