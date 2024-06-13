package it.polimi.ingsw.network.messages.responses;

import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.Color;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.PlacedCardRecord;

/**
 * MatchResumedMessage
 */

public final class MatchResumedMessage extends ResponseMessage {
    private final Map<String, Color> playersUsernamesAndPawns;
    private final Map<String, List<Integer>> playersHands;
    private final Pair<Integer, Integer> visibleObjectives;
    private final Map<DrawSource, Integer> visiblePlayableCards;
    private final Pair<Symbol, Symbol> decksTopReigns;
    private final Integer secretObjective;
    private final Map<String, Map<Symbol, Integer>> availableResources;
    private final Map<String, Map<Pair<Integer, Integer>, PlacedCardRecord>> placedCards;
    private final Map<String, Integer> playerPoints;
    private final String currentPlayer;
    private final boolean drawPhase;

    public MatchResumedMessage(Map<String, Color> playersUsernamesAndPawns,
            Map<String, List<Integer>> playersHands, Pair<Integer, Integer> visibleObjectives,
            Map<DrawSource, Integer> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns,
            Integer secretObjective, Map<String, Map<Symbol, Integer>> availableResources,
            Map<String, Map<Pair<Integer, Integer>, PlacedCardRecord>> placedCards,
            Map<String, Integer> playerPoints, String currentPlayer, boolean drawPhase) {
        super(null);
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
        this.drawPhase = drawPhase;
    }

    public Map<String, Color> getPlayersUsernamesAndPawns() {
        return playersUsernamesAndPawns;
    }

    public Map<String, List<Integer>> getPlayersHands() {
        return playersHands;
    }

    public Pair<Integer, Integer> getVisibleObjectives() {
        return visibleObjectives;
    }

    public Map<DrawSource, Integer> getVisiblePlayableCards() {
        return visiblePlayableCards;
    }

    public Pair<Symbol, Symbol> getDecksTopReigns() {
        return decksTopReigns;
    }

    public Integer getSecretObjective() {
        return secretObjective;
    }

    public Map<String, Map<Symbol, Integer>> getAvailableResources() {
        return availableResources;
    }

    public Map<String, Map<Pair<Integer, Integer>, PlacedCardRecord>> getPlacedCards() {
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
