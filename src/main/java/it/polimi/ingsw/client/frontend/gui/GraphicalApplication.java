package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.gui.nodes.BoardPane;
import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.CardsManager;
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
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/connection.fxml"));
        StackPane root = loader.load();
        // Add stylesheet
        root.getStylesheets().addAll(this.getClass().getResource("/css/style.css").toExternalForm());
        // Create the connection scene
        Scene connectionScene = new Scene(root, 1920, 1080);
        // Create toggle group for connection type selection
        ToggleGroup connecitonGroup = new ToggleGroup();

        RadioButton RMIButton = (RadioButton) connectionScene.lookup("#RMI");
        RadioButton TCPButton = (RadioButton) connectionScene.lookup("#TCP");

        RMIButton.setToggleGroup(connecitonGroup);
        TCPButton.setToggleGroup(connecitonGroup);
        TCPButton.setSelected(true);

        // Add listener to connect button
        Button connectButton = (Button) connectionScene.lookup("#connect-button");
        connectButton.setOnAction(event -> {
            try {
                this.showMatchStage();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        // Show the window
        primaryStage.setScene(connectionScene);
        primaryStage.show();
    }

    /**
     * Connect to the specified NetworkView
     */
    private void connect() {
        // TODO Define things better
        Scene currentScene = primaryStage.getScene();
        RadioButton TCPButton = (RadioButton) currentScene.lookup("#TCP");
        if (TCPButton.isSelected()) {
            view.setNetworkInterface(new NetworkViewTCP(view));
        } else {
            view.setNetworkInterface(new NetworkViewRMI(view, null));
        }
        primaryStage.setScene(new Scene(new VBox(), 1000, 80));
    }

    /**
     * Build match stage, the stage where the game is shown
     * @throws IOException
     */
    private void showMatchStage() throws IOException {
        // Get the match scene
        VBox root = getFromFXML("/fxml/match.fxml");
        applyCSS(root, "/css/match.css");
        Scene matchScene = new Scene(root, 1920, 1080);
        TabPane tabs = (TabPane) matchScene.lookup("#MatchTabs");
        // Populate match tabs
        String username;
        for (int i = 1; i < 3; i++) {
            username ="Player" + i;
            FXMLLoader loader = getLoader("/fxml/playertab.fxml");
            ObservableMap<String, Object> namespace = loader.getNamespace();

            Tab t = loader.load();
            t.setText(username);
            tabs.getTabs().add(t);
            BoardPane playerBoard = (BoardPane) namespace.get("playerboard");
            playerBoard.setId(username + "-board");
        }
        primaryStage.setScene(matchScene);
        for (int i = 0; i < 8; i++) {
            addToBoard("Player1", CardsManager.getInstance().getGoldCards().get(2), new Pair<>(i, i), Side.FRONT );
        }
        for (int i = 0; i < 3; i++) {
            addToBoard("Player1", CardsManager.getInstance().getResourceCards().get(3), new Pair<>(-i, -i), Side.BACK);
        }
    }

    /**
     * Add a card to the specified player's board
     * @param username Of the player to add the card to
     * @param card Card to add
     * @param position Coordinates of the card
     * @param side Side of the card
     */
    private void addToBoard(String username, Card card, Pair<Integer, Integer> position, Side side) {
        BoardPane playerBoard = (BoardPane) primaryStage.getScene().lookup("#" + username + "-board");
        playerBoard.addCard(position, card, side);
    }

    /**
     * Get a node from the specified FXML path
     * @param path file path of FXML
     * @return The first node
     * @param <T> Type of the node
     * @throws IOException
     */
    private <T>T getFromFXML(String path) throws IOException {
        FXMLLoader loader = this.getLoader(path);
        return loader.load();
    }

    /**
     * Get the loader from the specified path
     * @param path file path of fxml
     * @return loader
     */
    private FXMLLoader getLoader(String path) {
        return new FXMLLoader(this.getClass().getResource(path));
    }

    /**
     * Applies the specified CSS to a javafx scene parent
     * @param w The parent to apply the css to
     * @param path Path of the css file
     */
    private void applyCSS(javafx.scene.Parent w, String path) {
        w.getStylesheets().addAll(this.getClass().getResource(path).toExternalForm());
    }
}
