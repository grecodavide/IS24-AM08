package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.gui.controllers.SceneController;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Class from which the FXML application is run, so it's entry point for the user.
 * Apart from making use of FXML instances and methods, it interacts massively with {@link GraphicalViewGUI}.
 */
public class GraphicalApplication extends Application {
    private GraphicalViewGUI view;
    private Stage primaryStage;
    public static double screenWidth = 1920.0;
    public static double screenHeight = 1020.0;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Default method to start the FXML application, it can be called only from this class main(...).
     *
     * @param primaryStage The stage to be opened
     * @throws IOException If there has been an I/O error.
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        this.primaryStage = primaryStage;
        this.view = new GraphicalViewGUI(primaryStage);

        // Load initial screen
        primaryStage.setTitle("Codex Naturalis");
        // Load FXML layout
        StackPane root = this.loadScene("/fxml/connection.fxml");
        // Add stylesheet
        GuiUtil.applyCSS(root, "/css/style.css");
        // Create the connection scene
        Scene connectionScene = new Scene(root, screenWidth, screenHeight);
        // Show the window

        /* Not working yet
        // Fullscreen
        primaryStage.setFullScreen(true);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.setFullScreenExitHint("");
        */

        primaryStage.setScene(connectionScene);
        primaryStage.show();
        root.requestFocus();
        primaryStage.setOnCloseRequest((event) -> {
                Platform.exit();
                System.exit(0);
            });
    }


    /**
     * Get a node from the specified FXML path and set the values of a SceneController
     * @param path file path of FXML
     * @return The first node
     * @param <T> Type of the node
     * @throws IOException in case of error while reading the file
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
