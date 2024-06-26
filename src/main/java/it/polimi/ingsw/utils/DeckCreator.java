package it.polimi.ingsw.utils;

import it.polimi.ingsw.gamemodel.*;

import java.util.Map;

/**
 * This is a temporary class, used to have all the logic related to deck creation in a single place, so that
 * when it will be implemented correctly we know where to modify it
 */
public class DeckCreator {

    /**
     * Create the deck of initial cards
     * @return a gamedeck of initial cards
     */
    public GameDeck<InitialCard> createInitialDeck() {
        GameDeck<InitialCard> deck = new GameDeck<>();
        Map<Integer, InitialCard> cards = CardsManager.getInstance().getInitialCards();

        for (InitialCard card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }

    /**
     * Create the deck of resource cards
     * @return a gamedeck of resource cards
     */
    public GameDeck<ResourceCard> createResourceDeck() {
        GameDeck<ResourceCard> deck = new GameDeck<>();
        Map<Integer, ResourceCard> cards = CardsManager.getInstance().getResourceCards();

        for (ResourceCard card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }

    /**
     * Create the deck of gold cards
     * @return a gamedeck of gold cards
     */
    public GameDeck<GoldCard> createGoldDeck() {
        GameDeck<GoldCard> deck = new GameDeck<>();
        Map<Integer, GoldCard> cards = CardsManager.getInstance().getGoldCards();

        for (GoldCard card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }

    /**
     * Create the deck of objective cards
     * @return a gamedeck of objective cards
     */
    public GameDeck<Objective> createObjectiveDeck() {
        GameDeck<Objective> deck = new GameDeck<>();
        Map<Integer, Objective> cards = CardsManager.getInstance().getObjectives();

        for (Objective card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }
}
