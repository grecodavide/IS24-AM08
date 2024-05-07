package it.polimi.ingsw.controllers;

import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.HandException;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.exceptions.WrongTurnException;
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
import it.polimi.ingsw.network.messages.responses.SomeoneJoinedMessage;
import it.polimi.ingsw.network.messages.responses.SomeonePlayedCardMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneQuitMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneSentBroadcastTextMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneSentPrivateTextMessage;
import it.polimi.ingsw.network.messages.responses.SomeoneSetInitialSideMessage;
import it.polimi.ingsw.network.tcp.IOHandler;
import it.polimi.ingsw.utils.Pair;

public final class PlayerControllerTCP extends PlayerController {
    private IOHandler io;

    public PlayerControllerTCP(String nickname, Match match, IOHandler io) throws AlreadyUsedNicknameException, WrongStateException {
        super(nickname, match);

        try {
            this.io = io;
            Message joined = new SomeoneJoinedMessage(nickname, match.getPlayers(), match.getMaxPlayers());
            this.io.writeMsg(joined);
        } catch (Exception e) {
            e.printStackTrace();
            // match.removePlayer(player);
        }
    }

    private void sendMessage(Message msg) {
        try {
            this.io.writeMsg(msg);
        } catch (Exception e) {
            this.connectionError();
        }
    }

    private void connectionError() {
        match.removePlayer(player);
    }

    private ErrorMessage createErrorMessage(Exception e) {
        return new ErrorMessage(e.getMessage(), e.getClass().getName());
    }

    @Override
    public void matchStarted() {
        this.sendMessage(new MatchStartedMessage(match.getVisibleObjectives(), match.getVisiblePlayableCards(),
                match.getDecksTopReigns(), match.getPlayers()));
    }

    @Override
    public void someoneJoined(Player someone) {
        this.sendMessage(new SomeoneJoinedMessage(someone.getNickname(), match.getPlayers(), match.getMaxPlayers()));
    }

    @Override
    public void someoneQuit(Player someone) {
        this.sendMessage(new SomeoneQuitMessage(someone.getNickname(), match.getPlayers().size(), match.isFinished()));
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
        Integer objectiveID = null;
        if (someone.equals(player))
            objectiveID = objective.getID();
        this.sendMessage(new SomeoneChoseSecretObjectiveMessage(someone.getNickname(), objectiveID));
    }

    @Override
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        this.sendMessage(
                new SomeonePlayedCardMessage(someone.getNickname(), coords, card.getId(), side, someone.getPoints()));
    }

    @Override
    public void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard) {
        this.sendMessage(new SomeoneDrewCardMessage(someone.getNickname(), source, card.getId(),
                replacementCard.getId(), replacementCard.getReign()));
    }

    @Override
    public void matchFinished() {
        this.sendMessage(new MatchFinishedMessage(match.getPlayersFinalRanking()));
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

    @Override
    public void someoneSentBroadcastText(Player someone, String text) {
        Message msg = new SomeoneSentBroadcastTextMessage(someone.getNickname(), text);
        this.sendMessage(msg);
    }

    @Override
    public void someoneSentPrivateText(Player someone, Player recipient, String text) {
        if (recipient.getNickname().equals(this.player.getNickname())) {
            Message msg = new SomeoneSentPrivateTextMessage(someone.getNickname(), recipient.getNickname(), text);
            this.sendMessage(msg);
        }
    }

    @Override
    public void sendBroadcastText(String text) {
        this.player.sendBroadcastText(text);
    }

    @Override
    public void sendPrivateText(String recipientUsername, String text) {
        Player recipient = null;
        for (Player player : this.match.getPlayers()) {
            if (player.getNickname().equals(recipientUsername)) {
                recipient = player;
                break;
            }
        }

        // if you want to send error if recipient does not exist, change here
        if (recipient != null) {
            this.player.sendPrivateText(recipient, text);
        }
    }

}
