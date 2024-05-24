package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.nodes.BoardPane;
import it.polimi.ingsw.client.frontend.gui.nodes.CardView;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerTabController extends SceneController{
    private String username;
    @FXML
    CardView handCard1;
    @FXML
    CardView handCard2;
    @FXML
    CardView handCard3;
    @FXML
    BoardPane playerBoard;
    @FXML
    ScrollPane scroll;
    @FXML
    HBox resourcesCounter;
    @FXML
    Label pointsCounter;
    private List<Node> temporaryDragAreas = new ArrayList<>();

    public void initialize() {
        scroll.getStyleClass().clear();

        handCard1.setCard(CardsManager.getInstance().getPlayableCards().get(45), Side.FRONT);
        handCard2.setOnMouseClicked((e) -> {
            this.testBoard();
        });
        initializeHandCard(handCard1);
        initializeHandCard(handCard2);
        initializeHandCard(handCard3);

        HashMap<Symbol, Integer> res = new HashMap<>();
        for (Symbol s : Symbol.getBasicResources()) {
            res.put(s, 0);
        }
        setResources(res);
    }

    public void testBoard() {
        for (int i=0; i<5; i++) {
            playerBoard.addCard(new Pair<>(i, i), CardsManager.getInstance().getResourceCards().get(i+1), Side.FRONT);
        }
        for (int i = 1; i < 5; i++) {
            playerBoard.addCard(new Pair<>(i, -i), CardsManager.getInstance().getResourceCards().get(i+1), Side.FRONT);
        }
    }

    /**
     * Set the displayed resources
     * @param resources map to the resources amount
     */
    public void setResources(Map<Symbol, Integer> resources) {
        resourcesCounter.getChildren().clear();
        Symbol[] order = new Symbol[]{Symbol.FUNGUS, Symbol.PLANT, Symbol.ANIMAL, Symbol.INSECT,
                Symbol.FEATHER, Symbol.PARCHMENT, Symbol.INKWELL};
        for (Symbol s : order) {
            ImageView icon = new ImageView(new Image("/images/symbols/" + s.toString().toUpperCase() + ".png"));
            icon.setFitHeight(40);
            icon.setFitWidth(40);
            Label count = new Label(String.valueOf(resources.get(s)));
            count.getStyleClass().add("resources-count");
            resourcesCounter.getChildren().add(icon);
            resourcesCounter.getChildren().add(count);
        }
    }

    /**
     * Places a card on the board
     * @param coords
     * @param card
     * @param side
     */
    public void placeCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        playerBoard.addCard(coords, card, side);
    }

    public void setPoints(int points) {
        pointsCounter.setText("Points: " + points);
    }

    // Drag and drop management

    /**
     * Add attributes to the hand card
     * @param card CardView to add attributes
     */
    private void initializeHandCard(CardView card) {
        card.setOnDragDetected(event -> {
            // Set Dragboard content
            Dragboard dragboard = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("");
            dragboard.setContent(content);
            // Set the card as image of the dragboard
            dragboard.setDragView(card.snapshot(null, null));
            dragboard.setDragViewOffsetX(CardView.cardWidth/2);
            dragboard.setDragViewOffsetY(CardView.cardHeight/2);
            this.createDragArea((PlayableCard) card.getProperties().get("Card"), (Side) card.getProperties().get("Side"));
            card.setVisible(false);
            event.consume();
        });
        card.setOnDragDone(event -> {
            this.removeDragAreas();
            card.setVisible(true);
            event.consume();
        });
    }

    /**
     * Create all possible drag areas on the board
     * @param card Card to place
     * @param side Side on which place the card
     */
    private void createDragArea(PlayableCard card, Side side) {
        List<Pair<Integer, Integer>> placeableCoords = new ArrayList<>();
        for (Pair<Integer, Integer> coords : playerBoard.takenSpots ) {
            for (int i = -1; i < 2; i++) {
                for (int j = -1; j < 2; j++) {
                    if (i != 0 && j != 0) {
                        Pair<Integer, Integer> c = new Pair<>(coords.first() + i, coords.second() + j);
                        if (!playerBoard.takenSpots.contains(c)) placeableCoords.add(c);
                    }
                }
            }
        }
        for (Pair<Integer, Integer> c : placeableCoords) {
            showDragArea(c, card, side);
        }
    }

    /**
     * Create a drag area in the specified coordinates, for the specified card on a certain side
     * @param pcoords Game coordinates on which to put the area
     * @param card Card where to put the area
     * @param side Side on which to put the card
     */
    private void showDragArea(Pair<Integer, Integer> pcoords, PlayableCard card, Side side) {
        Pane dragArea = new Pane();
        dragArea.getStyleClass().add("place-spot");
        // Set area position
        Pair<Double, Double> gcoords = playerBoard.convertCoordinates(pcoords);
        dragArea.setLayoutX(gcoords.first());
        dragArea.setLayoutY(gcoords.second());
        // Set area size
        dragArea.setPrefHeight(CardView.cardHeight);
        dragArea.setPrefWidth(CardView.cardWidth);

        // When you drag over the area accept the drag
        dragArea.setOnDragOver(event -> {
            if (event.getGestureSource() != this && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });
        // When you drop on the area do the needed actions
        dragArea.setOnDragDropped(event -> {
            event.setDropCompleted(true);
            playerBoard.addCard(pcoords, card,side);
            this.removeDragAreas();
            event.consume();
        });

        // When you enter the area change style
        dragArea.setOnDragEntered(event -> {
            dragArea.getStyleClass().add("place-spot-hover");
            event.consume();
        });
        // When you exit the area change style
        dragArea.setOnDragExited(event -> {
            dragArea.getStyleClass().remove("place-spot-hover");
            event.consume();
        });
        playerBoard.getChildren().add(dragArea);
        temporaryDragAreas.add(dragArea);
    }

    /**
     * Remove all temporary drag areas from player board
     */
    private void removeDragAreas() {
        for (Node n : temporaryDragAreas) {
            playerBoard.getChildren().remove(n);
        }
        temporaryDragAreas.clear();
    }

}
