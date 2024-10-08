package it.polimi.ingsw.client.frontend.gui.nodes;


import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import javax.xml.crypto.dom.DOMCryptoContext;
import java.util.HashMap;
import java.util.Map;

public class CardView extends Pane {
    // Card dimensions
    public static double cardWidth = 199;
    public static double cardHeight = 132;
    public static double cardBorderW = 44.8;
    public static double cardBorderH = 52.6;

    /** Pawn dimensions (at center of the initial card) */
    public static double tokenRadius = 28;

    /** Path to what to show when a card is missing */
    public static String noCardPath = "/images/no_resource2.png";
    // Card corners
    public Pane topLeftCorner;
    public Pane topRightCorner;
    public Pane bottomLeftCorner;
    public Pane bottomRightCorner;

    private Image image;
    private Rectangle cardRectangle;

    /**
     * Initialize an empty CardView
     */
    public CardView() {
        super();
        String imagePath = noCardPath;
        this.getProperties().put("Card", null);
        this.getProperties().put("Side", null);
        this.addProperties(imagePath);
    }

    /**
     * Initialize a CardView of an Initial Card
     * @param card initial card
     * @param side side to show
     */
    public CardView(InitialCard card, Side side) {
        super();
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.getProperties().put("Side", side);
        this.addProperties(imagePath);
    }

    /**
     * Initialize a CardView of a Playable Card
     * @param card playable card
     * @param side side to show
     */
    public CardView(PlayableCard card, Side side) {
        super();
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.getProperties().put("Side", side);
        this.addProperties(imagePath);
    }

    /**
     * Initialize a CardView of an Objective Card
     * @param card objective
     * @param side side to show
     */
    public CardView(Objective card, Side side) {
        super();
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.getProperties().put("Side", side);
        this.addProperties(imagePath);
    }

    /**
     * Change the displayed card
     * @param card card to display
     * @param side side of the card to display
     */
    public void setCard(InitialCard card, Side side) {
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.getProperties().put("Side", side);
        this.addProperties(imagePath);
    }

    /**
     * Change the displayed card
     * @param card card to display
     * @param side side of the card to display
     */
    public void setCard(PlayableCard card, Side side) {
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.getProperties().put("Side", side);
        this.addProperties(imagePath);
    }

    /**
     * Change the displayed card
     * @param card card to display
     * @param side side of the card to display
     */
    public void setCard(Objective card, Side side) {
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.getProperties().put("Side", side);
        this.addProperties(imagePath);
    }

    /**
     * Change the displayed card to the back of a resource card
     * @param reign reign to display
     */
    public void setResourcesCardBack(Symbol reign) {
        String imagePath;
        this.getProperties().put("Card", null);
        this.getProperties().put("Side", Side.BACK);
        if (reign != null) {
            imagePath = GuiUtil.getResourcesBack(reign);
        } else {
            imagePath = noCardPath;
        }
        addProperties(imagePath);
    }

    /**
     * Change the displayed card to the back of a gold card
     * @param reign reign to display
     */
    public void setGoldsCardBack(Symbol reign) {
        String imagePath;
        this.getProperties().put("Card", null);
        this.getProperties().put("Side", Side.BACK);
        if (reign != null) {
            imagePath = GuiUtil.getGoldsBack(reign);
        } else {
            imagePath = noCardPath;
        }
        addProperties(imagePath);
    }

    /**
     * Add the properties to the image
     * @param imagePath
     */
    private void addProperties(String imagePath) {
        super.setPrefHeight(cardHeight);
        super.setPrefWidth(cardWidth);
        super.setMaxHeight(cardHeight);
        super.setMaxWidth(cardWidth);
        Rectangle rect = new Rectangle(cardWidth, cardHeight);
        image = new Image(imagePath);
        ImagePattern pattern = new ImagePattern(
                image
        );
        rect.setFill(pattern);
        cardRectangle = rect;
        setArc(20);
        super.getChildren().add(rect);
        super.getStyleClass().add("game-card");
        addCorners();
    }

    /**
     * Set arcHeight and arcWidth of the card
     * @param arc arc width and height
     */
    public void setArc(double arc) {
        cardRectangle.setArcHeight(arc);
        cardRectangle.setArcWidth(arc);
    }


    /**
     * Add corners to the card
     */
    private void addCorners() {
        topLeftCorner = new Pane();
        topRightCorner = new Pane();
        bottomLeftCorner= new Pane();
        bottomRightCorner = new Pane();

        setCornerProperties(topLeftCorner, 0, 0);
        setCornerProperties(topRightCorner, cardWidth-cardBorderW,0);
        setCornerProperties(bottomRightCorner, cardWidth-cardBorderW,cardHeight-cardBorderH);
        setCornerProperties(bottomLeftCorner, 0,cardHeight-cardBorderH);
    }

    /**
     * Add position and dimensions to a corner ad add them in the card
     * @param corner The corner to add the properties to
     * @param x position
     * @param y position
     */
    private void setCornerProperties(Pane corner, double x, double y) {
        corner.setPrefWidth(cardBorderW);
        corner.setPrefHeight(cardBorderH);
        corner.setLayoutX(x);
        corner.setLayoutY(y);
        super.getChildren().add(corner);
    }

    /**
     * Display token of a color on the initial card
     * @param color color of the token
     */
    public void setToken(Color color) {
        double tokenY = 64;
        Map<Side, Pair<Double, Double>> coords = new HashMap<>();
        coords.put(Side.FRONT, new Pair<>(78.0, 121.0));
        coords.put(Side.BACK, new Pair<>(62.0, 137.0));

        Side side = (Side)super.getProperties().get("Side");

        ImageView token = new ImageView(new Image(GuiUtil.getPawnImagePath(color)));
        token.setFitWidth(tokenRadius);
        token.setFitHeight(tokenRadius);

        token.setLayoutX(coords.get(side).first() - tokenRadius/2);
        token.setLayoutY(tokenY - tokenRadius / 2);

        if (color.equals(Color.RED)) {
            ImageView blackToken = new ImageView(GuiUtil.getBlackPawnImagePath());
            blackToken.setFitWidth(tokenRadius);
            blackToken.setFitHeight(tokenRadius);
            blackToken.setLayoutX(coords.get(side).second() - tokenRadius/2);
            blackToken.setLayoutY(tokenY - tokenRadius/2);
            super.getChildren().add(blackToken);
        }
        super.getChildren().add(token);
    }

}
