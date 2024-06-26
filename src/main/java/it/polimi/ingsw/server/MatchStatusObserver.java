package it.polimi.ingsw.server;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.io.*;
import java.util.Map;

/**
 * Subclass of {@link MatchObserver} supposed to perform routine actions, which means actions that are to be
 * performed when there's a state transition, BUT that are not related to a specific player.
 * It's current main function is to serialize the match from which it receives notifications.
 */
public class MatchStatusObserver implements MatchObserver {
    private final String matchName;
    private final Map<String, Match> matches;

    /**
     * Initializes this instance main attributes.
     *
     * @param matchName The match name to which this instance is attached and listens to.
     * @param matches All the matches of the server
     */
    public MatchStatusObserver(String matchName, Map<String, Match> matches) {
        this.matchName = matchName;
        this.matches = matches;
    }

    /**
     * Does nothing.
     */
    @Override
    public void matchStarted() {
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneJoined(Player someone) {
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneQuit(Player someone) {
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneDrewInitialCard(Player someone, InitialCard card) {
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneSetInitialSide(Player someone, Side side, Map<Symbol, Integer> availableResources) {
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) {
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneChoseSecretObjective(Player someone, Objective objective) {
    }

    /**
     * Serializes the match and saves it in the disk. This method parameters are not used.
     *
     * @param someone Not used by this method.
     * @param coords  Not used by this method.
     * @param card    Not used by this method.
     * @param side    Not used by this method.
     */
    @Override
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        serializeMatch();
    }

    /**
     * Serializes the match and saves it in the disk. This method parameters are not used.
     *
     * @param someone Not used by this method.
     * @param card    Not used by this method.
     */
    @Override
    public void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard) {
        serializeMatch();
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneSentBroadcastText(Player someone, String text) {
    }

    /**
     * Does nothing.
     */
    @Override
    public void someoneSentPrivateText(Player someone, Player recipient, String text) {
    }

    /**
     * Removes this match serialization file from the disk and removes this match instance from the list of matches
     * available in the {@link Server}.
     */
    @Override
    public void matchFinished() {
        matches.remove(matchName);
        removeSerializedMatch();
    }

    /**
     * Utility method that serializes the match and saves it in the disk.
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

    // Utility method that removes a serialized match from the disk.
    private void removeSerializedMatch() {
        File file = new File(matchName + ".match");
        file.delete();
    }
}

