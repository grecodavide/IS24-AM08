package it.polimi.ingsw.client.frontend.gui;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class GraphicalViewGUI extends GraphicalView {

    GraphicalApplication app;

    public GraphicalViewGUI(GraphicalApplication app) {
        this.app = app;
    }
    @Override
    public void showError(String cause) {

    }

    @Override
    public void changePlayer() {

    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {

    }

    @Override
    protected void notifyMatchStarted() {

    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {

    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {

    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {

    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) {

    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {

    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {

    }

    @Override
    public void notifyLastTurn() {

    }

    @Override
    public void someoneJoined(String someoneUsername) {

    }

    @Override
    public void someoneQuit(String someoneUsername) {

    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {

    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {

    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {

    }
}