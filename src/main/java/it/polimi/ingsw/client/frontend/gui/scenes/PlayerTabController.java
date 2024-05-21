package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.nodes.BoardPane;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class PlayerTabController extends SceneController{

    @FXML
    Pane handCard1;
    @FXML
    Pane handCard2;
    @FXML
    Pane handCard3;
    @FXML
    BoardPane playerBoard;
    @FXML
    ScrollPane scroll;

    public void initialize() {
        scroll.getStyleClass().clear();
        handCard1.setOnMouseClicked((e) -> {testBoard();});
    }

    public void testBoard() {
        for (int i=0; i<5; i++) {
            playerBoard.addCard(new Pair<>(i, i), CardsManager.getInstance().getResourceCards().get(i+1), Side.FRONT);
        }
        for (int i = 1; i < 5; i++) {
            playerBoard.addCard(new Pair<>(i, -i), CardsManager.getInstance().getResourceCards().get(i+1), Side.FRONT);
        }
    }
}
