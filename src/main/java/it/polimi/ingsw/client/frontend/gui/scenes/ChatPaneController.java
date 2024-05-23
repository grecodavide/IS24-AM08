package it.polimi.ingsw.client.frontend.gui.scenes;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: to finish implementation
public class ChatPaneController extends SceneController {
    public ScrollPane chatHistoryScrollPane;
    public VBox chatContainer;
    @FXML
    private Button sendMessageBtn;
    @FXML
    private TextFlow chatHistoryText;
    @FXML
    private HBox chatPane;
    @FXML
    private Button showChatBtn;
    @FXML
    private TextField chatInputText;
    @FXML
    private MenuButton chatSelectorBar;

    private boolean isVisible;
    private Map<MenuItem, String> chatHistory;

    private void changeVisibility() {
        TranslateTransition tt = new TranslateTransition(Duration.millis(200), chatPane);
        if (isVisible) {
            tt.setToX(0);
            setIconStatus(true);
        } else {
            tt.setToX(460);
            setIconStatus(false);
        }
        tt.play();
    }

    @Override
    public void initialize() {
        chatHistory = new HashMap<>();
        chatHistoryScrollPane.getStyleClass().clear();
        isVisible = false;
        chatPane.setTranslateX(460);
        setIconStatus(false);

        showChatBtn.setOnMouseClicked((mouseEvent -> {
            isVisible = !isVisible;
            changeVisibility();
        }));

        sendMessageBtn.setOnMouseClicked(mouseEvent -> {
            addMessage("TestUsername", chatInputText.getText());
        });

        addPlayer("ciao");
        addPlayer("ei");

    }

    private void setIconStatus(boolean opened) {
        String path;
        if (opened) {
            path = "/images/icons/right.png";
        } else {
            path = "/images/icons/left.png";
        }
        ImageView img = new ImageView(new Image(path));
        img.setFitHeight(35);
        img.setFitWidth(35);
        showChatBtn.setGraphic(img);
    }

    public void addPlayer(String playerUsername) {
        MenuItem newItem = new MenuItem(playerUsername);
        chatHistory.put(newItem, "");
        newItem.setOnAction(actionEvent -> {
            chatHistoryText.getChildren().clear();
            chatHistoryText.getChildren().add(new Text(chatHistory.get(newItem)));
        });

        chatSelectorBar.getItems().add(newItem);
    }

    public void addMessage(String username, String message) {
        if (!chatInputText.getText().isBlank()) {
            // Add message to the graphical chat
            Text textMessage = new Text(username + ": " + message + "\n");
            chatHistoryText.getChildren().add(textMessage);
        }
    }
}
