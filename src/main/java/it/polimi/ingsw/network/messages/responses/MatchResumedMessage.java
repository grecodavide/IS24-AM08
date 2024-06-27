package it.polimi.ingsw.network.messages.responses;

import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.Color;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.PlacedCardRecord;

/**
 * This response is sent to the user who just rejoined a match. All the parameters refer to the
 * status of the match before the server crashed
 */
public final class MatchResumedMessage extends ResponseMessage {
    private final Map<String, Color> playersUsernamesAndPawns;
    private final Map<String, List<Integer>> playersHands;
    private final Pair<Integer, Integer> visibleObjectives;
    private final Map<DrawSource, Integer> visiblePlayableCards;
    private final Pair<Symbol, Symbol> decksTopReigns;
    private final Integer secretObjective;
    private final Map<String, Map<Symbol, Integer>> availableResources;
    private final Map<String, Map<Integer, PlacedCardRecord>> placedCards;
    private final Map<String, Integer> playerPoints;
    private final String currentPlayer;
    private final boolean drawPhase;

    public MatchResumedMessage(Map<String, Color> playersUsernamesAndPawns,
            Map<String, List<Integer>> playersHands, Pair<Integer, Integer> visibleObjectives,
            Map<DrawSource, Integer> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns,
            Integer secretObjective, Map<String, Map<Symbol, Integer>> availableResources,
            Map<String, Map<Integer, PlacedCardRecord>> placedCards,
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


    /**
     * @return A map from players' username to pawn color
     */
    public Map<String, Color> getPlayersUsernamesAndPawns() {
        return playersUsernamesAndPawns;
    }

    /**
     * @return A map from players' username to their hand
     */
    public Map<String, List<Integer>> getPlayersHands() {
        return playersHands;
    }

    /**
     * @return The two visible objectives common to every player
     */
    public Pair<Integer, Integer> getVisibleObjectives() {
        return visibleObjectives;
    }

    /**
     * @return The four drawable cards visible to everyone
     */
    public Map<DrawSource, Integer> getVisiblePlayableCards() {
        return visiblePlayableCards;
    }

    /**
     * @return The reign of the two decks (resource and gold)
     */
    public Pair<Symbol, Symbol> getDecksTopReigns() {
        return decksTopReigns;
    }

    /**
     * @return The secret objective ID of the player
     */
    public Integer getSecretObjective() {
        return secretObjective;
    }

    /**
     * @return A map from players' username to their available resources
     */
    public Map<String, Map<Symbol, Integer>> getAvailableResources() {
        return availableResources;
    }

    /**
     * @return A map from players' username to their board
     */
    public Map<String, Map<Integer, PlacedCardRecord>> getPlacedCards() {
        return placedCards;
    }

    /**
     * @return A map from players' username to their points
     */
    public Map<String, Integer> getPlayerPoints() {
        return playerPoints;
    }

    /**
     * @return Username of the player currently playing his turn
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * @return Whether the current player should play or draw a card
     */
    public boolean isDrawPhase() {
        return drawPhase;
    }
}
