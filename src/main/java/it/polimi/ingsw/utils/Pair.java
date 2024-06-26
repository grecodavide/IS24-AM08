package it.polimi.ingsw.utils;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents a pair of generic values.
 *
 * @param first The first value of the pair
 * @param second The second value of the pair
 * @param <T> The type of the first value
 * @param <U> The type of the second value
 */
public record Pair<T, U>(T first, U second) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
