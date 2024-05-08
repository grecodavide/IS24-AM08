package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Sent when the required amount of players is reached and the match is about to start.
 */
public final class MatchStartedMessage extends ResponseMessage {
    private final Integer[] visibleObjectives;
    private final Map<DrawSource, Integer> visibleCards;
    private final Symbol[] visibleDeckReigns;
    private final Map<String, Integer[]> playerHands;
    private final Map<String, Color> playerPawnColors;

    /**
     * @return IDs of the visible objectives
     */
    public Integer[] getVisibleObjectives() {
        return visibleObjectives;
    }

    /**
     * @return a Map that maps to each visible draw source the ID of the card
     */
    public Map<DrawSource, Integer> getVisibleCards() {
        return visibleCards;
    }

    /**
     * @return Array of Symbol that contains the reign of top-card
     * of both the gold and the resource deck, in the first and second slot respectively
     */
    public Symbol[] getVisibleDeckReigns() {
        return visibleDeckReigns;
    }

    /**
     * @return Map mapping to each player username, the list of the cards they have in the hand
     */
    public Map<String, Integer[]> getPlayerHands() {
        return playerHands;
    }

    /**
     * @return Map containing for each palyer username, the Color of their pawn
     */
    public Map<String, Color> getPlayerPawnColors() {
        return playerPawnColors;
    }


    /**
     * Calculates the needed parameters given some information from the match
     *
     * @param objectives Pair containing the two visible objectives
     * @param cards      Map that for each visible draw source maps the visible card
     * @param deckReigns Pair containing the reign of the two visible cards on top of the deck.
     *                   The first is for the Golds deck, while the second for the resources deck.
     * @param players    List of the players in the match
     */
    public MatchStartedMessage(Pair<Objective, Objective> objectives, Map<DrawSource, PlayableCard> cards, Pair<Symbol, Symbol> deckReigns, List<Player> players) {
        super(null);
        this.visibleObjectives = new Integer[]{null, null};
        this.visibleDeckReigns = new Symbol[]{null, null};
        visibleObjectives[0] = objectives.first().getID();
        visibleObjectives[1] = objectives.second().getID();
        visibleCards = new HashMap<>();
        cards.forEach((d, c) -> visibleCards.put(d, c.getId()));
        visibleDeckReigns[0] = deckReigns.first();
        visibleDeckReigns[1] = deckReigns.second();
        // Calculate player hands and colors
        playerHands = new HashMap<String, Integer[]>();
        playerPawnColors = new HashMap<String, Color>();
        for (Player p : players) {
            Integer[] result;
            result = p.getBoard().getCurrentHand().stream()
                    .mapToInt(Card::getId)
                    .boxed().toArray(Integer[]::new);
            playerPawnColors.put(p.getNickname(), p.getPawnColor());
            playerHands.put(p.getNickname(), result);
        }
    }

}
