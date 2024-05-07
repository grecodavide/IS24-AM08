package it.polimi.ingsw.controllers;

import java.rmi.RemoteException;
import java.util.List;

import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.HandException;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.exceptions.WrongTurnException;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.MatchObserver;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

public abstract sealed class PlayerController implements MatchObserver permits PlayerControllerRMI, PlayerControllerTCP {
    protected Player player;
    protected Match match;

    public PlayerController(String nickname, Match match) throws AlreadyUsedNicknameException, WrongStateException {
        List<String> playersNicknames = match.getPlayers().stream().map(Player::getNickname).toList();

        if (playersNicknames.contains(nickname))
            throw new AlreadyUsedNicknameException("The chosen nickname is already in use");

        this.player = new Player(nickname, match);
        this.match = match;

        match.addPlayer(player);

        // Subscribe to match this PlayerController as a MatchObserver
        match.subscribeObserver(this);
    }

    public abstract void drawInitialCard() throws WrongStateException, WrongTurnException, RemoteException;

    public abstract void chooseInitialCardSide(Side side) throws WrongStateException, WrongTurnException, RemoteException;

    public abstract void drawSecretObjectives() throws WrongStateException, WrongTurnException, RemoteException;
    
    public abstract void chooseSecretObjective(Objective objective) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    public abstract void drawCard(DrawSource source) throws RemoteException, HandException, WrongStateException, WrongTurnException, WrongChoiceException;


    public abstract void sendBroadcastText(String text);

    public abstract void sendPrivateText(String recipient, String text);

}
