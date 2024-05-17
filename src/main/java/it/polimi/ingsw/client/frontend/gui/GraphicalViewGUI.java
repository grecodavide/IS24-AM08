package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.GraphicalViewInterface;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicalViewGUI extends Application implements GraphicalViewInterface {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/fxml/connection.fxml"));
        primaryStage.setTitle("Codex Naturalis");
        StackPane root = new StackPane();
        root.setId("pane");
        root.getStylesheets().addAll(this.getClass().getResource("/css/style.css").toExternalForm());
        VBox vbox = loader.<VBox>load();
        vbox.setAlignment(Pos.CENTER);
        root.getChildren().add(vbox);
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
    }

    @Override
    public void setNetworkInterface(NetworkView networkView) {

    }

    @Override
    public void sendError(String text) {

    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side) {

    }

    @Override
    public void cancelLastAction() {

    }
}