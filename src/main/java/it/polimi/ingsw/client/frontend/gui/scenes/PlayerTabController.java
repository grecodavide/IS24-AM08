package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.nodes.BoardPane;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.util.HashMap;
import java.util.Map;

public class PlayerTabController extends SceneController{
    private String username;
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
    @FXML
    HBox resourcesCounter;
    @FXML
    Label pointsCounter;

    public void initialize() {
        scroll.getStyleClass().clear();
        handCard1.setOnMouseClicked((e) -> {testBoard();});
        handCard2.setOnMouseClicked((e) -> {view.someoneDrewInitialCard(null, null);});
        HashMap<Symbol, Integer> res = new HashMap<>();
        for (Symbol s : Symbol.getBasicResources()) {
            res.put(s, 3);
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

    public void placeCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        playerBoard.addCard(coords, card, side);
    }

    public void setPoints(int points) {
        pointsCounter.setText("Points: " + points);
    }
}
