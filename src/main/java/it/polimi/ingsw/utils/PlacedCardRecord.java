package it.polimi.ingsw.utils;

import it.polimi.ingsw.gamemodel.Side;

/**
 * PlacedCardRecord
 *
 * @param cardID The card ID
 * @param x The x coordinate
 * @param y The y coordinate
 * @param side The chosen side
 */
public record PlacedCardRecord(Integer cardID, Integer x, Integer y, Side side) {
}
