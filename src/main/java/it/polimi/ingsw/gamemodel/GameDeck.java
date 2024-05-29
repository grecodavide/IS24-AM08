package it.polimi.ingsw.gamemodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import it.polimi.ingsw.exceptions.DeckException;

/**
 * Generic used to create the decks for all the types of cards
 *
 * @param <T> the type of deck, which can be a {@link ResourceCard}, {@link GoldCard}, {@link InitialCard} or {@link Objective}
 */
public class GameDeck<T> {
    private List<T> cardsList;

    /**
     * Class constructor, takes no argument as the decks start empty
     */
    public GameDeck() {
        cardsList = new ArrayList<>();
    }

    /**
     * Adds a card to the bottom of the deck
     *
     * @param card the card to add
     */
    public void add(T card) {
        this.cardsList.addFirst(card);
    }

    /**
     * Getter for the deck's size
     *
     * @return the deck's size
     */
    public int getSize() {
        return this.cardsList.size();
    }

    /**
     * Removes a card from the deck's top (throws exception if the deck is empty)
     *
     * @return the removed card
     * @throws DeckException if the deck is empty
     */
    public T pop() throws DeckException {
        if (this.isEmpty())
            throw new DeckException("Tried to draw from an empty deck!");
        return cardsList.removeLast();
    }

    /**
     * Returns a card from the deck's top without removing it (returns null if empty)
     *
     * @return the card
     */
    public T peek() {
        if (this.isEmpty())
            return null;
        return cardsList.getLast();
    }

    /**
     * Removes a card from the deck's top (null if the deck is empty)
     *
     * @return the removed card (null if the deck is empty)
     */
    public T poll() {
        if (this.isEmpty())
            return null;
        return cardsList.removeLast();
    }

    /**
     * Shuffles the deck
     */
    public void shuffle() {
        Collections.shuffle(this.cardsList);
    }

    /**
     * Checks whether the deck is empty or not
     *
     * @return whether the deck is empty or not
     */
    public boolean isEmpty() {
        return this.cardsList.isEmpty();
    }
}
