package it.polimi.ingsw.client.frontend.gui.nodes;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;


public class BoardPane extends Pane {
    public static double cardWidth = CardView.cardWidth;
    public static double cardHeight = CardView.cardHeight;
    public static double cardBorderW = 44.8;
    public static double cardBorderH = 52.6;
    public List<Pair<Integer, Integer>> takenSpots = new ArrayList<>();
    public BoardPane() {
        super();
    }

    /**
     * Add a card to the board
     * @param position relative coordinates of the card
     * @param card card to add
     * @param side side of the card to add
     */
    public void addCard(Pair<Integer, Integer> position, PlayableCard card, Side side) {
        CardView c = new CardView(card, side);
        c.getProperties().put("gameCoords", position);
        displayCard(position, c);
        takenSpots.add(position);
    }

    /**
     * Add a card to the board
     * @param position relative coordinates of the card
     * @param card card to add
     * @param side side of the card to add
     */
    public CardView addCard(Pair<Integer, Integer> position, InitialCard card, Side side) {
        CardView c = new CardView(card, side);
        displayCard(position, c);
        takenSpots.add(position);
        c.getProperties().put("gameCoords", position);
        return c;
    }

    /**
     * Sets the layout coordinates of the card and adds it to the board
     * @param position position of the card to place
     * @param c CardView of the card
     */
    private void displayCard(Pair<Integer, Integer> position, CardView c) {
        Pair<Double, Double> coords = convertCoordinates(position);
        c.setLayoutX(coords.first());
        c.setLayoutY(coords.second());
        this.getChildren().add(c);
    }

    /**
     * Convert coordinates from game coordinate to JavaFX Board coordinates
     * @param coords coordinate to convert
     * @return converted coordinates
     */
    public Pair<Double, Double> convertCoordinates(Pair<Integer, Integer> coords) {
        double boardWidth = super.getPrefWidth();
        double boardHeight = super.getPrefHeight();
        double w = boardWidth/2 + (coords.first() * (cardWidth - cardBorderW));
        double h = boardHeight/2 - (coords.second() * (cardHeight - cardBorderH));
        return new Pair<>(w, h);
    }


}
