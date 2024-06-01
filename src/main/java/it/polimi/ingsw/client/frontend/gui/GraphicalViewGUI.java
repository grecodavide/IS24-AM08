package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.frontend.MatchStatus;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.client.frontend.gui.controllers.*;
import it.polimi.ingsw.controllers.PlayerController;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.*;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        matchSceneController.setFocus(this.username);

        // Enable the hand cards interactions, so that they can be dragged
        playerTabControllers.get(this.username).enablePlaceCardInteractions(true);
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
        matchState = MatchStatus.MATCH_STATE;
        Platform.runLater(() -> {
            try {
                matchSceneController = waitingSceneController.showMatch();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Set visible objectives
            matchSceneController.setObjectives(super.visibleObjectives);
            // Set visible draw sources
            super.visiblePlayableCards.forEach((drawSource, playableCard) -> {
                System.out.println(drawSource + ": " + playableCard.getId());
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
            try {
                chatPaneController = matchSceneController.getChatPane();
                playerTabControllers.forEach((tabUsername, controller) -> {
                    if (!tabUsername.equals(this.username))
                        chatPaneController.addPlayer(tabUsername);
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // By default, disable draw sources interactions
            matchSceneController.enableDrawSourcesInteractions(false);
        });
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {
        super.giveInitialCard(initialCard);
        Platform.runLater(() -> {
            playerTabControllers.get(username).giveInitialCard(initialCard);
            matchSceneController.setFocus(username);
        });
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        super.giveSecretObjectives(secretObjectives);
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
            ShownCard card = super.clientBoards.get(someoneUsername).getPlaced().get(0);
            playerTabController.getBoard().addCard(new Pair<>(0, 0), (InitialCard) card.card(), card.side());
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

    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
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

                // Enable draw sources interactions
                matchSceneController.enableDrawSourcesInteractions(true);
            }
        });
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
                                Symbol replacementCardReign) {
        super.someoneDrewCard(someoneUsername, source, card, replacementCard, replacementCardReign);
        Platform.runLater(() -> {
            PlayerTabController tab = playerTabControllers.get(someoneUsername);
            tab.setHandCards(clientBoards.get(someoneUsername).getHand());
            matchSceneController.setDrawSource(source, replacementCard, replacementCardReign);

            // If the player that drew a card is this client, disable draw source interactions
            if (someoneUsername.equals(this.username))
                matchSceneController.enableDrawSourcesInteractions(false);
        });
    }
    @Override
    public void notifyError(Exception exception) {
        System.out.println(exception.getMessage());

        Stage dialog = new Stage();

        // TODO: implement modal window for errors
//        Parent root = null;
//        try {
//            root = GuiUtil.getFromFXML("error.fxml");
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        dialog.setScene(new Scene());
        dialog.setTitle("Error");

        dialog.initOwner(this.stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
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
}