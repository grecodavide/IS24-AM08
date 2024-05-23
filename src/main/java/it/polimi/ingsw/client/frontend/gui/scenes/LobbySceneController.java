package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class LobbySceneController extends SceneController {
    @FXML
    public TextField createUsername;
    @FXML
    public TextField matchName;
    @FXML
    public HBox matchNumberContainer;
    @FXML
    VBox matchContainer;
    @FXML
    ScrollPane lobbyScrollPane;
    @FXML
    Button joinButton;
    @FXML
    TextField joinUsername;
    @FXML
    Button createButton;
    ToggleGroup matchJoinToggle;
    ToggleGroup matchNumberToggle;

    public void initialize() {
        matchJoinToggle = new ToggleGroup();
        matchNumberToggle = new ToggleGroup();
    }

    @Override
    public void initializePostController() {
        createButton.setOnMouseClicked((e) -> {
            RadioButton radioButton = (RadioButton) matchNumberToggle.getSelectedToggle();
            view.setUsername(createUsername.getText());
            view.createMatch(matchName.getText(), Integer.valueOf(radioButton.getText()));
        });
        joinButton.setOnMouseClicked((e) -> {
            /*
            view.setUsername(joinUsername.getText());
            RadioButton radiobutton = (RadioButton) matchJoinToggle.getSelectedToggle();
            view.joinMatch(radiobutton.getText());
             */
            // Show match for debugging
            try {
                showMatch();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        matchNumberContainer.getChildren().forEach((button) -> {((RadioButton) button).setToggleGroup(matchNumberToggle);});

    }

    private void showMatch() throws IOException {
        VBox root = loadScene("/fxml/match.fxml");
        GuiUtil.applyCSS(root, "/css/match.css");
        Scene matchScene = new Scene(root, 1800, 1080);
        stage.setScene(matchScene);
    }
    public void updateMatches(List<AvailableMatch> matchList) {
        matchContainer.getChildren().clear();
        for (AvailableMatch m : matchList) {
            addMatchCard(m.name(), m.currentPlayers(), m.maxPlayers());
        }
    }

    private void addMatchCard(String name, int players, int maxPlayers) {
        /**
         *                 <HBox styleClass="lobby-card">
         *                     <Label text="Match Name" styleClass="lobby-title" />
         *                     <Pane HBox.hgrow="ALWAYS" />
         *                     <Label text="2/4" styleClass="lobby-title" />
         *                     <Pane prefWidth="5"/>
         *                     <RadioButton styleClass="radio" alignment="CENTER"/>
         *                 </HBox>
         */
        HBox matchCard = new HBox();
        matchCard.getStyleClass().add("lobby-card");

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("lobby-title");
        matchCard.getChildren().add(nameLabel);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        matchCard.getChildren().add(spacer);

        Label playerLabel = new Label(players + "/" + maxPlayers);
        playerLabel.getStyleClass().add("lobby-title");
        matchCard.getChildren().add(playerLabel);

        Pane spacer2 = new Pane();
        spacer2.setPrefWidth(5);
        matchCard.getChildren().add(spacer2);

        RadioButton button = new RadioButton();
        button.setAlignment(Pos.CENTER);
        button.getStyleClass().add("radio");
        button.setToggleGroup(this.matchJoinToggle);
        matchCard.getChildren().add(button);

        matchContainer.getChildren().add(matchCard);
    }
}
