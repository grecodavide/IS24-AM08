package it.polimi.ingsw.gamemodel;

public class ChooseInitialSideState extends MatchState {

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
