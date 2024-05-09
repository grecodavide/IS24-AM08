import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class serializer {
    public static void main(String[] args) {
        // cards
        InitialCard initialCard;
        Objective objectiveCard;
        ResourceCard resourceCard;
        GoldCard goldCard;

        //placeholders
        Symbol topLeft, topRight, bottomLeft, bottomRight, reign, multiplier;
        int points;
        QuantityRequirement requirements;

        // maps
        Map<Integer, InitialCard> initialCardMap = new HashMap<>();
        Map<Integer, Objective> objectiveCardMap = new HashMap<>();
        Map<Integer, ResourceCard> resourceCardMap = new HashMap<>();
        Map<Integer, GoldCard> goldCardMap = new HashMap<>();

        //1) initial cards map----------------------------------------------------------------------------------------------
        initialCard = new InitialCard(
                new CardFace(Symbol.INSECT, Symbol.FUNGUS, Symbol.PLANT, Symbol.ANIMAL, Collections.emptySet()),
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER,
                        new HashSet<>(Set.of(Symbol.ANIMAL, Symbol.INSECT, Symbol.PLANT)))
        );
        initialCardMap.put(initialCard.getId(), initialCard);

        initialCard = new InitialCard(
                new CardFace(Symbol.PLANT, Symbol.ANIMAL, Symbol.FUNGUS, Symbol.INSECT, Collections.emptySet()),
                new CardFace(Symbol.ANIMAL, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FUNGUS,
                        new HashSet<>(Set.of(Symbol.FUNGUS)))
        );
        initialCardMap.put(initialCard.getId(), initialCard);

        initialCard = new InitialCard(
                new CardFace(Symbol.INSECT, Symbol.ANIMAL, Symbol.FUNGUS, Symbol.PLANT, Collections.emptySet()),
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER,
                        new HashSet<>(Set.of(Symbol.PLANT, Symbol.FUNGUS)))
        );
        initialCardMap.put(initialCard.getId(), initialCard);

        initialCard = new InitialCard(
                new CardFace(Symbol.FUNGUS, Symbol.PLANT, Symbol.INSECT, Symbol.ANIMAL, Collections.emptySet()),
                new CardFace(Symbol.FULL_CORNER, Symbol.PLANT, Symbol.INSECT, Symbol.FULL_CORNER,
                        new HashSet<>(Set.of(Symbol.INSECT)))
        );
        initialCardMap.put(initialCard.getId(), initialCard);

        initialCard = new InitialCard(
                new CardFace(Symbol.PLANT, Symbol.INSECT, Symbol.ANIMAL, Symbol.FUNGUS, Collections.emptySet()),
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.FULL_CORNER,
                        new HashSet<>(Set.of(Symbol.ANIMAL, Symbol.INSECT)))
        );
        initialCardMap.put(initialCard.getId(), initialCard);

        initialCard = new InitialCard(
                new CardFace(Symbol.FUNGUS, Symbol.ANIMAL, Symbol.PLANT, Symbol.INSECT, Collections.emptySet()),
                new CardFace(Symbol.FULL_CORNER, Symbol.FULL_CORNER, Symbol.EMPTY_CORNER, Symbol.EMPTY_CORNER,
                        new HashSet<>(Set.of(Symbol.PLANT, Symbol.ANIMAL, Symbol.FUNGUS)))
        );
        initialCardMap.put(initialCard.getId(), initialCard);

        //2) objective cards map--------------------------------------------------------------------------------------------
        //2.1) quantity objective

        try {
            objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.INSECT, 3)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.FUNGUS, 3)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.ANIMAL, 3)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.PLANT, 3)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(3, new QuantityRequirement(
                    Map.of(Symbol.FEATHER, 1, Symbol.INKWELL, 1, Symbol.PARCHMENT, 1)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.FEATHER, 2)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.INKWELL, 2)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.PARCHMENT, 2)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        //2.2) positional objective - nb: coordinates are relative
        try {
            objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.FUNGUS, new Pair<>(1, 1), Symbol.FUNGUS, new Pair<>(2, 2), Symbol.FUNGUS)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.ANIMAL, new Pair<>(1, 1), Symbol.ANIMAL, new Pair<>(2, 2), Symbol.ANIMAL)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.PLANT, new Pair<>(1, -1), Symbol.PLANT, new Pair<>(2, -2), Symbol.PLANT)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.INSECT, new Pair<>(1, -1), Symbol.INSECT, new Pair<>(2, -2), Symbol.INSECT)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.ANIMAL, new Pair<>(1, -1), Symbol.INSECT, new Pair<>(1, -2), Symbol.INSECT)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.FUNGUS, new Pair<>(-1, -1), Symbol.ANIMAL, new Pair<>(-1, -2), Symbol.ANIMAL)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.FUNGUS, new Pair<>(0, -1), Symbol.FUNGUS, new Pair<>(1, -2), Symbol.PLANT)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);

            objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                    new Pair<>(0, 0), Symbol.PLANT, new Pair<>(0, -1), Symbol.PLANT, new Pair<>(-1, -2), Symbol.INSECT)));
            objectiveCardMap.put(objectiveCard.getID(), objectiveCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        //3) resource cards map---------------------------------------------------------------------------------------------
        //3.1) zero points
        points = 0;

        try {
            reign = Symbol.INSECT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.INSECT;
            bottomRight = Symbol.INSECT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FEATHER;
            bottomLeft = Symbol.ANIMAL;
            bottomRight = Symbol.INSECT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.INSECT;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.INSECT;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.PLANT;
            topRight = Symbol.PLANT;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.ANIMAL;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.ANIMAL;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.INSECT;
            topRight = Symbol.PLANT;
            bottomLeft = Symbol.INKWELL;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FUNGUS;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FUNGUS;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FUNGUS;
            topRight = Symbol.FUNGUS;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.ANIMAL;
            bottomRight = Symbol.ANIMAL;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FUNGUS;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FUNGUS;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.INSECT;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.INSECT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.PLANT;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.PLANT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.INSECT;
            bottomLeft = Symbol.FEATHER;
            bottomRight = Symbol.PLANT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FUNGUS;
            topRight = Symbol.PLANT;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.INKWELL;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.INSECT;
            bottomLeft = Symbol.INKWELL;
            bottomRight = Symbol.ANIMAL;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.PARCHMENT;
            topRight = Symbol.INSECT;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FUNGUS;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FEATHER;
            bottomLeft = Symbol.PLANT;
            bottomRight = Symbol.FUNGUS;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FUNGUS;
            topRight = Symbol.INSECT;
            bottomLeft = Symbol.PARCHMENT;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.INKWELL;
            topRight = Symbol.FUNGUS;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.ANIMAL;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.PLANT;
            topRight = Symbol.ANIMAL;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.PARCHMENT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FUNGUS;
            bottomRight = Symbol.FUNGUS;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.ANIMAL;
            topRight = Symbol.ANIMAL;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.PLANT;
            bottomRight = Symbol.PLANT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.PARCHMENT;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.PLANT;
            bottomRight = Symbol.ANIMAL;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FEATHER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.ANIMAL;
            bottomRight = Symbol.FUNGUS;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.PLANT;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.PLANT;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.INSECT;
            topRight = Symbol.INSECT;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        //3.2) one point
        points = 1;

        try {
            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.INSECT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FUNGUS;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FUNGUS;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.ANIMAL;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.PLANT;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.ANIMAL;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FUNGUS;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.ANIMAL;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.INSECT;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.PLANT;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.PLANT;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.INSECT;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.ANIMAL;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.ANIMAL;
            bottomRight = Symbol.FULL_CORNER;
            resourceCard = new ResourceCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, points);
            resourceCardMap.put(resourceCard.getId(), resourceCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        //4) gold cards map-------------------------------------------------------------------------------------------------
        //4.1) with object multiplier (1pt)
        points = 1;

        try {
            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.PARCHMENT;
            bottomRight = Symbol.FULL_CORNER;
            multiplier = Symbol.PARCHMENT;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 2, Symbol.ANIMAL, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.PARCHMENT;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            multiplier = Symbol.PARCHMENT;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 2, Symbol.INSECT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.PARCHMENT;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            multiplier = Symbol.PARCHMENT;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 2, Symbol.FUNGUS, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.PARCHMENT;
            multiplier = Symbol.PARCHMENT;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 2, Symbol.PLANT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FEATHER;
            multiplier = Symbol.FEATHER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 2, Symbol.ANIMAL, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FEATHER;
            bottomRight = Symbol.FULL_CORNER;
            multiplier = Symbol.FEATHER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 2, Symbol.FUNGUS, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FEATHER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            multiplier = Symbol.FEATHER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 2, Symbol.INSECT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FEATHER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            multiplier = Symbol.FEATHER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 2, Symbol.PLANT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.INKWELL;
            bottomRight = Symbol.FULL_CORNER;
            multiplier = Symbol.INKWELL;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 2, Symbol.ANIMAL, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.INKWELL;
            multiplier = Symbol.INKWELL;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 2, Symbol.FUNGUS, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.INKWELL;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            multiplier = Symbol.INKWELL;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 2, Symbol.INSECT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.INKWELL;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            multiplier = Symbol.INKWELL;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 2, Symbol.PLANT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        //4.2) with corner multiplier (2pt)
        points = 2;
        multiplier = Symbol.CORNER_OBJ;

        try {
            reign = Symbol.ANIMAL;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 3, Symbol.PLANT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 3, Symbol.INSECT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 3, Symbol.FUNGUS, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 3, Symbol.ANIMAL, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 3, Symbol.PLANT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 3, Symbol.INSECT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 3, Symbol.ANIMAL, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 3, Symbol.PLANT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 3, Symbol.FUNGUS, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 3, Symbol.INSECT, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 3, Symbol.FUNGUS, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 3, Symbol.ANIMAL, 1));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        //4.3) with no multiplier (3/5pt)
        points = 3;
        multiplier = Symbol.NO_MULT;

        try {
            reign = Symbol.ANIMAL;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FEATHER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.PARCHMENT;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.ANIMAL;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.INKWELL;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.INKWELL;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FEATHER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.PARCHMENT;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.INKWELL;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.PARCHMENT;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FEATHER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.PARCHMENT;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FEATHER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.INKWELL;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 3));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        points = 5;
        multiplier = Symbol.NO_MULT;

        try {
            reign = Symbol.ANIMAL;
            topLeft = Symbol.EMPTY_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.FULL_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.ANIMAL, 5));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.FUNGUS;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.EMPTY_CORNER;
            bottomLeft = Symbol.FULL_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.FUNGUS, 5));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.INSECT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.INSECT, 5));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);

            reign = Symbol.PLANT;
            topLeft = Symbol.FULL_CORNER;
            topRight = Symbol.FULL_CORNER;
            bottomLeft = Symbol.EMPTY_CORNER;
            bottomRight = Symbol.EMPTY_CORNER;
            requirements = new QuantityRequirement(Map.of(Symbol.PLANT, 5));
            goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
            goldCardMap.put(goldCard.getId(), goldCard);
        } catch (InvalidResourceException e) {
            throw new RuntimeException(e);
        }

        //5) to json--------------------------------------------------------------------------------------------------------
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            String path = "";
            System.out.println(gson.toJson(initialCardMap));
            FileWriter file;
            file = new FileWriter("initial_card.json");
            file.write(gson.toJson(initialCardMap));
            file.close();

            file = new FileWriter("objective_card.json");
            file.write(gson.toJson(objectiveCardMap));
            file.close();

            file = new FileWriter("resource_card.json");
            file.write(gson.toJson(resourceCardMap));
            file.close();

            file = new FileWriter("gold_card.json");
            file.write(gson.toJson(goldCardMap));
            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


/*
    reign = Symbol.;
    topLeft = Symbol.;
    topRight = Symbol.;
    bottomLeft = Symbol.;
    bottomRight = Symbol.;
    requirements = new QuantityRequirement(Map.of(Symbol., 5));
    goldCard = new GoldCard(new CardFace(topLeft, topRight, bottomLeft, bottomRight, Collections.emptySet()), reign, multiplier, points, requirements);
    goldCardMap.put(goldCard.getId(), goldCard);
 */
}