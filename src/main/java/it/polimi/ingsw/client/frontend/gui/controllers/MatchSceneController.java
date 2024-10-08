package it.polimi.ingsw.client.frontend.gui.controllers;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.client.frontend.gui.nodes.CardView;

import it.polimi.ingsw.client.frontend.gui.nodes.PlateauPane;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaFX controller of the match scene
 */
public class MatchSceneController extends SceneController {
    public Tab tableTab;
    public Label stateTitle;
    @FXML
    TabPane matchTabs;
    @FXML
    AnchorPane matchPane;

    public CardView goldsDeck;
    public CardView resourcesDeck;
    public CardView firstVisible;
    public CardView secondVisible;
    public CardView thirdVisible;
    public CardView fourthVisible;
    public CardView firstObjective;
    public CardView secondObjective;
    public PlateauPane plateauPane;
    public HBox chatPane;
    Map<String, Tab> tabs = new HashMap<>();

    public void initialize() {
    }

    @Override
    public void initializePostController() throws IOException {
        goldsDeck.setOnMouseClicked((clickEvent) -> view.drawCard(DrawSource.GOLDS_DECK));
        goldsDeck.setCursor(Cursor.HAND);
        resourcesDeck.setOnMouseClicked((clickEvent) -> view.drawCard(DrawSource.RESOURCES_DECK));
        resourcesDeck.setCursor(Cursor.HAND);
        firstVisible.setOnMouseClicked((clickEvent) -> view.drawCard(DrawSource.FIRST_VISIBLE));
        firstVisible.setCursor(Cursor.HAND);
        secondVisible.setOnMouseClicked((clickEvent) -> view.drawCard(DrawSource.SECOND_VISIBLE));
        secondVisible.setCursor(Cursor.HAND);
        thirdVisible.setOnMouseClicked((clickEvent) -> view.drawCard(DrawSource.THIRD_VISIBLE));
        thirdVisible.setCursor(Cursor.HAND);
        fourthVisible.setOnMouseClicked((clickEvent) -> view.drawCard(DrawSource.FOURTH_VISIBLE));
        fourthVisible.setCursor(Cursor.HAND);

        // Load the chat pane
        chatPane = loadScene("/fxml/chat.fxml");
        matchPane.getChildren().add(chatPane);
    }

    /**
     * Add the tab of the given player
     * @param username username of the player
     * @param color color of the player
     * @return controller of the created player tab
     * @throws IOException if there are file errors
     */
    public PlayerTabController addPlayerTab(String username, Color color) throws IOException {
        // Load the tab
        FXMLLoader loader = GuiUtil.getLoader("/fxml/player_tab.fxml");
        Tab t = loader.load();
        setControllerAttributes(loader);
        // Add the tab
        t.setText(username);
        Circle icon = new Circle();
        icon.setRadius(15);
        icon.setFill(javafx.scene.paint.Color.web(GuiUtil.getHexFromColor(color)));
        t.setGraphic(icon);
        matchTabs.getTabs().add(t);
        // Add properties to the controller
        PlayerTabController controller = loader.getController();
        controller.setUsername(username);
        t.getProperties().put("Controller", controller);
        // Add colored pawn
        plateauPane.setColor(username, color);
        tabs.put(username, t);
        return controller;
    }

    /**
     * Set the displayed card for the given draw source
     * @param source Source of the draw
     * @param replacementCard Replacement card, can be null
     * @param replacementReign Replacement reign, can be null
     */
    public void setDrawSource (DrawSource source, PlayableCard replacementCard, Symbol replacementReign) {
        switch (source) {
            case DrawSource.GOLDS_DECK -> goldsDeck.setGoldsCardBack(replacementReign);
            case DrawSource.RESOURCES_DECK -> resourcesDeck.setResourcesCardBack(replacementReign);
            case DrawSource.FIRST_VISIBLE -> firstVisible.setCard(replacementCard, Side.FRONT);
            case DrawSource.SECOND_VISIBLE -> secondVisible.setCard(replacementCard, Side.FRONT);
            case DrawSource.THIRD_VISIBLE -> thirdVisible.setCard(replacementCard, Side.FRONT);
            case DrawSource.FOURTH_VISIBLE -> fourthVisible.setCard(replacementCard, Side.FRONT);
        }
    }

    /**
     * Set the visible objectives cards
     * @param objectives pair of the objectives
     */
    public void setObjectives(Pair<Objective, Objective> objectives) {
        firstObjective.setCard(objectives.first(), Side.FRONT);
        secondObjective.setCard(objectives.second(), Side.FRONT);
    }

    /**
     * Move the player pawn on the plateau
     * @param username username of the player to move
     * @param points total amount of points of the player
     */
    public void setPlateauPoints(String username, int points) {
        this.plateauPane.setPoints(username, points);
    }

    /**
     * Show the ranking scene on match end
     * @return ranking scene controller
     * @throws IOException on file errors
     */
    public RankingSceneController showRankingScene() throws IOException {
        StackPane root = loadScene("/fxml/ranking.fxml");
        Scene rankingScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        GuiUtil.applyCSS(root, "/css/style.css");
        stage.setScene(rankingScene);
        return (RankingSceneController) root.getProperties().get("Controller");
    }

    /**
     * Get the chat pane controller
     * @return the chat pane controller
     */
    public ChatPaneController getChatPane() {
        return (ChatPaneController) chatPane.getProperties().get("Controller");
    }

    /**
     * Force the focus on a player's tab
     * @param username username of the player
     */
    public void setFocus(String username) {
        matchTabs.getSelectionModel().select(tabs.get(username));
    }

    /**
     *
     */
    public void setFocusToTable() {
        matchTabs.getSelectionModel().select(tableTab);
    }

    /**
     * Enables/disables mouse interactions with draw sources.
     *
     * @param enable True if interactions should be enabled, false otherwise
     */
    public void enableDrawSourcesInteractions(boolean enable) {
        goldsDeck.setDisable(!enable);
        resourcesDeck.setDisable(!enable);
        firstVisible.setDisable(!enable);
        secondVisible.setDisable(!enable);
        thirdVisible.setDisable(!enable);
        fourthVisible.setDisable(!enable);
    }

    /**
     * Set the current state title to express an action while playing
     * @param text title
     */
    public void setStateTitle(String text) {
        stateTitle.setText(text);
    }
}
