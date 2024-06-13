package it.polimi.ingsw.utils;

import it.polimi.ingsw.gamemodel.Side;

/**
 * PlacedCardRecord
 */
public record PlacedCardRecord(Integer cardID, Integer turn, Side side) {
}
