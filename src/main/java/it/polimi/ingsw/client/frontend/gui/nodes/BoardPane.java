package it.polimi.ingsw.client.frontend.gui.nodes;

import it.polimi.ingsw.gamemodel.Card;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;


public class BoardPane extends Pane {
    public static double cardWidth = CardView.cardWidth;
    public static double cardHeight = CardView.cardHeight;
    public static double cardBorderW = 44.8;
    public static double cardBorderH = 52.6;
    public BoardPane() {
        super();
    }

    public void addCard(Pair<Integer, Integer> position, PlayableCard card, Side side) {
        CardView c = new CardView(card, side);
        displayCard(position, c);
    }

    public void addCard(Pair<Integer, Integer> position, InitialCard card, Side side) {
        CardView c = new CardView(card, side);
        displayCard(position, c);
    }

    private void displayCard(Pair<Integer, Integer> position, CardView c) {
        Pair<Double, Double> coords = convertCoordinates(position);
        c.setLayoutX(coords.first());
        c.setLayoutY(coords.second());
        this.getChildren().add(c);
    }

    public Pair<Double, Double> convertCoordinates(Pair<Integer, Integer> coords) {
        double boardWidth = super.getWidth();
        double boardHeight = super.getHeight();
        double w = boardWidth/2 + (coords.first() * (cardWidth - cardBorderW));
        double h = boardHeight/2 - (coords.second() * (cardHeight - cardBorderH));
        return new Pair<>(w, h);
    }
}
