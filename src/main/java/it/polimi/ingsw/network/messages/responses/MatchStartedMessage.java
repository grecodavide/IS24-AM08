package it.polimi.ingsw.network.messages.responses;

import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.utils.Pair;

import java.util.HashMap;
import java.util.Map;

/**
 * MatchStartedMessage
 */
public final class MatchStartedMessage extends ResponseMessage {
    public Integer[] visibleObjectives;
    public Map<DrawSource, Integer> visibleCards;
    public Integer[] visibleDeckCards;

    // TODO decide if sending the card id is the case
    public MatchStartedMessage(Pair<Objective, Objective> objectives, Map<DrawSource, PlayableCard> cards, Pair<PlayableCard, PlayableCard> deckCards) {
        super("");
        visibleObjectives[0] = objectives.first().getID();
        visibleObjectives[1] = objectives.second().getID();
        visibleCards = new HashMap<>();
        cards.forEach((d, c) -> visibleCards.put(d, c.getId()));
        visibleDeckCards[0] = deckCards.first().getId();
        visibleDeckCards[1] = deckCards.second().getId();
    }
    
}
