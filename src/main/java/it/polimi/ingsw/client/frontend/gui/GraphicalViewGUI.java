package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.frontend.gui.scenes.PlayerTabController;
import it.polimi.ingsw.client.frontend.gui.scenes.SceneController;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphicalViewGUI extends GraphicalView {
    private String username;
    private Stage stage;
    private Map<String, PlayerTabController> playerTabControllers;

    public GraphicalViewGUI(Stage stage) {
        this.stage = stage;
    }
    @Override
    public void showError(String cause) {

    }

    @Override
    public void changePlayer() {

    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {

    }

    @Override
    protected void notifyMatchStarted() {

    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {

    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {

    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {

    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) {

    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {

    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {

    }

    @Override
    public void notifyLastTurn() {

    }

    @Override
    public void someoneJoined(String someoneUsername) {

    }

    @Override
    public void someoneQuit(String someoneUsername) {

    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {

    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {

    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {

    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) {
        super.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
        PlayerTabController controller = playerTabControllers.get(someoneUsername);
        controller.placeCard(coords, card, side);
        controller.setPoints(points);
        controller.setResources(availableResources);
    }

    public void setUsername(String username) {
        this.username = username;
        networkView.setUsername(username);
    }

    /**
     * Initialize the map from player to its PlayerTabController
     */
    public void initializeSceneControllers() {
        playerTabControllers = new HashMap<>();
        TabPane tabs = (TabPane) stage.getScene().lookup("#matchTabs");
        if (tabs == null) return;
        for (Tab tab : tabs.getTabs()) {
            String user = (String) tab.getProperties().get("Username");
            if (user != null) {
                PlayerTabController controller = (PlayerTabController) tab.getProperties().get("Controller");
                playerTabControllers.put(user, controller);
            }
        }
    }
}