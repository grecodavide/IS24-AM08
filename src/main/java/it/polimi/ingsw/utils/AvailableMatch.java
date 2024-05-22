package it.polimi.ingsw.utils;

import java.io.Serial;
import java.io.Serializable;

/**
 * AvailableMatches
 */
public record AvailableMatch(String name, Integer maxPlayers, Integer currentPlayers) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
