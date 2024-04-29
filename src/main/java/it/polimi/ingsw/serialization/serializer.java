import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.util.*;


public static void main(String[] args) {
    // cards
    InitialCard initialCard;        //Set<Symbol> center = new HashSet<>(Set.of(Symbol.FUNGUS));
    Objective objectiveCard;
    ResourceCard resourceCard;
    GoldCard goldCard;

    //placeholders
    Symbol topLeft, topRight, bottomLeft, bottomRight, reign;
    int points;

    // maps
    Map<Integer, InitialCard> initialCardMap = new HashMap<>();
    Map<Integer, Objective> objectiveMap = new HashMap<>();
    Map<Integer, GoldCard> goldCardMap = new HashMap<>();
    Map<Integer, ResourceCard> resourceCardMap = new HashMap<>();

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
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.FUNGUS, 3)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.ANIMAL, 3)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.PLANT, 3)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(3, new QuantityRequirement(
                                        Map.of(Symbol.FEATHER, 1, Symbol.INKWELL, 1, Symbol.PARCHMENT, 1)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.FEATHER, 2)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.INKWELL, 2)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new QuantityRequirement(Map.of(Symbol.PARCHMENT, 2)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);
    } catch (InvalidResourceException e) {
        throw new RuntimeException(e);
    }

    //2.2) positional objective - nb: coordinates are relative
    try {
        objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.FUNGUS, new Pair<>(1,1), Symbol.FUNGUS, new Pair<>(2,2), Symbol.FUNGUS)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.ANIMAL, new Pair<>(1,1), Symbol.ANIMAL, new Pair<>(2,2), Symbol.ANIMAL)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.PLANT, new Pair<>(1,-1), Symbol.PLANT, new Pair<>(2,-2), Symbol.PLANT)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(2, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.INSECT, new Pair<>(1,-1), Symbol.INSECT, new Pair<>(2,-2), Symbol.INSECT)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.ANIMAL, new Pair<>(1,-1), Symbol.INSECT, new Pair<>(1,-2), Symbol.INSECT)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.FUNGUS, new Pair<>(-1,-1), Symbol.ANIMAL, new Pair<>(-1,-2), Symbol.ANIMAL)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.FUNGUS, new Pair<>(0,-1), Symbol.FUNGUS, new Pair<>(1,-2), Symbol.PLANT)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);

        objectiveCard = new Objective(3, new PositionRequirement(Map.of(
                new Pair<>(0,0), Symbol.PLANT, new Pair<>(0,-1), Symbol.PLANT, new Pair<>(-1,-2), Symbol.INSECT)));
        objectiveMap.put(objectiveCard.getID(), objectiveCard);
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


    //5) to json--------------------------------------------------------------------------------------------------------


}
