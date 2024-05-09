package it.polimi.ingsw.client.network;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import it.polimi.ingsw.gamemodel.Color;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;

public class NetworkViewTCP extends NetworkView{

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {

    }

    @Override
    public void matchStarted(Map<Color, String> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) {

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
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points) {
        // Example implementation
        graphicalInterface.someonePlayedCard(someoneUsername, coords, card, side);
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard, Symbol replacementReign) throws RemoteException {

    }

    @Override
    public void someoneJoined(String someoneUsername) throws RemoteException {

    }

    @Override
    public void someoneQuit(String someoneUsername) throws RemoteException {

    }

    @Override
    public void matchFinished(List<Pair<String, Boolean>> ranking) throws RemoteException {

    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException {

    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException {

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
