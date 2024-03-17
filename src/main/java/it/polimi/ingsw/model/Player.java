package it.polimi.ingsw.model;

import com.sun.tools.javac.util.Pair;

import java.util.Map;

public class Player {
    private String nickname;
    private Match match;
    private int points;
    private Board board;
    private Color color;
    private Objective objective;

    public Player(String nickname, Match match) {
        this.nickname = nickname;
        this.match = match;
         
        //Initialize values
        board = new Board();
        points = 0;

    }

    public void playCard(Pair<Integer, Integer> coord, PlayableCard card, Side side) {
        //TODO
    }

    public void drawcard(DrawSource source) {
        // TODO
    }

    public void chooseSecretObjective(Objective objective) {
        // TODO
    }
    public Board getBoard() {
        return board;
    }

    public int getPoints() {
        return points;
    }

    public Color getColor() {
        return color;
    }

    protected void setColor(Color color) {
        this.color = color;
    }

    protected Objective getSecretObjective() {
        return objective;
    }

    protected void addPoints(int points) {
        this.points += points;
    }


}
