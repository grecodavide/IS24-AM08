package it.polimi.ingsw.utils;

import java.util.Map;

import it.polimi.ingsw.gamemodel.GameDeck;
import it.polimi.ingsw.gamemodel.GoldCard;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.ResourceCard;

/**
 * This is a temporary class, used to have all the logic related to deck creation in a single place, so that
 * when it will be implemented correctly we know where to modify it
 */
public class DeckCreator {

    public GameDeck<InitialCard> createInitialDeck() {
        GameDeck<InitialCard> deck = new GameDeck<>();
        Map<Integer, InitialCard> cards = CardsManager.getInstance().getInitialCards();

        for (InitialCard card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }
    
    public GameDeck<ResourceCard> createResourceDeck() {
        GameDeck<ResourceCard> deck = new GameDeck<>();
        Map<Integer, ResourceCard> cards = CardsManager.getInstance().getResourceCards();

        for (ResourceCard card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }

    public GameDeck<GoldCard> createGoldDeck() {
        GameDeck<GoldCard> deck = new GameDeck<>();
        Map<Integer, GoldCard> cards = CardsManager.getInstance().getGoldCards();

        for (GoldCard card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }

    public GameDeck<Objective> createObjectiveDeck() {
        GameDeck<Objective> deck = new GameDeck<>();
        Map<Integer, Objective> cards = CardsManager.getInstance().getObjectives();

        for (Objective card : cards.values()) {
            deck.add(card);
        }

        return deck;
    }
}
