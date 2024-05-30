package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.client.frontend.gui.nodes.CardView;

import it.polimi.ingsw.client.frontend.gui.nodes.PlateauPane;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.*;

import java.io.IOException;

public class MatchSceneController extends SceneController{
    public CardView goldsDeck;
    public CardView resourcesDeck;
    public CardView firstVisible;
    public CardView secondVisible;
    public CardView thirdVisible;
    public CardView fourthVisible;
    public CardView firstObjective;
    public CardView secondObjective;
    public PlateauPane plateauPane;
    @FXML
    TabPane matchTabs;
    @FXML
    AnchorPane matchPane;

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
        String username;
        for (int i = 1; i < 3; i++) {
            username ="Player" + i;
            FXMLLoader loader = GuiUtil.getLoader("/fxml/player_tab.fxml");
            ObservableMap<String, Object> namespace = loader.getNamespace();
            Tab t = loader.load();
            setControllerAttributes(loader);
            t.setText(username);
            matchTabs.getTabs().add(t);
            PlayerTabController controller = loader.getController();
            controller.setUsername(username);
            t.getProperties().put("Controller", loader.getController());
            plateauPane.setColor(username, Color.values()[i]);
            //BoardPane playerBoard = (HBox chatPane = this.loadScene("/fxml/chat.fxml");
            //playerBoard.setId(username + "-board");
        }

        // Add the chat
        HBox chatPane = this.loadScene("/fxml/chat.fxml");
        matchPane.getChildren().add(chatPane);
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

    public void setObjectives(Pair<Objective, Objective> objectives) {
        firstObjective.setCard(objectives.first(), Side.FRONT);
        secondObjective.setCard(objectives.second(), Side.FRONT);
    }

    private void showRankingScene() throws IOException {
        StackPane root = GuiUtil.getFromFXML("/fxml/ranking.fxml");
        GuiUtil.applyCSS(root, "/css/style.css");
        Scene matchScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(matchScene);
    }
}
