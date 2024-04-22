package it.polimi.ingsw.controllers;

import java.io.ObjectOutputStream;
import java.net.Socket;

import com.google.gson.Gson;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.Card;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.MatchFinishedMessage;
import it.polimi.ingsw.network.messages.responses.MatchStartedMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneChoseSecretObjectiveMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneDrewCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneDrewInitialCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneDrewSecretObjectivesMessage;
import it.polimi.ingsw.network.messages.responses.SomeonePlayedCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneSetInitialSideMessage;
import it.polimi.ingsw.utils.MessageJsonParser;
import it.polimi.ingsw.utils.Pair;

public class PlayerControllerTCP extends PlayerController {
    private Socket socket;
    private ObjectOutputStream outputStream;

    public PlayerControllerTCP(String nickname, Match match, Socket socket) throws AlreadyUsedNicknameException{
        super(nickname, match);
        try {
            this.socket = socket;
            this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            // match.removePlayer(player);
        }
    }

    private void sendMessage(Message msg) {
        try {
            MessageJsonParser parser = new MessageJsonParser();
            this.outputStream.writeObject(parser.toJson(msg));
        } catch (Exception e) {
            this.connectionError();
        }
    }

    private void connectionError() {
        try {
            match.removePlayer(player);
        } catch (PlayerQuitException e) {

        }
    }

    private ErrorMessage createErrorMessage(Exception e) {
        return new ErrorMessage(e.getMessage(), e.getClass().getName());
    }

    @Override
    public void matchStarted() {
        this.sendMessage(new MatchStartedMessage());
    }

    @Override
    public void someoneDrewInitialCard(Player someone, InitialCard card) {
        this.sendMessage(new SomeoneDrewInitialCardMessage(someone.getNickname(), card.getId()));
    }

    @Override
    public void someoneSetInitialSide(Player someone, Side side) {
        this.sendMessage(new SomeoneSetInitialSideMessage(someone.getNickname(), side));
    }

    @Override
    public void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) {
        Pair<Integer, Integer> IDs = new Pair<Integer, Integer>(objectives.first().getID(),
                objectives.second().getID());
        this.sendMessage(new SomeoneDrewSecretObjectivesMessage(someone.getNickname(), IDs));
    }

    @Override
    public void someoneChoseSecretObjective(Player someone, Objective objective) {
        this.sendMessage(new SomeoneChoseSecretObjectiveMessage(someone.getNickname(), objective.getID()));
    }

    @Override
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        this.sendMessage(new SomeonePlayedCardMessage(someone.getNickname(), coords, card.getId(), side));
    }

    @Override
    public void someoneDrewCard(Player someone, DrawSource source, Card card) {
        this.sendMessage(new SomeoneDrewCardMessage(someone.getNickname(), source, card.getId()));
    }

    @Override
    public void matchFinished() {
        this.sendMessage(new MatchFinishedMessage());
    }

    @Override
    public void drawInitialCard() {
        try {
            this.player.drawInitialCard();
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    @Override
    public void chooseInitialCardSide(Side side) {
        try {
            this.player.chooseInitialCardSide(side);
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    @Override
    public void drawSecretObjectives() {
        try {
            this.player.drawSecretObjectives();
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    @Override
    public void chooseSecretObjective(Objective objective) {
        try {
            this.player.chooseSecretObjective(objective);
        } catch (WrongChoiceException | WrongStateException | WrongTurnException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        try {
            this.player.playCard(coords, card, side);
        } catch (WrongChoiceException | WrongStateException | WrongTurnException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    @Override
    public void drawCard(DrawSource source) {
        try {
            this.player.drawCard(source);
        } catch (HandException | WrongTurnException | WrongStateException | WrongChoiceException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

}
