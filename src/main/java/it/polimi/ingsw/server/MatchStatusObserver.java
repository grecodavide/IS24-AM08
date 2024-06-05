package it.polimi.ingsw.server;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.util.Map;

public class MatchStatusObserver implements MatchObserver {

    private final String matchName;
    private final Map<String, Match> matches;

    public MatchStatusObserver(String matchName, Map<String, Match> matches) {
        this.matchName = matchName;
        this.matches = matches;
    }

    @Override
    public void matchStarted() {

    }

    @Override
    public void someoneJoined(Player someone) {

    }

    @Override
    public void someoneQuit(Player someone) {

    }

    @Override
    public void someoneDrewInitialCard(Player someone, InitialCard card) {

    }

    @Override
    public void someoneSetInitialSide(Player someone, Side side, Map<Symbol, Integer> availableResources) {

    }

    @Override
    public void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) {

    }

    @Override
    public void someoneChoseSecretObjective(Player someone, Objective objective) {

    }

    @Override
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) {

    }

    @Override
    public void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard) {

    }

    @Override
    public void someoneSentBroadcastText(Player someone, String text) {

    }

    @Override
    public void someoneSentPrivateText(Player someone, Player recipient, String text) {

    }

    @Override
    public void matchFinished() {
        matches.remove(matchName);
    }
}
