package it.polimi.ingsw.client.frontend.gui.scenes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: to finish implementation
public class ChatPaneController extends SceneController {
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
        if (isVisible) {
            chatPane.setTranslateX(0);
        } else {
            chatPane.setTranslateX(460);
        }
    }

    @Override
    public void initialize() {
        chatHistory = new HashMap<>();

        isVisible = false;
        changeVisibility();

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
