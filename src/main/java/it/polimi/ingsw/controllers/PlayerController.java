package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.RemoteException;
import java.util.List;

public abstract sealed class PlayerController implements MatchObserver permits PlayerControllerRMI {
    protected Match match;
    protected Player player;
    protected ViewInterface view;

    public PlayerController(String nickname, Match match) throws AlreadyUsedNicknameException, WrongStateException {
        this.match = match;

        player = new Player(nickname, match);

        match.addPlayer(player);
        match.subscribeObserver(this);
    }

    public abstract void drawInitialCard() throws WrongStateException, WrongTurnException, RemoteException, PlayerQuitException;

    public abstract void chooseInitialCardSide(Side side) throws WrongStateException, WrongTurnException, RemoteException;

    public abstract void drawSecretObjectives() throws WrongStateException, WrongTurnException, RemoteException, PlayerQuitException;
    
    public abstract void chooseSecretObjective(Objective objective) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    public abstract void drawCard(DrawSource source) throws RemoteException, HandException, WrongStateException, WrongTurnException, WrongChoiceException;

}
