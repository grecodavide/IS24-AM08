package it.polimi.ingsw.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.gamemodel.*;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Singleton that represents a collection of all cards actually existing in the Game, so only those used
 * in the Match instances.
 * It's appointed to initialise them with instances to be de-serialized from a file and make them available
 * through getters.
 */
public final class CardsManager {
    private static final CardsManager singletonInstance = new CardsManager();

    private final Map<Integer, InitialCard> initialCards;
    private final Map<Integer, GoldCard> goldCards;
    private final Map<Integer, ResourceCard> resourceCards;
    private final Map<Integer, Objective> objectives;

    /**
     * Private constructor since the singleton pattern is being used.
     * Read from the JSON files, de-serialise the content in Map<Integer, XXX> objects, initialize the private attributes
     * with these values.
     */
    // TODO: Implement Requirement deserializer
    private CardsManager() {
        CardJsonParser parser = new CardJsonParser();
        Gson gson = parser.getCardBuilder();

        Type initialCardsType = new TypeToken<Map<Integer, InitialCard>>() {
        }.getType();
        Type goldCardsType = new TypeToken<Map<Integer, GoldCard>>() {
        }.getType();
        Type resourceCardsType = new TypeToken<Map<Integer, ResourceCard>>() {
        }.getType();
        Type objectivesType = new TypeToken<Map<Integer, Objective>>() {
        }.getType();

        try {
            initialCards = gson.fromJson(new FileReader("src/main/resources/json/initial_card.json"), initialCardsType);
            goldCards = gson.fromJson(new FileReader("src/main/resources/json/gold_card.json"), goldCardsType);
            resourceCards = gson.fromJson(new FileReader("src/main/resources/json/resource_card.json"), resourceCardsType);
            objectives = gson.fromJson(new FileReader("src/main/resources/json/objective_card.json"), objectivesType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Getter for the only possible instance available of this class, so used instead of a constructor.
     *
     * @return Always the same CardsManager instance
     */
    public static CardsManager getInstance() {
        return singletonInstance;
    }

    /**
     * Getter for the initial cards
     *
     * @return Map that matches an int ID to the corresponding initial card
     */
    public Map<Integer, InitialCard> getInitialCards() {
        return initialCards;
    }

    /**
     * Getter for the gold cards
     *
     * @return Map that matches an int ID to the corresponding gold card
     */
    public Map<Integer, GoldCard> getGoldCards() {
        return goldCards;
    }

    /**
     * Getter for the resource cards
     *
     * @return Map that matches an int ID to the corresponding resource card
     */
    public Map<Integer, ResourceCard> getResourceCards() {
        return resourceCards;
    }

    /**
     * Getter for the objectives
     *
     * @return Map that matches an int ID to the corresponding objective
     */
    public Map<Integer, Objective> getObjectives() {
        return objectives;
    }

    public Map<Integer, PlayableCard> getPlayableCards() {
        Map<Integer, PlayableCard> playableCards = new HashMap<>();
        playableCards.putAll(goldCards);
        playableCards.putAll(resourceCards);

        return playableCards;
    }
}
