package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.client.frontend.gui.nodes.CardView;

import it.polimi.ingsw.client.frontend.gui.nodes.PlateauPane;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;

import java.io.IOException;

public class MatchSceneController extends SceneController {
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

    public void initialize() {
    }

    @Override
    public void initializePostController() throws IOException {
        firstObjective.setOnMouseClicked((e) -> {
            try {
                this.showRankingScene();
            } catch (Exception err) {
                throw new RuntimeException(err);
            }
        });

        // Add the chat
        HBox chatPane = this.loadScene("/fxml/chat.fxml");
        matchPane.getChildren().add(chatPane);
    }

    public PlayerTabController addPlayerTab(String username, Color color) throws IOException {
        // Load the tab
        FXMLLoader loader = GuiUtil.getLoader("/fxml/player_tab.fxml");
        Tab t = loader.load();
        setControllerAttributes(loader);
        // Add the tab
        t.setText(username);
        matchTabs.getTabs().add(t);
        // Add properties to the controller
        PlayerTabController controller = loader.getController();
        controller.setUsername(username);
        t.getProperties().put("Controller", controller);
        // Add colored pawn
        plateauPane.setColor(username, color);
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

    public RankingSceneController showRankingScene() throws IOException {
        StackPane root = loadScene("/fxml/ranking.fxml");
        Scene matchScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        GuiUtil.applyCSS(root, "/css/style.css");
        stage.setScene(matchScene);
        return (RankingSceneController) root.getProperties().get("Controller");
    }
}
