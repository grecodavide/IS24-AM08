package it.polimi.ingsw.client.frontend.gui.controllers;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;

/**
 * JavaFX controller for the waiting scene. It shows all the current players
 * waiting for the match to start
 */
public class WaitingSceneController extends SceneController {

    public Label matchName;
    public VBox playersContainer;
    private String name;
    private int maxPlayers = 0;
    private int players = 0;
    private HashMap<String, Label> labels = new HashMap<>();

    @Override
    public void initialize() throws IOException {

    }

    /**
     * Add a player to the list
     * @param username username of the player to add
     */
    public void addPlayer(String username) {
        Label playerLabel = new Label();
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.setText(username);
        playerLabel.getStyleClass().add("form-label");
        playersContainer.getChildren().add(playerLabel);
        labels.put(username, playerLabel);
    }

    /**
     * Set the name of the match to display
     * @param name name of the match
     */
    public void setMatchName(String name) {
        this.name = name;
        updateLabel();
    }

    /**
     * Set the maximum amount of players in the current match
     * @param players maximum number of players
     */
    public void setMaxPlayers(int players) {
        maxPlayers = players;
        updateLabel();

    }

    /**
     * Set the current amount of players in the current match
     * @param players current number of players
     */
    public void setCurrentPlayers(int players) {
        this.players = players;
        updateLabel();
    }

    /**
     * Get the amount of current players
     * @return the amount of current players in the match
     */
    public int getCurrentPlayers() {
        return players;
    }

    /**
     * Update the players count, match name and max players
     */
    public void updateLabel() {
        matchName.setText(name + " " + players + "/" + maxPlayers);
    }

    /**
     * Remove a player from the list
     * @param username username of the player to remove
     */
    public void removePlayer(String username) {
        if (this.labels.containsKey(username)) {
            playersContainer.getChildren().remove(labels.get(username));
        }
    }

    /**
     * Show the match scene when the match is started
     * @return the match scene controller
     * @throws IOException if there was a file error
     */
    public MatchSceneController showMatch() throws IOException {
        VBox root = loadScene("/fxml/match.fxml");
        GuiUtil.applyCSS(root, "/css/match.css");
        Scene matchScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(matchScene);
        return (MatchSceneController) root.getProperties().get("Controller");
    }
}
