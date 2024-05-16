package it.polimi.ingsw.client.network;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.controllers.PlayerControllerRMIInterface;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.exceptions.WrongTurnException;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

public class NetworkViewRMI extends NetworkView {

    PlayerControllerRMIInterface controller;

    public NetworkViewRMI(PlayerControllerRMIInterface controller) {
        this.controller = controller;
    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) throws RemoteException {

    }

    @Override
    public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException {

    }

    @Override
    public void giveInitialCard(InitialCard initialCard) throws RemoteException {

    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException {

    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) throws RemoteException {

    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) throws RemoteException {

    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) throws RemoteException {

    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) throws RemoteException {

    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points) throws RemoteException {
        // Demo implementation
        graphicalInterface.someonePlayedCard(someoneUsername, coords, card, side, points, null);
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
        // Demo implementation for playCard
        try {
            controller.playCard(coords, card, side);
        } catch (WrongTurnException | WrongChoiceException | WrongStateException e) {
            graphicalInterface.sendError(e.getMessage());
            graphicalInterface.cancelLastAction();
        } catch (RemoteException e) {
            // Notifiy disconnection
        }
    }

    @Override
    public void drawCard(DrawSource source) {

    }
}
