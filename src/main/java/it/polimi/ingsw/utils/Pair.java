package it.polimi.ingsw.utils;

import java.io.Serial;
import java.io.Serializable;

public record Pair<T, U>(T first, U second) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
