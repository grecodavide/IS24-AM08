package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.ViewRMIInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PlayerControllerRMIInterface extends Remote {
    void drawInitialCard() throws RemoteException, WrongStateException, WrongTurnException;

    void chooseInitialCardSide(Side side) throws RemoteException, WrongStateException, WrongTurnException;

    void drawSecretObjectives() throws RemoteException, WrongStateException, WrongTurnException;

    void chooseSecretObjective(Objective objective) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    void drawCard(DrawSource source) throws RemoteException, HandException, WrongStateException, WrongTurnException, WrongChoiceException;

    void registerView(ViewRMIInterface view) throws RemoteException;
}
