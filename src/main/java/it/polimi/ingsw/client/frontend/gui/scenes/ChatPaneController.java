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
    private Map<String, MenuItem> playersItems = new HashMap<>();
    private MenuItem broadcastItem;

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
        chatHistoryScrollPane.getStyleClass().clear();
        isVisible = false;
        chatPane.setTranslateX(460);
        setIconStatus(false);

        showChatBtn.setOnMouseClicked((mouseEvent -> {
            isVisible = !isVisible;
            changeVisibility();
        }));

        sendMessageBtn.setOnMouseClicked(mouseEvent -> {
            addBroadcastMessage("TestUsername", chatInputText.getText());
        });

        broadcastItem = new MenuItem("Broadcast");
        broadcastItem.getProperties().put("history", "");
        chatSelectorBar.getItems().add(broadcastItem);
        broadcastItem.setOnAction(actionEvent -> {
            chatSelectorBar.setText("Broadcast");
            chatHistoryText.getChildren().clear();
            chatHistoryText.getChildren().add(new Text((String)broadcastItem.getProperties().get("history")));
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
        playersItems.put(playerUsername, newItem);
        newItem.getProperties().put("history", "");
        newItem.setOnAction(actionEvent -> {
            chatSelectorBar.setText(playerUsername);
            chatHistoryText.getChildren().clear();
            chatHistoryText.getChildren().add(new Text((String)newItem.getProperties().get("history")));
        });
        chatSelectorBar.getItems().add(newItem);
    }

    public void addBroadcastMessage(String username, String message) {
            // Add message to the graphical chat
            String textMessage = username + ": " + message + "\n";

            String history = broadcastItem.getProperties().get("history") + textMessage;
            broadcastItem.getProperties().put("history", history);
            chatHistoryText.getChildren().add(new Text(textMessage));
    }
}
