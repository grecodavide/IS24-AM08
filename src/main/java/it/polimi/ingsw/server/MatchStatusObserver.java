package it.polimi.ingsw.server;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.io.*;
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
        serializeMatch();
    }

    @Override
    public void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard) {
        serializeMatch();
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
        removeSerializedMatch();
    }

    /**
     * Utility method that serializes the match and saves it in the disk
     */
    private void serializeMatch() {
        try {
            FileOutputStream fileOut = new FileOutputStream(matchName + ".match");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            Match m = matches.get(matchName);
            synchronized (m) {
                out.writeObject(m);
                out.close();
                fileOut.close();
            }
        } catch (IOException e) {
            System.err.println("The match \"" + matchName + "\" cannot be serialized due to I/O errors");
        }
    }

    private void removeSerializedMatch() {
        File file = new File(matchName + ".match");
        file.delete();
    }
}

