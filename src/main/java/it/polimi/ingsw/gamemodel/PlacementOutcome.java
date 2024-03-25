package it.polimi.ingsw.gamemodel;

/**
* Before placing a card, its placement must be verified. This enum is used to know if it is a valid one and, if it isn't, what went wrong
* @see Board#verifyCardPlacement(it.polimi.ingsw.utils.Pair, Card, Side)
*/
public enum PlacementOutcome {
    VALID,
    INVALID_COORDS,
    INVALID_ENOUGH_RESOURCES
}

