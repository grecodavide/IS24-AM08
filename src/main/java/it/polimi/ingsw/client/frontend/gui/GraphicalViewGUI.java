package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.client.frontend.gui.scenes.*;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.RequestStatus;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GraphicalViewGUI extends GraphicalView {
    private Stage stage;
    private Map<String, PlayerTabController> playerTabControllers;
    private MatchSceneController matchSceneController;
    private WaitingSceneController waitingSceneController;
    private LobbySceneController lobbySceneController;
    private String matchName;
    private List<AvailableMatch> lastAvailableMatches;
    private Integer maxPlayers;

    public GraphicalViewGUI(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void changePlayer() {
        // TODO: implement
    }

    @Override
    public void makeMove() {
        // TODO: implement
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
        try {
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
        for (String p : super.players) {
            try {
                PlayerTabController controller = matchSceneController.addPlayerTab(p, Color.values()[n]);
                playerTabControllers.put(p, controller);
                controller.setHandCards(super.clientBoards.get(p).getHand());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            n++;
        }
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {
        super.giveInitialCard(initialCard);
        playerTabControllers.get(username).giveInitialCard(initialCard);
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        super.giveSecretObjectives(secretObjectives);
        playerTabControllers.get(username).giveSecretObjectives(secretObjectives);
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        super.someoneDrewInitialCard(someoneUsername, card);
        playerTabControllers.get(someoneUsername).someoneDrewInitialCard(card);
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side, Map<Symbol, Integer> availableResources) {
        super.someoneSetInitialSide(someoneUsername, side, availableResources);
        PlayerTabController playerTabController = playerTabControllers.get(someoneUsername);
        playerTabController.removePlayerChoiceContainer();
        ShownCard card = super.clientBoards.get(someoneUsername).getPlaced().get(0);
        playerTabController.getBoard().addCard(new Pair<>(0, 0), (InitialCard) card.card(), card.side());
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        super.someoneDrewSecretObjective(someoneUsername);
        PlayerTabController playerTabController = playerTabControllers.get(someoneUsername);
        playerTabController.someoneDrewSecretObjective();
    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        super.someoneChoseSecretObjective(someoneUsername);
        PlayerTabController playerTabController = playerTabControllers.get(someoneUsername);
        playerTabController.removePlayerChoiceContainer();
        playerTabController.setSecretObjective(null);
    }

    @Override
    public void notifyLastTurn() {
        for (PlayerTabController t : playerTabControllers.values()) {
            t.setStateTitle("Last turn, play carefully!");
        }
    }

    @Override
    public void someoneJoined(String someoneUsername, List<String> joinedPlayers) {
        super.someoneJoined(someoneUsername, joinedPlayers);
        if (this.maxPlayers == null) {
            int maxPlayers = lastAvailableMatches.stream()
                    .filter((m) -> m.name().equals(matchName))
                    .mapToInt(AvailableMatch::maxPlayers)
                    .toArray()[0];
        }
        if (username.equals(someoneUsername)) {
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
                waitingSceneController.setCurrentPlayers(players.size());
            });
        }
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        // TODO: implement
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        try {
            RankingSceneController rankingSceneController = matchSceneController.showRankingScene();
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
        // TODO: implement
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        // TODO: implement
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) {
        super.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
        PlayerTabController controller = playerTabControllers.get(someoneUsername);
        controller.placeCard(coords, card, side);
        controller.setPoints(points);
        matchSceneController.setPlateauPoints(someoneUsername, points);
        controller.setResources(availableResources);
    }

    public void setUsername(String username) {
        this.username = username;
        networkView.setUsername(username);
    }

    public String getUsername() {
        return username;
    }

    /**
     * Initialize the map from player to its PlayerTabController
     */
    public void initializeSceneControllers() {
        matchSceneController = (MatchSceneController) stage.getScene().lookup("#matchScene").getProperties().get("Controller");
        playerTabControllers = new HashMap<>();
        TabPane tabs = (TabPane) stage.getScene().lookup("#matchTabs");
        if (tabs == null) return;
        for (Tab tab : tabs.getTabs()) {
            String user = (String) tab.getProperties().get("Username");
            if (user != null) {
                PlayerTabController controller = (PlayerTabController) tab.getProperties().get("Controller");
                playerTabControllers.put(user, controller);
            }
        }
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