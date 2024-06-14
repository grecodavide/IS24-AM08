package it.polimi.ingsw.client.frontend.gui;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.frontend.MatchStatus;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.client.frontend.gui.controllers.*;
import it.polimi.ingsw.client.frontend.gui.nodes.CardView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GraphicalViewGUI extends GraphicalView {
    private final Stage stage;
    // Controllers
    private Map<String, PlayerTabController> playerTabControllers;
    private MatchSceneController matchSceneController;
    private WaitingSceneController waitingSceneController;
    private LobbySceneController lobbySceneController;
    private RankingSceneController rankingSceneController;
    private ChatPaneController chatPaneController;

    // Match state management
    MatchStatus matchState = MatchStatus.LOBBY;

    // Temporary vars
    private String matchName;
    private List<AvailableMatch> lastAvailableMatches;
    private Integer maxPlayers;

    public GraphicalViewGUI(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void changePlayer() {
        Platform.runLater(() -> {
            // Notify to each player tab if it is his turn and disable his hand card interactions
            // this is needed in order to disable this client's interactions after his turn has finished
            for (String username : playerTabControllers.keySet()) {
                PlayerTabController tab = playerTabControllers.get(username);
                boolean isCurrent = username.equals(currentPlayer);

                tab.setCurrentPlayer(isCurrent);
                tab.enablePlaceCardInteractions(false);
            }
        });
    }

    /**
     * Method called everytime it's this client turn.
     */
    @Override
    public void makeMove() {
        this.changePlayer();
        Platform.runLater(() -> {
            matchSceneController.setFocus(this.username);

            // Enable the hand cards interactions, so that they can be dragged
            playerTabControllers.get(this.username).enablePlaceCardInteractions(true);
            playerTabControllers.get(this.username).setStateTitle("Play a card");
        });
    }

    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        super.createMatch(matchName, maxPlayers);
        this.matchName = matchName;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void joinMatch(String matchName) {
        super.joinMatch(matchName);
        this.matchName = matchName;
    }

    @Override
    protected void notifyMatchStarted() {
        this.setupMatch(false, false);
    }

    @Override
    protected void notifyMatchResumed(boolean drawPhase) {
        this.setupMatch(true, drawPhase);
    }

    /**
     * Set match scene and populate elements on match start
     * @param matchResumed if the match is resumed
     */
    private void setupMatch(boolean matchResumed, boolean drawPhase) {
        matchState = MatchStatus.MATCH_STATE;
        Platform.runLater(() -> {
            try {
                if (waitingSceneController == null) {
                    waitingSceneController = new WaitingSceneController();
                    waitingSceneController.setGraphicalView(this);
                    waitingSceneController.setStage(stage);
                }
                matchSceneController = waitingSceneController.showMatch();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Set visible objectives
            matchSceneController.setObjectives(super.visibleObjectives);
            // Set visible draw sources
            super.visiblePlayableCards.forEach((drawSource, playableCard) -> {
                matchSceneController.setDrawSource(drawSource, playableCard, playableCard.getReign());
            });
            matchSceneController.setDrawSource(DrawSource.GOLDS_DECK, null, super.decksTopReign.first());
            matchSceneController.setDrawSource(DrawSource.RESOURCES_DECK, null, super.decksTopReign.second());

            // Create players tabs, assign colors and their hands
            int n = 0;
            playerTabControllers = new HashMap<>();
            for (String p : super.players) {
                try {
                    PlayerTabController controller = matchSceneController.addPlayerTab(p, Color.values()[n]);
                    playerTabControllers.put(p, controller);
                    controller.setHandCards(super.clientBoards.get(p).getHand());
                    // Disable the interaction with hand cards on all player tabs
                    controller.enablePlaceCardInteractions(false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                n++;
            }

            // Initialize the chat pane
            chatPaneController = matchSceneController.getChatPane();
            playerTabControllers.forEach((tabUsername, controller) -> {
                if (!tabUsername.equals(this.username))
                    chatPaneController.addPlayer(tabUsername);
            });

            // By default, disable draw sources interactions
            matchSceneController.enableDrawSourcesInteractions(false);
            if (matchResumed) this.setupResumedMatch(drawPhase);
        });
    }

    /**
     * Populate extra elements after match resumed
     */
    private void setupResumedMatch(boolean drawPhase) {
        playerTabControllers.forEach(((username, playerTabController) -> {
            ClientBoard playerBoard = clientBoards.get(username);
            playerTabController.setSecretObjective(playerBoard.getObjective());

            // Place the initial card
            playerTabController.getBoard().addCard(new Pair<>(0, 0), (InitialCard) playerBoard.getPlaced().get(0).card(), playerBoard.getPlaced().get(0).side());

            // Place all the other cards
            Map<Integer, ShownCard> placed = playerBoard.getPlaced();
            for (Integer turn : placed.keySet()) {
                if (turn > 0) {
                    playerTabController.getBoard().addCard(placed.get(turn).coords(), (PlayableCard) placed.get(turn).card(), placed.get(turn).side());
                }
            }

            // Set points and available resources
            playerTabController.setPoints(playerBoard.getPoints());
            matchSceneController.plateauPane.setPoints(username, playerBoard.getPoints());
            playerTabController.setResources(playerBoard.getAvailableResources());
        }));

        // Enable interactions if it is the current user turn
        this.changePlayer();
        if (currentPlayer.equals(username)) {
            if (!drawPhase) {
                this.makeMove();
                playerTabControllers.get(username).enablePlaceCardInteractions(true);
            } else {
                // Draw interactions
                // Set focus on the table
                matchSceneController.setFocusToTable();
                matchSceneController.setStateTitle("Draw a card");
                // Enable draw sources interactions
                matchSceneController.enableDrawSourcesInteractions(true);
            }
        }
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {
        super.giveInitialCard(initialCard);
        this.changePlayer();
        Platform.runLater(() -> {
            playerTabControllers.get(username).giveInitialCard(initialCard);
            matchSceneController.setFocus(username);
        });
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        super.giveSecretObjectives(secretObjectives);
        this.changePlayer();
        Platform.runLater(() -> {
            playerTabControllers.get(username).giveSecretObjectives(secretObjectives);
            matchSceneController.setFocus(username);
        });
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        super.someoneDrewInitialCard(someoneUsername, card);
        Platform.runLater(() -> playerTabControllers.get(someoneUsername).someoneDrewInitialCard(card));
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side, Map<Symbol, Integer> availableResources) {
        super.someoneSetInitialSide(someoneUsername, side, availableResources);
        Platform.runLater(() -> {
            PlayerTabController playerTabController = playerTabControllers.get(someoneUsername);
            playerTabController.removePlayerChoiceContainer();
            InitialCard card = super.clientBoards.get(someoneUsername).getInitialCard();
            CardView initial = playerTabController.getBoard().addCard(new Pair<>(0, 0), card, side);
            initial.setToken(Color.values()[players.indexOf(someoneUsername)]);
        });
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        super.someoneDrewSecretObjective(someoneUsername);
        PlayerTabController playerTabController = playerTabControllers.get(someoneUsername);
        Platform.runLater(playerTabController::someoneDrewSecretObjective);
    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        super.someoneChoseSecretObjective(someoneUsername);
        Platform.runLater(() -> {
            PlayerTabController playerTabController = playerTabControllers.get(someoneUsername);
            playerTabController.removePlayerChoiceContainer();
            if (someoneUsername.equals(username)) {
                playerTabController.setSecretObjective(clientBoards.get(username).getObjective());
            } else {
                playerTabController.setSecretObjective(null);
            }
        });
    }

    @Override
    public void notifyLastTurn() {
        for (PlayerTabController t : playerTabControllers.values()) {
            Platform.runLater(() -> t.setStateTitle("Last turn, play carefully!"));
        }
    }

    @Override
    public void someoneJoined(String someoneUsername, List<String> joinedPlayers) {
        if (!matchState.equals(MatchStatus.WAIT_STATE) && !matchState.equals(MatchStatus.LOBBY)) {
            return;
        }
        super.someoneJoined(someoneUsername, joinedPlayers);
        if (this.maxPlayers == null) {
            maxPlayers = lastAvailableMatches.stream()
                    .filter((m) -> m.name().equals(matchName))
                    .mapToInt(AvailableMatch::maxPlayers)
                    .toArray()[0];
        }
        if (username.equals(someoneUsername)) {
            matchState = MatchStatus.WAIT_STATE;
            Platform.runLater(() -> {
                try {
                    waitingSceneController = lobbySceneController.showWaitScene();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                waitingSceneController.setCurrentPlayers(joinedPlayers.size());
                waitingSceneController.setMatchName(matchName);
                waitingSceneController.setMaxPlayers(maxPlayers);
                for (String player : joinedPlayers) {
                    waitingSceneController.addPlayer(player);
                }
            });
        } else {
            Platform.runLater(() -> {
                waitingSceneController.addPlayer(someoneUsername);
                waitingSceneController.setCurrentPlayers(joinedPlayers.size());
            });
        }
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        if (matchState.equals(MatchStatus.WAIT_STATE)) {
            Platform.runLater(() -> {
                waitingSceneController.removePlayer(someoneUsername);
                waitingSceneController.setCurrentPlayers(waitingSceneController.getCurrentPlayers()-1);
            });
        } else {
            notifyError("Player Quit", "Match finished because " + someoneUsername + " quit");
        }
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        Platform.runLater(() -> {
            try {
                rankingSceneController = matchSceneController.showRankingScene();
                ranking.forEach((entry) -> {
                    if (entry.username().equals(this.username)) {
                        rankingSceneController.setVictory(entry.winner());
                    }
                    rankingSceneController.addRanking(entry);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        Platform.runLater(() -> {
            if (someoneUsername.equals(this.username))
                chatPaneController.confirmSubmitBroadcastMessage(text);
            else
                chatPaneController.receiveBroadcastMessage(someoneUsername, text);
        });
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        Platform.runLater(() -> {
            if (someoneUsername.equals(this.username))
                chatPaneController.confirmSubmitPrivateMessage(text);
            else
                chatPaneController.receivePrivateMessage(someoneUsername, text);
        });
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) {
        super.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
        Platform.runLater(() -> {
            PlayerTabController controller = playerTabControllers.get(someoneUsername);
            controller.placeCard(coords, card, side);
            controller.setPoints(points);
            matchSceneController.setPlateauPoints(someoneUsername, points);
            controller.setHandCards(clientBoards.get(someoneUsername).getHand());
            controller.setResources(availableResources);

            // If the player that played a card is this client
            if (someoneUsername.equals(this.username)) {
                // Set the focus on the plateau tab
                matchSceneController.setFocusToTable();
                matchSceneController.setStateTitle("Draw a card");
                // Enable draw sources interactions
                matchSceneController.enableDrawSourcesInteractions(true);
            } else {
                matchSceneController.setStateTitle(someoneUsername + " is drawing a card...");
            }
            playerTabControllers.get(someoneUsername).setStateTitle("");
        });
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
                                Pair<Symbol, Symbol> deckTopReigns) {
        super.someoneDrewCard(someoneUsername, source, card, replacementCard, deckTopReigns);
        Platform.runLater(() -> {
            PlayerTabController tab = playerTabControllers.get(someoneUsername);
            tab.setHandCards(clientBoards.get(someoneUsername).getHand());
            if (!source.equals(DrawSource.GOLDS_DECK) && !source.equals(DrawSource.RESOURCES_DECK)) {
                matchSceneController.setDrawSource(source, replacementCard, replacementCard.getReign());
            }
            matchSceneController.setDrawSource(DrawSource.GOLDS_DECK, null, deckTopReigns.first());
            matchSceneController.setDrawSource(DrawSource.RESOURCES_DECK, null, deckTopReigns.second());

            // If the player that drew a card is this client, disable draw source interactions
            if (someoneUsername.equals(this.username)) {
                matchSceneController.enableDrawSourcesInteractions(false);
                matchSceneController.setFocus(this.username);
            }
            // Remove draw title
            matchSceneController.setStateTitle("");
        });
    }

    @Override
    public void notifyError(Exception exception) {
        this.notifyError(GuiUtil.getExceptionTitle(exception), exception.getMessage());
    }

    public void notifyError(String title, String description) {
        Platform.runLater(() -> {
            try {
                // Load the error node from the fxml file

                FXMLLoader loader = GuiUtil.getLoader("/fxml/error.fxml");
                StackPane root = loader.load();
                ErrorSceneController controller = loader.getController();

                Stage dialog = new Stage();
                Scene errorScene = new Scene(root, ErrorSceneController.windowWidth, ErrorSceneController.windowHeight);

                // Initialize attributes
                GuiUtil.applyCSS(root, "/css/style.css");
                controller.setTitle(title);
                controller.setText(description);

                dialog.setScene(errorScene);
                dialog.setTitle("Error");
                dialog.initOwner(this.stage);
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setResizable(false);
                dialog.sizeToScene();

                // Show the modal window (stage)
                dialog.show();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setUsername(String username) {
        this.username = username;
        networkView.setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    public void getAvailableMatches() {
        this.setLastRequestStatus(RequestStatus.PENDING);
        this.networkView.getAvailableMatches();
    }

    public void receiveAvailableMatches(List<AvailableMatch> availableMatches) {
        super.receiveAvailableMatches(availableMatches);
        lastAvailableMatches = availableMatches;
        Platform.runLater(() -> lobbySceneController.updateMatches(availableMatches));
    }

    public void setLobbySceneController(LobbySceneController lobbySceneController) {
        this.lobbySceneController = lobbySceneController;
        this.getAvailableMatches();
    }

    public static void main(String[] args) {
        Application.launch(GraphicalApplication.class, args);
    }

    @Override
    public void notifyConnectionLost() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyConnectionLost'");
    }
}
