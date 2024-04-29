package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MatchStartedMessage
 */
public final class MatchStartedMessage extends ResponseMessage {
    public Integer[] visibleObjectives;
    public Map<DrawSource, Integer> visibleCards;
    public Symbol[] visibleDeckReigns;
    public Map<String, Integer[]> playerHands;
    public Map<String, Color> playerPawnColors;

    // TODO decide if sending the card id is the case
    public MatchStartedMessage(Pair<Objective, Objective> objectives, Map<DrawSource, PlayableCard> cards, Pair<PlayableCard, PlayableCard> deckCards, List<Player> players) {
        super(null);
        this.visibleObjectives = new Integer[]{null, null};
        this.visibleDeckReigns = new Symbol[]{null, null};
        visibleObjectives[0] = objectives.first().getID();
        visibleObjectives[1] = objectives.second().getID();
        visibleCards = new HashMap<>();
        cards.forEach((d, c) -> visibleCards.put(d, c.getId()));
        visibleDeckReigns[0] = deckCards.first().getReign();
        visibleDeckReigns[1] = deckCards.second().getReign();
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
