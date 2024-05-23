package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.gui.scenes.PlayerTabController;
import it.polimi.ingsw.client.frontend.gui.scenes.SceneController;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.Pair;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
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
        this.view = new GraphicalViewGUI(this);
        this.primaryStage = primaryStage;

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
     */
    public void addToBoard(String username, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        TabPane tabs = (TabPane) primaryStage.getScene().lookup("#matchTabs");
        for (Tab tab : tabs.getTabs()) {
            String user = (String) tab.getProperties().get("Username");
            if (user != null && user.equals(username)) {
                PlayerTabController controller = (PlayerTabController) tab.getProperties().get("Controller");
                controller.placeCard(coords, card, side);
            }
        }
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
