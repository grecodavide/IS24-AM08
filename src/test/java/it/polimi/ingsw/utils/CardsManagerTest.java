package it.polimi.ingsw.utils;

import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.gamemodel.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CardsManagerTest {
    CardsManager cardsManager = CardsManager.getInstance();

    @Test
    public void getInitialCards() throws CardException {
        Map<Integer, InitialCard> initialCards = cardsManager.getInitialCards();

        CardFace front = initialCards.get(1).getSide(Side.FRONT);
        CardFace back = initialCards.get(1).getSide(Side.BACK);

        assertEquals("Initial card id.1 has wrong id", Integer.valueOf(1), initialCards.get(1).getId());

        assertEquals("Initial card id.1 has wrong front top left corner", Symbol.INSECT, front.getCorner(Corner.TOP_LEFT));
        assertEquals("Initial card id.1 has wrong front top right corner", Symbol.FUNGUS, front.getCorner(Corner.TOP_RIGHT));
        assertEquals("Initial card id.1 has wrong front bottom left corner", Symbol.PLANT, front.getCorner(Corner.BOTTOM_LEFT));
        assertEquals("Initial card id.1 has wrong front bottom right corner", Symbol.ANIMAL, front.getCorner(Corner.BOTTOM_RIGHT));
        assertTrue("Initial card id.1 has wrong front center", front.getCenter().isEmpty());

        Set<Symbol> centerExpectedValues = new HashSet<>();
        centerExpectedValues.add(Symbol.INSECT);
        centerExpectedValues.add(Symbol.PLANT);
        centerExpectedValues.add(Symbol.ANIMAL);

        assertEquals("Initial card id.1 has wrong back top left corner", Symbol.FULL_CORNER, back.getCorner(Corner.TOP_LEFT));
        assertEquals("Initial card id.1 has wrong back top right corner", Symbol.FULL_CORNER, back.getCorner(Corner.TOP_RIGHT));
        assertEquals("Initial card id.1 has wrong back bottom left corner", Symbol.EMPTY_CORNER, back.getCorner(Corner.BOTTOM_LEFT));
        assertEquals("Initial card id.1 has wrong back bottom right corner", Symbol.EMPTY_CORNER, back.getCorner(Corner.BOTTOM_RIGHT));
        assertTrue("Initial card id.1 has wrong back center", back.getCenter().containsAll(centerExpectedValues));
    }

    @Test
    public void getGoldCards() throws CardException, InvalidResourceException {
        Map<Integer, GoldCard> goldCards = cardsManager.getGoldCards();

        GoldCard card = goldCards.get(64);

        CardFace front = card.getSide(Side.FRONT);
        CardFace back = card.getSide(Side.BACK);

        Map<Symbol, Integer> reqMap = new LinkedHashMap<>();
        reqMap.put(Symbol.ANIMAL, 1);
        reqMap.put(Symbol.PLANT, 3);
        QuantityRequirement req = new QuantityRequirement(reqMap);

        assertEquals("Gold card id.64 has wrong id", Integer.valueOf(64), card.getId());
        assertEquals("Gold card id.64 has wrong multiplier", Symbol.CORNER_OBJ, card.getMultiplier());
        assertEquals("Gold card id.64 has wrong reign", Symbol.PLANT, card.getReign());

        assertEquals("Gold card id.64 has wrong front top left corner", Symbol.FULL_CORNER, front.getCorner(Corner.TOP_LEFT));
        assertEquals("Gold card id.64 has wrong front top right corner", Symbol.FULL_CORNER, front.getCorner(Corner.TOP_RIGHT));
        assertEquals("Gold card id.64 has wrong front bottom left corner", Symbol.FULL_CORNER, front.getCorner(Corner.BOTTOM_LEFT));
        assertEquals("Gold card id.64 has wrong front bottom right corner", Symbol.EMPTY_CORNER, front.getCorner(Corner.BOTTOM_RIGHT));
        assertTrue("Gold card id.64 has wrong front center", front.getCenter().isEmpty());

        Set<Symbol> centerExpectedValues = new HashSet<>();
        centerExpectedValues.add(Symbol.PLANT);

        assertEquals("Gold card id.64 has wrong back top left corner", Symbol.FULL_CORNER, back.getCorner(Corner.TOP_LEFT));
        assertEquals("Gold card id.64 has wrong back top right corner", Symbol.FULL_CORNER, back.getCorner(Corner.TOP_RIGHT));
        assertEquals("Gold card id.64 has wrong back bottom left corner", Symbol.FULL_CORNER, back.getCorner(Corner.BOTTOM_LEFT));
        assertEquals("Gold card id.64 has wrong back bottom right corner", Symbol.FULL_CORNER, back.getCorner(Corner.BOTTOM_RIGHT));
        assertTrue("Gold card id.64 has wrong back center", back.getCenter().containsAll(centerExpectedValues));
    }

    @Test
    public void getResourceCard() throws CardException, InvalidResourceException {
        Map<Integer, ResourceCard> resourceCards = cardsManager.getResourceCards();

        ResourceCard card = resourceCards.get(1);

        CardFace front = card.getSide(Side.FRONT);
        CardFace back = card.getSide(Side.BACK);

        assertEquals("Resource card id.1 has wrong id", Integer.valueOf(1), card.getId());
        assertEquals("Resource card id.1 has wrong reign", Symbol.INSECT, card.getReign());

        assertEquals("Resource card id.1 has wrong front top left corner", Symbol.EMPTY_CORNER, front.getCorner(Corner.TOP_LEFT));
        assertEquals("Resource card id.1 has wrong front top right corner", Symbol.FULL_CORNER, front.getCorner(Corner.TOP_RIGHT));
        assertEquals("Resource card id.1 has wrong front bottom left corner", Symbol.INSECT, front.getCorner(Corner.BOTTOM_LEFT));
        assertEquals("Resource card id.1 has wrong front bottom right corner", Symbol.INSECT, front.getCorner(Corner.BOTTOM_RIGHT));
        assertTrue("Resource card id.1 has wrong front center", front.getCenter().isEmpty());

        Set<Symbol> centerExpectedValues = new HashSet<>();
        centerExpectedValues.add(Symbol.INSECT);

        assertEquals("Resource card id.1 has wrong back top left corner", Symbol.FULL_CORNER, back.getCorner(Corner.TOP_LEFT));
        assertEquals("Resource card id.1 has wrong back top right corner", Symbol.FULL_CORNER, back.getCorner(Corner.TOP_RIGHT));
        assertEquals("Resource card id.1 has wrong back bottom left corner", Symbol.FULL_CORNER, back.getCorner(Corner.BOTTOM_LEFT));
        assertEquals("Resource card id.1 has wrong back bottom right corner", Symbol.FULL_CORNER, back.getCorner(Corner.BOTTOM_RIGHT));
        assertTrue("Resource card id.1 has wrong back center", back.getCenter().containsAll(centerExpectedValues));
    }

    @Test
    public void getObjectives() {
        Map<Integer, Objective> objectives = cardsManager.getObjectives();

        Objective obj = objectives.get(1);

        assertEquals("Objective id.1 has wrong id", Integer.valueOf(1), obj.getID());
        assertEquals("Objective id.1 has wrong points", 2, obj.getPoints());
        assertTrue("Objective id.1 has wrong requirement type", obj.getReq() instanceof QuantityRequirement);
    }

}
