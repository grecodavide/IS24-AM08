package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.gui.nodes.BoardPane;
import it.polimi.ingsw.client.frontend.gui.scenes.SceneController;
import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;

public class GraphicalApplication extends Application {
    private GraphicalViewGUI view;
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        // Create a GraphicalViewGUI
        view = new GraphicalViewGUI(this);
        // Load initial screen
        primaryStage.setTitle("Codex Naturalis");
        // Load FXML layout
        StackPane root = this.loadScene("/fxml/connection.fxml");
        // Add stylesheet
        GuiUtil.applyCSS(root, "/css/style.css");
        // Create the connection scene
        Scene connectionScene = new Scene(root, 1920, 1080);
        // Show the window
        primaryStage.setScene(connectionScene);
        primaryStage.show();
    }

    /**
     * Add a card to the specified player's board
     *
     * @param username Of the player to add the card to
     * @param card     Card to add
     * @param position Coordinates of the card
     * @param side     Side of the card
     */
    private void addToBoard(String username, PlayableCard card, Pair<Integer, Integer> position, Side side) {
        BoardPane playerBoard = (BoardPane) primaryStage.getScene().lookup("#" + username + "-board");
        playerBoard.addCard(position, card, side);
    }

    /**
     * Get a node from the specified FXML path and set the values of a SceneController
     * @param path file path of FXML
     * @return The first node
     * @param <T> Type of the node
     * @throws IOException
     */
    private <T>T loadScene(String path) throws IOException {
        FXMLLoader loader = GuiUtil.getLoader(path);
        T result = loader.load();
        SceneController controller = loader.getController();
        controller.setGraphicalView(view);
        controller.setStage(primaryStage);
        return result;
    }
}
