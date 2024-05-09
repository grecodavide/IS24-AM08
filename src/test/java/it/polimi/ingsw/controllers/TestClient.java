package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.RemoteViewInterface;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class TestClient implements RemoteViewInterface {

    @Override
    public void giveLobbyInfo(List<String> playersNicknames) {
        System.out.println("giveLobbyInfo called");
    }

    @Override
    public void matchStarted(Map<Color, String> playersNicknamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {
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
    public void someoneDrewInitialCard(String someoneNickname, InitialCard card) throws RemoteException {
        System.out.println("someoneDrewInitialCard called");
    }

    @Override
    public void someoneSetInitialSide(String someoneNickname, Side side) throws RemoteException {
        System.out.println("someoneSetInitialSide called");
    }

    @Override
    public void someoneDrewSecretObjective(String someoneNickname) throws RemoteException {
        System.out.println("someoneDrewSecretObjective called");
    }

    @Override
    public void someoneChoseSecretObjective(String someoneNickname) throws RemoteException {
        System.out.println("someoneChoseSecretObjective called");
    }

    @Override
    public void someonePlayedCard(String someoneNickname, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points) throws RemoteException {

    }

    @Override
    public void someoneDrewCard(String someoneNickname, DrawSource source, PlayableCard card, PlayableCard replacementCard, Symbol replacementReign) throws RemoteException {
        System.out.println("someoneDrewCard called");
    }

    @Override
    public void someoneJoined(String someoneNickname) throws RemoteException {
        System.out.println("someoneJoined called");
    }

    @Override
    public void someoneQuit(String someoneNickname) throws RemoteException {
        System.out.println("someoneQuit called");
    }

    @Override
    public void matchFinished(List<Pair<String, Boolean>> ranking) throws RemoteException {
        System.out.println("matchFinished called");
    }

    @Override
    public void someoneSentBroadcastText(String someoneNickname, String text) throws RemoteException {
        System.out.println("someoneSentBroadcastText called");
    }

    @Override
    public void someoneSentPrivateText(String someoneNickname, String text) throws RemoteException {
        System.out.println("someoneSentPrivateText called");
    }
}
