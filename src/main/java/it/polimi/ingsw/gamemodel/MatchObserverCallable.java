package it.polimi.ingsw.gamemodel;

/**
 * Functional interface used to represents a method call ({@link #call(MatchObserver)}) on a {@link MatchObserver}.
 */
@FunctionalInterface
public interface MatchObserverCallable {
    /**
     * Generic method call on a {@link MatchObserver}.
     *
     * @param matchObserver The match observer on which to call a method
     */
    void call(MatchObserver matchObserver);
}