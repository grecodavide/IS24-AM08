package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.nodes.CardView;

import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

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
    @FXML
    TabPane matchTabs;
    @FXML
    AnchorPane matchPane;

    public void initialize() {
    }

    @Override
    public void initializePostController() throws IOException {
        String username;
        for (int i = 1; i < 3; i++) {
            username ="Player" + i;
            FXMLLoader loader = GuiUtil.getLoader("/fxml/player_tab.fxml");
            ObservableMap<String, Object> namespace = loader.getNamespace();
            Tab t = loader.load();
            setControllerAttributes(loader);
            t.setText(username);
            matchTabs.getTabs().add(t);
            t.getProperties().put("Controller", loader.getController());
            t.getProperties().put("Username", username);
            //BoardPane playerBoard = (HBox chatPane = this.loadScene("/fxml/chat.fxml");
            //playerBoard.setId(username + "-board");
        }

        // Add the chat
        HBox chatPane = this.loadScene("/fxml/chat.fxml");
        matchPane.getChildren().add(chatPane);
    }

    public void setGoldsDeckReign(Symbol reign) {
        this.goldsDeck.setGoldsCardBack(reign);
    }

    public void setResourcesDeckReign(Symbol reign) {
        this.resourcesDeck.setGoldsCardBack(reign);
    }
}
