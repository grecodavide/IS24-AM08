package it.polimi.ingsw.gamemodel;

@FunctionalInterface
public interface MatchObserverCallable {
    void call(MatchObserver matchObserver);
}