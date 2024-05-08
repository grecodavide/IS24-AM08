package it.polimi.ingsw.client;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class NetworkViewTCP extends NetworkView{

    @Override
    public void giveLobbyInfo(List<String> playersNicknames) {

    }

    @Override
    public void matchStarted(Map<Color, String> playersNicknamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) {

    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {

    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {

    }

    @Override
    public void someoneDrewInitialCard(String someoneNickname, InitialCard card) {

    }

    @Override
    public void someoneSetInitialSide(String someoneNickname, Side side) {

    }

    @Override
    public void someoneDrewSecretObjective(String someoneNickname) {

    }

    @Override
    public void someoneChoseSecretObjective(String someoneNickname) {

    }

    @Override
    public void someonePlayedCard(String someoneNickname, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        // Example implementation
        graphicalInterface.someonePlayedCard(someoneNickname, coords, card, side);
    }

    @Override
    public void someoneDrewCard(String someoneNickname, DrawSource source, Card card) throws RemoteException {

    }

    @Override
    public void someoneJoined(String someoneNickname) throws RemoteException {

    }

    @Override
    public void someoneQuit(String someoneNickname) throws RemoteException {

    }

    @Override
    public void matchFinished() throws RemoteException {

    }

    @Override
    public void someoneSentBroadcastText(String someoneNickname, String text) throws RemoteException {

    }

    @Override
    public void someoneSentPrivateText(String someoneNickname, String text) throws RemoteException {

    }

    @Override
    public void drawInitialCard() {

    }

    @Override
    public void chooseInitialCardSide(Side side) {

    }

    @Override
    public void drawSecretObjectives() {

    }

    @Override
    public void chooseSecretObjective(Objective objective) {

    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        // Send a PlayCardMessage
        // this.sendMessage(new PlayCardMessage(...));
    }

    @Override
    public void drawCard(DrawSource source) {

    }
}
