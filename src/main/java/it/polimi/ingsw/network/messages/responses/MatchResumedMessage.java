package it.polimi.ingsw.network.messages.responses;

import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

/**
 * MatchResumedMessage
 */

public final class MatchResumedMessage extends ResponseMessage {
    private final Map<String, Color> playersUsernamesAndPawns;
    private final Map<String, List<PlayableCard>> playersHands;
    private final Pair<Objective, Objective> visibleObjectives;
    private final Map<DrawSource, PlayableCard> visiblePlayableCards;
    private final Pair<Symbol, Symbol> decksTopReigns;
    private final Objective secretObjective;
    private final Map<String, Map<Symbol, Integer>> availableResources;
    private final Map<String, Map<Pair<Integer, Integer>, PlacedCard>> placedCards;
    private final Map<String, Integer> playerPoints;
    private final String currentPlayer;
    private final boolean drawPhase;

    public MatchResumedMessage(String username, boolean drawPhase,
            Map<String, Color> playersUsernamesAndPawns,
            Map<String, List<PlayableCard>> playersHands,
            Pair<Objective, Objective> visibleObjectives,
            Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns,
            Objective secretObjective, Map<String, Map<Symbol, Integer>> availableResources,
            Map<String, Map<Pair<Integer, Integer>, PlacedCard>> placedCards,
            Map<String, Integer> playerPoints, String currentPlayer) {
        super(username);
        this.drawPhase = drawPhase;
        this.playersUsernamesAndPawns = playersUsernamesAndPawns;
        this.playersHands = playersHands;
        this.visibleObjectives = visibleObjectives;
        this.visiblePlayableCards = visiblePlayableCards;
        this.decksTopReigns = decksTopReigns;
        this.secretObjective = secretObjective;
        this.availableResources = availableResources;
        this.placedCards = placedCards;
        this.playerPoints = playerPoints;
        this.currentPlayer = currentPlayer;
    }

    public Map<String, Color> getPlayersUsernamesAndPawns() {
        return playersUsernamesAndPawns;
    }

    public Map<String, List<PlayableCard>> getPlayersHands() {
        return playersHands;
    }

    public Pair<Objective, Objective> getVisibleObjectives() {
        return visibleObjectives;
    }

    public Map<DrawSource, PlayableCard> getVisiblePlayableCards() {
        return visiblePlayableCards;
    }

    public Pair<Symbol, Symbol> getDecksTopReigns() {
        return decksTopReigns;
    }

    public Objective getSecretObjective() {
        return secretObjective;
    }

    public Map<String, Map<Symbol, Integer>> getAvailableResources() {
        return availableResources;
    }

    public Map<String, Map<Pair<Integer, Integer>, PlacedCard>> getPlacedCards() {
        return placedCards;
    }

    public Map<String, Integer> getPlayerPoints() {
        return playerPoints;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean isDrawPhase() {
        return drawPhase;
    }

}


