package it.polimi.ingsw.client.frontend.gui.controllers;

import it.polimi.ingsw.client.frontend.gui.nodes.BoardPane;
import it.polimi.ingsw.client.frontend.gui.nodes.CardView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseButton;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller of the player tab
 */
public class PlayerTabController extends SceneController {
    public HBox handCards;
    @FXML
    private StackPane rootPane;
    @FXML
    private Tab playerTab;
    private String username;
    @FXML
    BoardPane playerBoard;
    @FXML
    ScrollPane scroll;
    @FXML
    HBox resourcesCounter;
    @FXML
    Label pointsCounter;
    @FXML
    Label stateTitle;
    HBox actionContainer;
    private final List<Node> temporaryDragAreas = new ArrayList<>();

    public void initialize() {
        scroll.getStyleClass().clear();

        HashMap<Symbol, Integer> res = new HashMap<>();
        for (Symbol s : Symbol.getBasicResources()) {
            res.put(s, 0);
        }
        setResources(res);
    }

    /**
     * Set the displayed resources
     *
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
     *
     * @param coords
     * @param card
     * @param side
     */
    public void placeCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        playerBoard.addCard(coords, card, side);
    }

    /**
     * Set the amount of points that the player has
     * @param points amount of points
     */
    public void setPoints(int points) {
        pointsCounter.setText("Points: " + points);
    }

    // Drag and drop management

    /**
     * Add attributes to the hand card
     *
     * @param card CardView to add attributes
     */
    private void initializeHandCard(CardView card) {
        card.setCursor(Cursor.OPEN_HAND);
        card.setOnDragDetected(event -> {
            // Set Dragboard content
            Dragboard dragboard = card.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("");
            dragboard.setContent(content);
            // Set the card as image of the dragboard
            card.setArc(0);
            dragboard.setDragView(card.snapshot(null, null));
            dragboard.setDragViewOffsetX(CardView.cardWidth / 2);
            dragboard.setDragViewOffsetY(CardView.cardHeight / 2);
            this.createDragArea((PlayableCard) card.getProperties().get("Card"), (Side) card.getProperties().get("Side"));
            card.setVisible(false);
            card.setCursor(Cursor.CLOSED_HAND);
            event.consume();
        });
        card.setOnDragDone(event -> {
            this.removeDragAreas();
            card.setArc(20);
            card.setVisible(true);
            card.setCursor(Cursor.DEFAULT);
            event.consume();
        });
        card.setOnMouseClicked((clickEvent) -> {
            if (clickEvent.getButton() == MouseButton.SECONDARY) {
                Side side = (Side) card.getProperties().get("Side");
                Side newSide = side.equals(Side.BACK) ? Side.FRONT : Side.BACK;
                card.setCard((PlayableCard) card.getProperties().get("Card"), newSide);
            } else if (clickEvent.getButton() == MouseButton.MIDDLE) {
                // Compatibility with Hyprland
                if (this.temporaryDragAreas.size() > 0) {
                    this.removeDragAreas();
                } else {
                    this.createDragArea((PlayableCard) card.getProperties().get("Card"), (Side) card.getProperties().get("Side"));
                }
            }
        });
    }

    /**
     * Create all possible drag areas on the board
     *
     * @param card Card to place
     * @param side Side on which place the card
     */
    private void createDragArea(PlayableCard card, Side side) {
        List<Pair<Integer, Integer>> placeableCoords = new ArrayList<>();
        for (Pair<Integer, Integer> coords : playerBoard.takenSpots) {
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
     *
     * @param pcoords Game coordinates on which to put the area
     * @param card    Card where to put the area
     * @param side    Side on which to put the card
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
            this.removeDragAreas();
            // Play the card
            view.playCard(pcoords, card, side);
            event.consume();
        });
        dragArea.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                this.removeDragAreas();
                // Play the card
                view.playCard(pcoords, card, side);
                event.consume();
            }
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

    /**
     * Current player has to choose the secret objective
     * @param objectives the two objectives to choose
     */
    public void giveSecretObjectives(Pair<Objective, Objective> objectives) {
        stateTitle.setText("Choose your secret objective");
        CardView first = new CardView(objectives.first(), Side.FRONT);
        CardView second = new CardView(objectives.second(), Side.FRONT);
        createCardChoiceContainer(first, second);
        first.setCursor(Cursor.HAND);
        second.setCursor(Cursor.HAND);
        first.setOnMouseClicked((mouseEvent) -> view.chooseSecretObjective(objectives.first()));
        second.setOnMouseClicked((mouseEvent -> view.chooseSecretObjective(objectives.second())));
    }

    /**
     * Another player is choosing the secret objective
     */
    public void someoneDrewSecretObjective() {
        stateTitle.setText(username + " is choosing the secret objective");
        CardView first = new CardView(CardsManager.getInstance().getObjectives().get(1), Side.BACK);
        CardView second = new CardView(CardsManager.getInstance().getObjectives().get(1), Side.BACK);
        createCardChoiceContainer(first, second);
    }

    /**
     * The current player is choosing the initial card side
     * @param card given initial card
     */
    public void giveInitialCard(InitialCard card) {
        stateTitle.setText("Choose your card");
        CardView front = new CardView(card, Side.FRONT);
        CardView back = new CardView(card, Side.BACK);
        createCardChoiceContainer(front, back);
        front.setCursor(Cursor.HAND);
        front.setOnMouseClicked((e) -> {
            view.chooseInitialCardSide(Side.FRONT);
        });
        back.setCursor(Cursor.HAND);
        back.setOnMouseClicked((e) -> {
            view.chooseInitialCardSide(Side.BACK);
        });
    }

    /**
     * Show that someone is choosing the initial card side
     * @param card initial card
     */
    public void someoneDrewInitialCard(InitialCard card) {
        stateTitle.setText(username + " is choosing the initial card side...");
        CardView front = new CardView(card, Side.FRONT);
        CardView back = new CardView(card, Side.BACK);
        createCardChoiceContainer(front, back);
    }

    /**
     * Remove the container that asks for initials card, objective cards..
     */
    public void removePlayerChoiceContainer() {
        rootPane.getChildren().remove(actionContainer);
        stateTitle.setText("");
    }

    /**
     * Creates the container to show the choice of initials card or objectives
     *
     * @param front First option to show
     * @param back  Second option to show
     */
    private void createCardChoiceContainer(CardView front, CardView back) {
        actionContainer = new HBox();
        // Add CardViews
        actionContainer.getChildren().add(front);
        actionContainer.getChildren().add(back);
        // Set container properties
        actionContainer.setMaxHeight(Double.NEGATIVE_INFINITY);
        actionContainer.setMaxWidth(Double.NEGATIVE_INFINITY);
        actionContainer.setSpacing(30);
        // Set alignment and add to the rootPane
        actionContainer.setAlignment(Pos.BASELINE_CENTER);
        rootPane.getChildren().add(actionContainer);
        StackPane.setAlignment(actionContainer, Pos.CENTER);
    }

    /**
     * Set the player username
     * @param username username of the player
     */
    public void setUsername(String username) {
        this.username = username;
        playerTab.getProperties().put("Username", username);
    }

    /**
     * Get the Board Pane of the player
     * @return the board pane
     */
    public BoardPane getBoard() {
        return playerBoard;
    }

    /**
     * Set the secret objective for the current player
     * @param objective objective to set
     */
    public void setSecretObjective(Objective objective) {
        CardView secretObjective;
        if (objective != null) {
            secretObjective = new CardView(objective, Side.FRONT);
        } else {
            secretObjective = new CardView(CardsManager.getInstance().getObjectives().get(2), Side.BACK);
        }
        StackPane.setAlignment(secretObjective, Pos.BOTTOM_LEFT);
        StackPane.setMargin(secretObjective, new Insets(0, 100, 100, 0));

        rootPane.getChildren().add(secretObjective);
    }

    /**
     * Set state title, usually used to tell the player what to do
     * @param title text of the title
     */
    public void setStateTitle(String title) {
        this.stateTitle.setText(title);
    }

    /**
     * Set player hand cards
     * @param cards list of cards currently in the player's hand
     */
    public void setHandCards(List<PlayableCard> cards) {
        handCards.getChildren().clear();
        for (PlayableCard card : cards) {
            CardView handCard = new CardView(card, Side.FRONT);
            initializeHandCard(handCard);
            handCards.getChildren().add(handCard);
        }
    }

    /**
     * Set if the player is the current one
     * @param current if the player is current
     */
    public void setCurrentPlayer(boolean current) {
        if (current) {
            playerTab.getStyleClass().add("player-tab");
        } else {
            playerTab.getStyleClass().remove("player-tab");
        }
    }

    /**
     * Enables/disables mouse interactions with hand cards.
     *
     * @param enable True if interactions should be enabled, false otherwise
     */
    public void enablePlaceCardInteractions(boolean enable) {
        handCards.setDisable(!enable);
    }

}
