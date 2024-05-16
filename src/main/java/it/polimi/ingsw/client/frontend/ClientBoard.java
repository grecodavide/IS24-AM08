package it.polimi.ingsw.client.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

/**
 * ClientBoard
 */

public class ClientBoard {
    private final Map<Integer, ShownCard> placed;
    private Integer turn;
    private List<Integer> hand;
    private Integer points;
    private Map<Symbol, Integer> resources;
    private final String username;
    private final Color color;
    private Integer objective;

    public ClientBoard(String username, Color color, Integer[] hand) {
        this.turn = 0;
        this.placed = new HashMap<>();
        this.username = username;
        this.color = color;

        this.hand = new ArrayList<>();
        for (Integer cardID : hand) {
            this.hand.add(cardID);
        }

        this.points = 0;

        this.resources = new HashMap<>();
    }

    public void setSecretObjective(Integer objectiveID) {
        this.objective = objectiveID;
    }

    public void placeCard(Pair<Integer, Integer> coords, PlayableCard card, Side side, Integer points, Map<Symbol, Integer> resources) {
        this.placed.put(turn, new ShownCard(card, side, coords));
        this.points = points;
        this.resources = resources;
        this.turn++;
    }

    public void placeInitial(InitialCard card, Side side) {
        this.placed.put(turn, new ShownCard(card, side, new Pair<>(0, 0)));
        this.turn++;
    }


    public Integer getTurn() {
        return turn;
    }

    public List<Integer> getHand() {
        return hand;
    }

    public Integer getPoints() {
        return points;
    }
    public Map<Integer, ShownCard> getPlaced() {
        return placed;
    }

    public Map<Symbol, Integer> getResources() {
        return resources;
    }

    public String getUsername() {
        return username;
    }

    public Color getColor() {
        return color;
    }

    public Integer getObjective() {
        return objective;
    }

}
