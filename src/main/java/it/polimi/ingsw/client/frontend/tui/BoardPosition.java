package it.polimi.ingsw.client.frontend.tui;

import java.util.Optional;
import it.polimi.ingsw.gamemodel.Corner;

/**
 * Represents a position on the board, used to find valid positions and display anchor numbers when
 * a player must choose where to place the card.
 *
 * @param isValid Whether the position is a valid one or not
 * @param link The anchor point for a valid position
 *
 * @see ValidPositions
 */
public record BoardPosition(boolean isValid, Optional<Corner> link) {
}
