package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Controller for the connection scene
 */
public class ConnectionSceneController extends SceneController {
    public TextField serverAddress;
    public TextField serverPort;
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
            connect();
        });
    }

    /**
     * Connect to the specified NetworkView
     */
    private void connect() {
        // TODO Define things better
        Scene currentScene = stage.getScene();
        NetworkView networkHandler = null;

        if (TCPButton.isSelected()) {
            try {
                networkHandler = new NetworkViewTCP(view, serverAddress.getText(), Integer.valueOf(serverPort.getText()));
                view.setNetworkInterface(networkHandler);
            } catch (Exception e) {
                // TODO Handle connection error
                System.err.println(e);
            }
        } else {
            try {
                networkHandler = new NetworkViewRMI(view, serverAddress.getText(), Integer.parseInt(serverPort.getText()));
                view.setNetworkInterface(networkHandler);
            } catch (Exception e) {
                // TODO Handle connection error
                System.err.println(e);
            }
        }
        try {
            showLobby();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void showLobby() throws IOException {
        StackPane root = this.loadScene("/fxml/lobby.fxml");
        GuiUtil.applyCSS(root, "/css/style.css");
        Scene lobbyScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        LobbySceneController controller = (LobbySceneController) root.getProperties().get("Controller");
        view.setLobbySceneController(controller);
        stage.setScene(lobbyScene);
    }
}
