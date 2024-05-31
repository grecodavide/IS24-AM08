package it.polimi.ingsw.client.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

/**
 * This class contains just elements needed to show the player's board, points, resources, hand and objectives
 */
public class ClientBoard {
    private final Map<Integer, ShownCard> placed;
    private Integer placementNumber;
    private List<PlayableCard> hand;
    private Integer points;
    private Map<Symbol, Integer> availableResources;
    private final Color color;
    private Objective objective;
    private InitialCard initialCard;

    public ClientBoard(Color color, List<PlayableCard> hand) {
        this.placementNumber = 0;
        this.placed = new HashMap<>();
        this.color = color;

        this.hand = new ArrayList<>();
        this.hand = hand;

        this.points = 0;

        this.availableResources = new HashMap<>();
        Symbol.getBasicResources().forEach((reign -> this.availableResources.put(reign, 0)));
    }

    public void setSecretObjective(Objective objective) {
        this.objective = objective;
    }

    public void placeCard(Pair<Integer, Integer> coords, PlayableCard card, Side side, Integer points, Map<Symbol, Integer> resources) {
        this.hand.remove(card);
        this.placed.put(placementNumber, new ShownCard(card, side, coords));
        this.points = points;
        this.availableResources = resources;
        this.placementNumber++;
    }

    public void drawCard(PlayableCard card) {
        this.hand.add(card);
    }

    public void setInitial(InitialCard card) {
        this.initialCard = card;
    }

    public void placeInitial(Side side, Map<Symbol, Integer> availableResources) {
        this.placed.put(placementNumber, new ShownCard(this.initialCard, side, new Pair<>(0, 0)));
        this.availableResources = availableResources;
        this.placementNumber++;
    }


    public Integer getPlacementNumber() {
        return placementNumber;
    }

    public List<PlayableCard> getHand() {
        return hand;
    }

    public Integer getPoints() {
        return points;
    }
    public Map<Integer, ShownCard> getPlaced() {
        return placed;
    }

    public Map<Symbol, Integer> getAvailableResources() {
        return availableResources;
    }

    public Color getColor() {
        return color;
    }

    public Objective getObjective() {
        return objective;
    }

}
