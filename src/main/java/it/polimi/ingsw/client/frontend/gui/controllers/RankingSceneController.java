package it.polimi.ingsw.client.frontend.gui.controllers;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.client.frontend.gui.GraphicalViewGUI;
import it.polimi.ingsw.utils.GuiUtil;
import it.polimi.ingsw.utils.LeaderboardEntry;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class RankingSceneController extends SceneController {

    public VBox leaderboardContainer;
    public static double tableSize = 700;
    public Label victoryLabel;
    public Button playAgainButton;

    @Override
    public void initialize() throws IOException {

    }

    @Override
    public void initializePostController() {
        playAgainButton.setOnAction(event -> {
            try {
                showConnectionScene();
            } catch (IOException e) {}
        });
    }

    /**
     * Add an entry to the graphical leaderboard
     * @param entry the entry
     */
    public void addRanking(LeaderboardEntry entry) {
        StackPane row = new StackPane();
        row.setMaxHeight(Double.NEGATIVE_INFINITY);
        row.setMinWidth(tableSize);

        // Add player label
        Label playerLabel = new Label(entry.username());
        playerLabel.setMaxWidth(tableSize/3);
        StackPane.setAlignment(playerLabel, Pos.CENTER_LEFT);
        playerLabel.getStyleClass().add("leaderboard-player-label");

        // Add player points
        Label playerPoints = new Label(String.valueOf(entry.points()) + " points");
        StackPane.setAlignment(playerPoints, Pos.CENTER);
        playerPoints.getStyleClass().add("leaderboard-points");

        // Add player result
        Label playerResult = new Label(entry.winner() ? "Winner" : "Loser");
        StackPane.setAlignment(playerResult, Pos.CENTER_RIGHT);
        playerResult.getStyleClass().add("leaderboard-result");
        playerResult.getStyleClass().add(entry.winner() ? "leaderboard-winner" : "leaderboard-loser");

        // Add elements to parents
        row.getChildren().addAll(playerLabel, playerPoints, playerResult);
        leaderboardContainer.getChildren().add(row);
    }

    /**
     * Set if the player has won the match
     * @param victory if the current player has won
     */
    public void setVictory(boolean victory) {
        if (victory) {
            victoryLabel.setText("Victory");
        } else {
            victoryLabel.setText("Defeat");
        }
    }

    public void showConnectionScene() throws IOException {
        view = new GraphicalViewGUI(stage);
        StackPane root = this.loadScene("/fxml/connection.fxml");
        // Add stylesheet
        GuiUtil.applyCSS(root, "/css/style.css");
        // Create the connection scene
        Scene connectionScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(connectionScene);
    }
}
