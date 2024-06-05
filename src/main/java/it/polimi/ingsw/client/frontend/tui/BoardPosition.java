package it.polimi.ingsw.client.frontend.tui;

import java.util.Optional;
import it.polimi.ingsw.gamemodel.Corner;

/**
 * BoardPosition
 */
public record BoardPosition(boolean isValid, Optional<Corner> link) { }
