package it.polimi.ingsw.gamemodel;

/**
 * All the sources a player can draw from: the decks and the four visible cards.
 */
public enum DrawSource {
    /**
     * Gold cards deck
     */
    GOLDS_DECK,
    /**
     * Resource cards deck
     */
    RESOURCES_DECK,
    /**
     * First gold card (first among all)
     */
    FIRST_VISIBLE,
    /**
     * Second gold card (second among all)
     */
    SECOND_VISIBLE,
    /**
     * First resource card (third among all)
     */
    THIRD_VISIBLE,
    /**
     * Second resource card (fourth among all)
     */
    FOURTH_VISIBLE
}
