package it.polimi.ingsw.client.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
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

    public ClientBoard(Integer[] hand) {
        this.turn = 0;
        this.placed = new HashMap<>();

        this.hand = new ArrayList<>();
        for (Integer cardID : hand) {
            this.hand.add(cardID);
        }

        this.points = 0;

        this.resources = new HashMap<>();
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


}
