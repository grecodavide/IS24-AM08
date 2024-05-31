package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;

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
        lobbyScrollPane.getStyleClass().clear();
    }

    @Override
    public void initializePostController() {
        createButton.setOnMouseClicked((e) -> {
            RadioButton radioButton = (RadioButton) matchNumberToggle.getSelectedToggle();
            view.setUsername(createUsername.getText());
            view.createMatch(matchName.getText(), Integer.valueOf(radioButton.getText()));
        });
        joinButton.setOnMouseClicked((e) -> {
            view.setUsername(joinUsername.getText());
            RadioButton radiobutton = (RadioButton) matchJoinToggle.getSelectedToggle();
            view.joinMatch((String) radiobutton.getProperties().get("matchName"));
        });
        matchNumberContainer.getChildren().forEach((button) -> {((RadioButton) button).setToggleGroup(matchNumberToggle);});
    }

    private void showMatch() throws IOException {
        VBox root = loadScene("/fxml/match.fxml");
        GuiUtil.applyCSS(root, "/css/match.css");
        Scene matchScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(matchScene);
    }
    private void showLobby() throws IOException {
        StackPane root = GuiUtil.getFromFXML("/fxml/waiting.fxml");
        GuiUtil.applyCSS(root, "/css/match.css");
        Scene matchScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(matchScene);
    }
    public WaitingSceneController showWaitScene() throws IOException {
        StackPane root = loadScene("/fxml/waiting.fxml");
        GuiUtil.applyCSS(root, "/css/match.css");
        WaitingSceneController controller = (WaitingSceneController) root.getProperties().get("Controller");
        Scene matchScene = new Scene(root, GraphicalApplication.screenWidth, GraphicalApplication.screenHeight);
        stage.setScene(matchScene);
        return controller;
    }

    /**
     * Set the matches displayed
     * @param matchList List of the current available matches
     */
    public void updateMatches(List<AvailableMatch> matchList) {
        matchContainer.getChildren().clear();
        if (matchList.isEmpty()) {
            Label emptyLabel = new Label("There is no match");
            emptyLabel.getStyleClass().add("input-label");
            matchContainer.getChildren().add(emptyLabel);
        }
        for (AvailableMatch m : matchList) {
            addMatchCard(m.name(), m.currentPlayers(), m.maxPlayers());
        }
    }

    /**
     * Create the container for a Match
     * @param name name of the match
     * @param players current amount of players
     * @param maxPlayers maximum number of players allowed in the match
     */
    private void addMatchCard(String name, int players, int maxPlayers) {
        /**
         *                 <HBox styleClass="lobby-card">
         *                     <Label text="Match Name" styleClass="lobby-title" />
         *                     <Pane HBox.hgrow="ALWAYS" />
         *                     <Label text="2/4" styleClass="lobby-title" />
         *                     <Pane prefWidth="5"/>
         *                     <RadioButton styleClass="radio" alignment="CENTER"/>
         java*                 </HBox>
         */
        HBox matchCard = new HBox();
        matchCard.getStyleClass().add("lobby-card");
        matchCard.getStyleClass().add("lobby-card-" + matchContainer.getChildren().size()%2);
        matchCard.setAlignment(Pos.CENTER);

        RadioButton button = new RadioButton();
        button.setAlignment(Pos.CENTER);
        button.getStyleClass().add("radio");
        button.getStyleClass().add("lobby-radio");
        button.setToggleGroup(this.matchJoinToggle);
        button.getProperties().put("matchName", name);
        //button.setText(name);
        matchCard.getChildren().add(button);

        Label nameLabel = new Label(name);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.getStyleClass().add("lobby-title");
        matchCard.getChildren().add(nameLabel);

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        matchCard.getChildren().add(spacer);

        Label playerLabel = new Label(players + "/" + maxPlayers);
        playerLabel.setAlignment(Pos.CENTER);
        playerLabel.getStyleClass().add("lobby-title");
        matchCard.getChildren().add(playerLabel);

        Pane spacer2 = new Pane();
        spacer2.setPrefWidth(5);
        matchCard.getChildren().add(spacer2);

        matchContainer.getChildren().add(matchCard);
    }
}
