package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controller for the connection scene
 */
public class ConnectionSceneController extends SceneController {
    @FXML
    private RadioButton RMIButton;
    @FXML
    private RadioButton TCPButton;
    @FXML
    private Button connectButton;
    private ToggleGroup connectionGroup;

    public void initialize() {
        // Set the toggle group
        connectionGroup = new ToggleGroup();
        RMIButton.setToggleGroup(connectionGroup);
        TCPButton.setToggleGroup(connectionGroup);
        TCPButton.setSelected(true);
        // Add callback for the button
        connectButton.setOnAction(event -> {
            try {
                this.showLobby();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Connect to the specified NetworkView
     */
    private void connect() {
        // TODO Define things better
        Scene currentScene = stage.getScene();
        RadioButton TCPButton = (RadioButton) currentScene.lookup("#TCP");
        if (TCPButton.isSelected()) {
            // TODO: fix number of arguments
            // view.setNetworkInterface(new NetworkViewTCP(view));
        } else {
            // TODO: fix number of arguments
            // view.setNetworkInterface(new NetworkViewRMI(view, null));
        }
        stage.setScene(new Scene(new VBox(), 1000, 80));
    }

    private void showMatch() throws IOException {
        VBox root = loadScene("/fxml/match.fxml");
        GuiUtil.applyCSS(root, "/css/match.css");
        Scene matchScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(matchScene);
    }

    private void showLobby() throws IOException {
        StackPane root = this.loadScene("/fxml/lobby.fxml");
        GuiUtil.applyCSS(root, "/css/style.css");
        Scene lobbyScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(lobbyScene);
    }
}
