package it.polimi.ingsw.client.frontend.gui.nodes;


import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class CardView extends Rectangle {
    public static double cardWidth = 199;
    public static double cardHeight = 132;
    public static String noCardPath = "/images/no_resource2.png";

    public CardView() {
        super(cardWidth, cardHeight);
        String imagePath = noCardPath;
        System.out.println(imagePath);
        this.getProperties().put("Card", null);
        this.addProperties(imagePath);
    }
    public CardView(InitialCard card, Side side) {
        super(cardWidth, cardHeight);
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.addProperties(imagePath);
    }

    public CardView(PlayableCard card, Side side) {
        super(cardWidth, cardHeight);
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.addProperties(imagePath);
    }

    public void setCard(InitialCard card, Side side) {
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.addProperties(imagePath);
    }

    public void setCard(PlayableCard card, Side side) {
        String imagePath = GuiUtil.getImagePath(card, side);
        this.getProperties().put("Card", card);
        this.addProperties(imagePath);
    }

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

    private void addProperties(String imagePath) {
        super.setArcHeight(20);
        super.setArcWidth(20);
        ImagePattern pattern = new ImagePattern(
                new Image(imagePath)
        );
        super.setFill(pattern);
        super.getStyleClass().add("game-card");
    }

}
