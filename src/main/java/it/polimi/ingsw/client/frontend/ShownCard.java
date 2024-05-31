package it.polimi.ingsw.client.frontend;

import it.polimi.ingsw.gamemodel.Card;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

/**
 * ShownCard
 *
 * @param card The card to be shown
 * @param side The side to be shown
 * @param coords The coordinates to be shown on the card (if needed)
 */
public record ShownCard(Card card, Side side, Pair<Integer, Integer> coords) {
}
