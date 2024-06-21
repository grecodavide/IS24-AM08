package it.polimi.ingsw.client.frontend.gui.controllers;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.client.frontend.gui.nodes.CardView;
import it.polimi.ingsw.client.network.NetworkHandler;
import it.polimi.ingsw.client.network.NetworkHandlerRMI;
import it.polimi.ingsw.client.network.NetworkHandlerTCP;
import it.polimi.ingsw.gamemodel.Color;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.LeaderboardEntry;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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
        if (debuggingKeys()) {
            return;
        }
        NetworkHandler networkHandler = null;
        if (TCPButton.isSelected()) {
            try {
                networkHandler = new NetworkHandlerTCP(view, serverAddress.getText(), Integer.valueOf(serverPort.getText()));
                view.setNetworkHandler(networkHandler);
                showLobby();
            } catch (Exception e) {
                view.notifyError(new RemoteException("Cannot connect to the server!"));
            }
        } else {
            try {
                networkHandler = new NetworkHandlerRMI(view, serverAddress.getText(), Integer.parseInt(serverPort.getText()));
                view.setNetworkHandler(networkHandler);
                showLobby();
            } catch (Exception e) {
                view.notifyError(e);
            }
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

    private boolean debuggingKeys() {
        boolean res = false;
        String command = serverAddress.getText();
        if (command.equals("ranking")) {
            try {
                StackPane root = this.loadScene("/fxml/ranking.fxml");
                GuiUtil.applyCSS(root, "/css/style.css");
                RankingSceneController controller = (RankingSceneController) root.getProperties().get("Controller");
                Scene lobbyScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
                stage.setScene(lobbyScene);
                controller.setVictory(true);
                controller.addRanking(new LeaderboardEntry("Oingo", 20, true));
                controller.addRanking(new LeaderboardEntry("Boingo", 12, false));
                controller.addRanking(new LeaderboardEntry("Hol Horse", 10, false));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        } else if (command.equals("waiting")) {
            try {
                StackPane root = this.loadScene("/fxml/waiting.fxml");
                GuiUtil.applyCSS(root, "/css/style.css");
                WaitingSceneController controller = (WaitingSceneController) root.getProperties().get("Controller");
                Scene lobbyScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
                stage.setScene(lobbyScene);
                controller.setCurrentPlayers(3);
                controller.setMaxPlayers(4);
                controller.setMatchName("Marioh");
                controller.addPlayer("Oingo");
                controller.addPlayer("Boingo");
                controller.addPlayer("Hol Horse");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        } else if (command.equals("match")) {
            try {
                VBox root = this.loadScene("/fxml/match.fxml");
                GuiUtil.applyCSS(root, "/css/match.css");
                MatchSceneController controller = (MatchSceneController) root.getProperties().get("Controller");
                Scene lobbyScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
                stage.setScene(lobbyScene);
                controller.addPlayerTab("Oingo", Color.RED);
                PlayerTabController controller1 = controller.addPlayerTab("Boingo", Color.BLUE);
                controller1.setCurrentPlayer(true);
                CardView cardView = new CardView();
                ChatPaneController chatPaneController = controller.getChatPane();
                chatPaneController.addPlayer("Oingo");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return true;
        } else if (command.startsWith("error")) {
            String errorMessage = command.replace("error ", "");
            view.notifyError(new Exception(errorMessage));
        } else if (command.equals("lobby")) {
            try {
                StackPane root = this.loadScene("/fxml/lobby.fxml");
                GuiUtil.applyCSS(root, "/css/style.css");
                LobbySceneController controller = (LobbySceneController) root.getProperties().get("Controller");
                List<AvailableMatch> matches = new ArrayList<>();
                matches.add(new AvailableMatch("New", 3, 2, true));
                matches.add(new AvailableMatch("New2", 3, 3, false));
                matches.add(new AvailableMatch("New3", 0, 3, false));
                controller.updateMatches(matches);
                Scene lobbyScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
                stage.setScene(lobbyScene);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return res;
    }
}
