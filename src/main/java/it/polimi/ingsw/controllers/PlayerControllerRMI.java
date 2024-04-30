package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.ViewRMIInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlayerControllerRMI extends PlayerController implements PlayerControllerRMIInterface {

    public PlayerControllerRMI(String nickname, Match match, int port) throws AlreadyUsedNicknameException, RemoteException, WrongStateException {
        super(nickname, match);

        UnicastRemoteObject.exportObject(this, port);
    }

    @Override
    public void registerView(ViewRMIInterface view) {
        this.view = view;
    }

    @Override
    public void drawInitialCard() throws WrongStateException, WrongTurnException {
        player.drawInitialCard();
    }

    @Override
    public void chooseInitialCardSide(Side side) throws WrongStateException, WrongTurnException {
        player.chooseInitialCardSide(side);
    }

    @Override
    public void drawSecretObjectives() throws WrongStateException, WrongTurnException {
        player.drawSecretObjectives();
    }

    @Override
    public void chooseSecretObjective(Objective objective) throws WrongStateException, WrongTurnException, WrongChoiceException {
        player.chooseSecretObjective(objective);
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException, WrongTurnException, WrongChoiceException {
        player.playCard(coords, card, side);
    }

    @Override
    public void drawCard(DrawSource source) throws HandException, WrongStateException, WrongTurnException, WrongChoiceException {
        player.drawCard(source);
    }

    @Override
    public void matchStarted() {
        // Get visible objectives, visible playable cards and visible decks top reigns
        Pair<Objective, Objective> visibleObjectives = match.getVisibleObjectives();
        Map<DrawSource, PlayableCard> visiblePlayableCards = match.getVisiblePlayableCards();
        Pair<Symbol, Symbol> decksTopReigns = match.getDecksTopReigns();

        // Create a map that matches each pawn colour to the corresponding player's nickname
        Map<Color, String> playersNicknamesAndPawns = new HashMap<>();

        // Create a map that matches each player's nickname to the corresponding list of cards in the hand
        Map<String, List<PlayableCard>> playersHands = new HashMap<>();

        // Fill the maps with proper values
        for (Player p : match.getPlayers()) {
            playersNicknamesAndPawns.put(p.getPawnColor(), p.getNickname());
            playersHands.put(player.getNickname(), p.getBoard().getCurrentHand());
        }

        try {
            view.matchStarted(playersNicknamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards, decksTopReigns);
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    @Override
    public void someoneDrewInitialCard(Player someone, InitialCard card) {
        try {
            if (player.equals(someone)) {
                view.giveInitialCard(card);
            }
            else {
                view.someoneDrewInitialCard(someone.getNickname(), card);
            }
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    @Override
    public void someoneSetInitialSide(Player someone, Side side) {
        try {
            view.someoneSetInitialSide(someone.getNickname(), side);
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    @Override
    public void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) {
        try {
            if (player.equals(someone)) {
                view.giveSecretObjectives(objectives);
            }
            else {
                view.someoneDrewSecretObjective(someone.getNickname());
            }
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    @Override
    public void someoneChoseSecretObjective(Player someone) {
        try {
            view.someoneChoseSecretObjective(someone.getNickname());
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    @Override
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        try {
            view.someonePlayedCard(someone.getNickname(), coords, card, side);
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    @Override
    public void someoneDrewCard(Player someone, DrawSource source, Card card, Card replacementCard) {
        try {
            view.someoneDrewCard(someone.getNickname(), source, card);
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    @Override
    public void matchFinished() {
        try {
            view.matchFinished();
        } catch (RemoteException e) {
            onConnectionError();
        }
    }

    private void onConnectionError() {
        match.removePlayer(player);
    }
}
