package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.nodes.BoardPane;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;

public class MatchSceneController extends SceneController{
    @FXML
    TabPane matchTabs;

    public void initialize() throws IOException {
        String username;
        for (int i = 1; i < 3; i++) {
            username ="Player" + i;
            FXMLLoader loader = GuiUtil.getLoader("/fxml/player_tab.fxml");
            ObservableMap<String, Object> namespace = loader.getNamespace();
            Tab t = loader.load();
            setControllerAttributes(loader);
            t.setText(username);
            matchTabs.getTabs().add(t);
            //BoardPane playerBoard = (BoardPane) namespace.get("playerboard");
            //playerBoard.setId(username + "-board");
        }
    }


}
