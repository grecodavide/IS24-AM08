package it.polimi.ingsw.gamemodel;

public class ChooseInitialSideState extends MatchState {

    public ChooseInitialSideState(Match match) {
        super(match);
    }

    @Override
    public void chooseInitialSide() {
        Player lastPlayer = match.getPlayers().getLast();

        if (match.getCurrentPlayer().equals(lastPlayer))
            match.doInitialTurnFinish();

        this.transition();
    }

    @Override
    public void transition() {
        MatchState nextState = new NextTurnState(match);
        match.setState(nextState);
    }
}
