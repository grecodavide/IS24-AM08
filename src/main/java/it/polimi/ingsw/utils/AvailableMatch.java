package it.polimi.ingsw.utils;

import java.io.Serial;
import java.io.Serializable;

/**
 * AvailableMatches
 *
 * @param name The match name
 * @param maxPlayers The max number of players allowed in the match
 * @param currentPlayers The number of currently joined players
 */
public record AvailableMatch(String name, Integer maxPlayers, Integer currentPlayers) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
