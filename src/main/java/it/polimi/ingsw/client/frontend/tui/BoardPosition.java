package it.polimi.ingsw.client.frontend.tui;

import java.util.Optional;
import it.polimi.ingsw.gamemodel.Corner;

/**
 * BoardPosition
 *
 * @param isValid Whether the position is a valid one or not
 * @param link The anchor point for a valid position
 */
public record BoardPosition(boolean isValid, Optional<Corner> link) { }
