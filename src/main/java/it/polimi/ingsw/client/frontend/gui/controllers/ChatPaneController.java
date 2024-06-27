package it.polimi.ingsw.client.frontend.gui.controllers;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

import java.util.*;

/**
 * JavaFX controller for the chat pane
 */
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
    private ChoiceBox<String> chatSelector;

    public ScrollPane chatHistoryScrollPane;
    public VBox chatContainer;
    private final Map<String, String> chatHistory = new HashMap<>();
    private boolean isVisible;
    private Queue<String> unconfirmedPrivateMessagesReceivers;

    @Override
    public void initialize() {
        chatHistoryScrollPane.getStyleClass().clear();
        isVisible = false;
        chatPane.setTranslateX(460);
        setIconStatus(false);

        unconfirmedPrivateMessagesReceivers = new LinkedList<>();

        // Add the broadcast item as an entry in the chatSelector and set it as default item
        chatHistory.put("broadcast", "");
        chatSelector.getItems().add("broadcast");
        chatSelector.getSelectionModel().select("broadcast");

        // Show/hide the chat pane when the outer button is clicked
        showChatBtn.setOnMouseClicked((mouseEvent -> {
            isVisible = !isVisible;
            changeVisibility();
        }));

        // Send message written in input box on send message button clicked
        sendMessageBtn.setOnMouseClicked(mouseEvent -> {
            this.submitMessage();
        });
        chatInputText.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER)  {
                this.submitMessage();
            }
        });

        // Switch to chat when a chatSelector item is clicked
        chatSelector.setOnAction(actionEvent -> {
            String selectedChatName = chatSelector.getSelectionModel().getSelectedItem();
            String selectedChatHistory = chatHistory.get(selectedChatName);

            chatHistoryText.getChildren().clear();
            chatHistoryText.getChildren().add(new Text(selectedChatHistory));
        });
    }

    /**
     * Submit a message written in the chat. Calls the network and
     * clears the input field
     */
    private void submitMessage() {
        String inputText = chatInputText.getText();
        String selectedChatName = chatSelector.getSelectionModel().getSelectedItem();

        if (!inputText.isBlank()) {
            if (selectedChatName.equals("broadcast")) {
                super.view.sendBroadcastText(inputText);
            } else {
                super.view.sendPrivateText(selectedChatName, inputText);
                unconfirmedPrivateMessagesReceivers.offer(selectedChatName);
            }
        }

        chatInputText.clear();
    }
    /**
     * Adds a player to the chat.
     *
     * @param playerUsername The player's username
     */
    public void addPlayer(String playerUsername) {
        chatHistory.put(playerUsername, "");
        chatSelector.getItems().add(playerUsername);
    }

    /**
     * Handles the receipt of a broadcast message from a user who's not the current client.
     * For this purpose the method confirmSubmitBroadcastMessage(...) can be used.
     *
     * @param senderUsername The username of the player that sent the message
     * @param message        The message sent
     */
    public void receiveBroadcastMessage(String senderUsername, String message) {
        addMessage("broadcast", senderUsername, message);
    }

    /**
     * Handles the receipt of a private message from a user who's not the current client.
     * For this purpose the method confirmSubmitPrivateMessage(...) can be used.
     *
     * @param senderUsername The username of the player that sent the message
     * @param message        The message sent
     */
    public void receivePrivateMessage(String senderUsername, String message) {
        addMessage(senderUsername, senderUsername, message);
    }

    /**
     * Handles the receipt of confirmation of successful submit of a broadcast message
     * from this client to the server (either broadcast or private).
     *
     * @param message The message to be confirmed
     */
    public void confirmSubmitBroadcastMessage(String message) {
        addMessage("broadcast", super.view.getUsername(), message);
    }

    /**
     * Handles the receipt of confirmation of successful submit of a private message
     * from this client to the server (either broadcast or private).
     *
     * @param message The message to be confirmed
     */
    public void confirmSubmitPrivateMessage(String message) {
        String receiverUsername = unconfirmedPrivateMessagesReceivers.poll();
        addMessage(receiverUsername, super.view.getUsername(), message);
    }

    /**
     * Utility method to add a message to the chat pane
     *
     * @param chatName       The chat in which to put the message
     * @param senderUsername The sender's username
     * @param message        Text message text content
     */
    private void addMessage(String chatName, String senderUsername, String message) {
        String textMessage = senderUsername + ": " + message + "\n";

        // Update the specific chat history associated to the sender
        String updatedHistory = chatHistory.get(chatName) + textMessage;
        chatHistory.put(chatName, updatedHistory);

        // If it's the currently selected chat is this one
        if (chatSelector.getSelectionModel().getSelectedItem().equals(chatName)) {
            // Visually update the chat pane
            chatHistoryText.getChildren().clear();
            chatHistoryText.getChildren().add(new Text(updatedHistory));
        }
    }

    /**
     * Switches the chat pane from visible to not visible and viceversa.
     */
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

    /**
     * Changes the icon of the button to open the chat.
     *
     * @param opened True if the chat is opened, false otherwise
     */
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
}
