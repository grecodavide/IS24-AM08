package it.polimi.ingsw.model;

import java.util.Map;

public class Player {
    private String nickname;
    private Match match;
    private int points;
    private Board board;
    private Map<Symbol, Integer> resources;
    private Color color;
    private Objective objective;

    public Player(String nickname, Match match) {
    }

    public void playCard(Pair<Integer, Integer> coord, PlayableCard card, Side side) {
    }


}
