package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.MatchState;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;
import javafx.application.Application;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Stack;

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

    private void showMatchStage() throws IOException {
        VBox root = getFromFXML("/fxml/match.fxml");
        applyCSS(root, "/css/match.css");
        Scene matchScene = new Scene(root, 1920, 1080);
        TabPane tabs = (TabPane) matchScene.lookup("#MatchTabs");
        String username;
        for (int i = 1; i < 2; i++) {
            username ="Player" + i;
            FXMLLoader loader = getLoader("/fxml/playertab.fxml");
            ObservableMap<String, Object> namespace = loader.getNamespace();

            Tab t = loader.load();
            t.setText(username);
            tabs.getTabs().add(t);
            CheckBox c = new CheckBox();
            Pane playerBoard = (Pane) namespace.get("playerboard");
            playerBoard.setId(username + "-board");
        }
        primaryStage.setScene(matchScene);
        for (int i = 0; i < 8; i++) {
            addToBoard("Player1", new Pair<>(i, i), "/images/PlayableCards/ANIMAL-golds-back.png");
        }
    }

    private void addToBoard(String username, Pair<Integer, Integer> coords, String png) {
        double card_w = 199;
        double card_h = 132;
        Pane playerBoard = (Pane) primaryStage.getScene().lookup("#" + username + "-board");
        ImageView img = new ImageView(new Image(png));
        img.setFitHeight(card_h);
        img.setFitWidth(card_w);
        img.setLayoutX(playerBoard.getWidth()/2 + coords.first() * card_w);
        img.setLayoutY(playerBoard.getHeight()/2 - coords.second() * card_h);
        System.out.println(playerBoard.getWidth());
        playerBoard.getChildren().add(img);
    }
    private <T>T getFromFXML(String path) throws IOException {
        FXMLLoader loader = this.getLoader(path);
        return loader.load();
    }

    private FXMLLoader getLoader(String path) {
        return new FXMLLoader(this.getClass().getResource(path));
    }

    private void applyCSS(javafx.scene.Parent w, String path) {
        w.getStylesheets().addAll(this.getClass().getResource(path).toExternalForm());
    }
}
