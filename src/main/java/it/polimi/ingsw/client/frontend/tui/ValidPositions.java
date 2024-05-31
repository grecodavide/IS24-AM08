package it.polimi.ingsw.client.frontend.tui;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.gamemodel.CardFace;
import it.polimi.ingsw.gamemodel.Corner;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;

/**
 * ValidPositions
 */

public class ValidPositions {
    private final Map<Pair<Integer, Integer>, BoardPosition> coordinates;
    private static final Map<Corner, Pair<Integer, Integer>> offsets =
            Map.of(Corner.TOP_LEFT, new Pair<Integer, Integer>(-1, 1), Corner.TOP_RIGHT, new Pair<Integer, Integer>(1, 1),
                    Corner.BOTTOM_LEFT, new Pair<Integer, Integer>(-1, -1), Corner.BOTTOM_RIGHT, new Pair<Integer, Integer>(1, -1));

    public ValidPositions() {
        this.coordinates = new HashMap<>();
    }

    private Pair<Integer, Integer> offsetCoord(Pair<Integer, Integer> coord, Pair<Integer, Integer> offset) {
        return new Pair<Integer, Integer>(coord.first() + offset.first(), coord.second() + offset.second());
    }

    public boolean isValid(Pair<Integer, Integer> coord) {
        if (this.coordinates.get(coord) == null) {
            return false;
        }

        return this.coordinates.get(coord).isValid();
    }

    public Map<Pair<Integer, Integer>, Pair<Integer, Corner>> getValidPlaces() {
        Map<Pair<Integer, Integer>, Pair<Integer, Corner>> valids = new HashMap<>();
        int pos = 1;
        for (Pair<Integer, Integer> coord : this.coordinates.keySet()) {
            if (this.coordinates.get(coord).isValid()) {
                valids.put(coord, new Pair<Integer,Corner>(pos, this.coordinates.get(coord).link().get()));
                pos++;
            }
        }

        return valids;
    }

    public void addCard(ShownCard card) {
        CardFace cardFace = card.card().getSide(card.side());
        Pair<Integer, Integer> coord = card.coords();
        this.coordinates.put(coord, new BoardPosition(false, Optional.empty()));
        for (Corner corner : offsets.keySet()) {
            try {
                if (!cardFace.getCorner(corner).equals(Symbol.EMPTY_CORNER)) {
                    this.coordinates.putIfAbsent(this.offsetCoord(coord, offsets.get(corner)), new BoardPosition(true, Optional.of(corner)));
                } else {
                    this.coordinates.put(this.offsetCoord(coord, offsets.get(corner)), new BoardPosition(false, Optional.empty()));
                }
            } catch (CardException e) {
            }
        }
    }
}
