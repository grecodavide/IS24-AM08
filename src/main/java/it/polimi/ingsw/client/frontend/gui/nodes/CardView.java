package it.polimi.ingsw.client.frontend.gui.nodes;


import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class CardView extends Pane {
    public static double cardWidth = 199;
    public static double cardHeight = 132;
    public static double cardBorderW = 44.8;
    public static double cardBorderH = 52.6;
    public static String noCardPath = "/images/no_resource2.png";
    public Pane topLeftCorner;
    public Pane topRightCorner;
    public Pane bottomLeftCorner;
    public Pane bottomRightCorner;

    public CardView() {
        super();
        String imagePath = noCardPath;
        System.out.println(imagePath);
        this.getProperties().put("Card", null);
        this.addProperties(imagePath);
    }
    public CardView(InitialCard card, Side side) {
        super();
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.addProperties(imagePath);
    }

    public CardView(PlayableCard card, Side side) {
        super();
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
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
        this.addProperties(imagePath);
    }

    /**
     * Change the displayed card to the back of a resource card
     * @param reign reign to display
     */
    public void setResourcesCardBack(Symbol reign) {
        String imagePath;
        this.getProperties().put("Card", null);
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
        Rectangle rect = new Rectangle(cardWidth, cardHeight);
        rect.setArcHeight(20);
        rect.setArcWidth(20);
        ImagePattern pattern = new ImagePattern(
                new Image(imagePath)
        );
        rect.setFill(pattern);
        super.getChildren().add(rect);
        super.getStyleClass().add("game-card");
        addCorners();
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

}
