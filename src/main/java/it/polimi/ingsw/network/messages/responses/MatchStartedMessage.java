package it.polimi.ingsw.network.messages.responses;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.gamemodel.Card;
import it.polimi.ingsw.gamemodel.Color;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;

/**
 * MatchStartedMessage
 */
public final class MatchStartedMessage extends ResponseMessage {
    private final Integer[] visibleObjectives;
    private final Map<DrawSource, Integer> visibleCards;
    private final Symbol[] visibleDeckReigns;
    private final Map<String, Integer[]> playerHands;
    private final Map<String, Color> playerPawnColors;

    public Integer[] getVisibleObjectives() {
        return visibleObjectives;
    }

    public Map<DrawSource, Integer> getVisibleCards() {
        return visibleCards;
    }

    public Symbol[] getVisibleDeckReigns() {
        return visibleDeckReigns;
    }

    public Map<String, Integer[]> getPlayerHands() {
        return playerHands;
    }

    public Map<String, Color> getPlayerPawnColors() {
        return playerPawnColors;
    }


    // TODO decide if sending the card id is the case
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
