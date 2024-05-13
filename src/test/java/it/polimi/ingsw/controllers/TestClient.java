package it.polimi.ingsw.controllers;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.network.RemoteViewInterface;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

public class TestClient implements RemoteViewInterface {

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {
        System.out.println("giveLobbyInfo called");
    }

    @Override
    public void matchStarted(Map<Color, String> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {
        System.out.println("matchStarted called");
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) throws RemoteException {
        System.out.println("giveInitialCard called");
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException {
        System.out.println("giveSecretObjectives called");
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) throws RemoteException {
        System.out.println("someoneDrewInitialCard called");
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) throws RemoteException {
        System.out.println("someoneSetInitialSide called");
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) throws RemoteException {
        System.out.println("someoneDrewSecretObjective called");
    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) throws RemoteException {
        System.out.println("someoneChoseSecretObjective called");
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points) throws RemoteException {

    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard, Symbol replacementReign) throws RemoteException {
        System.out.println("someoneDrewCard called");
    }

    @Override
    public void someoneJoined(String someoneUsername) throws RemoteException {
        System.out.println("someoneJoined called");
    }

    @Override
    public void someoneQuit(String someoneUsername) throws RemoteException {
        System.out.println("someoneQuit called");
    }

    @Override
    public void matchFinished(List<Pair<String, Boolean>> ranking) throws RemoteException {
        System.out.println("matchFinished called");
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException {
        System.out.println("someoneSentBroadcastText called");
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException {
        System.out.println("someoneSentPrivateText called");
    }
}
