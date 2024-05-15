package it.polimi.ingsw.client.frontend;

import it.polimi.ingsw.gamemodel.Card;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

/**
 * ShownCard
 */
public record ShownCard(Card card, Side side, Pair<Integer, Integer> coords) {
}
