package it.polimi.ingsw.client.frontend.gui.scenes;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;

public class WaitingSceneController extends SceneController {

    public Label matchName;
    public VBox playersContainer;
    private String name;
    private int maxPlayers = 0;
    private int players = 0;
    private HashMap<String, Label> labels = new HashMap<>();

    @Override
    public void initialize() throws IOException {
        // TODO remove test methods
        this.setMaxPlayers(4);
        this.setCurrentPlayers(2);
        this.setMatchName("Morioh");
        this.addPlayer("Oingo");
        this.addPlayer("Boingo");
    }

    public void addPlayer(String username) {
        Label playerLabel = new Label();
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.setText(username);
        playerLabel.getStyleClass().add("form-label");
        playersContainer.getChildren().add(playerLabel);
        labels.put(username, playerLabel);
    }

    public void setMatchName(String name) {
        this.name = name;
        updateLabel();
    }

    public void setMaxPlayers(int players) {
        maxPlayers = players;
        updateLabel();

    }

    public void setCurrentPlayers(int players) {
        this.players = players;
        updateLabel();

    }

    public void updateLabel() {
        matchName.setText(name + " " + players + "/" + maxPlayers);
    }

    public void removePlayer(String username) {
        if (this.labels.containsKey(username)) {
            playersContainer.getChildren().remove(labels.get(username));
        }
    }
}
