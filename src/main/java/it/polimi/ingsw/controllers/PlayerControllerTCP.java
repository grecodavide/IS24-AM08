package it.polimi.ingsw.controllers;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.*;
import it.polimi.ingsw.network.tcp.ClientListener;
import it.polimi.ingsw.network.tcp.IOHandler;
import it.polimi.ingsw.utils.Pair;

/**
 * TCP implementation of {@link PlayerController}. This will actually write to the socket's stream
 * the messages
 */
public final class PlayerControllerTCP extends PlayerController {
    private IOHandler io;

    /**
     * Class constructor. This will also send the {@link SomeoneJoinedMessage}, since the {@link Player}
     * won't actually join a {@link Match} until its {@link PlayerController} is not created
     * 
     * @param username the player's username
     * @param match the match the player wants to join
     * @param io the I/O handler, already created in {@link ClientListener}
     * @throws AlreadyUsedUsernameException
     * @throws WrongStateException
     */
    public PlayerControllerTCP(String username, Match match, IOHandler io) throws AlreadyUsedUsernameException, WrongStateException {
        super(username, match);

        try {
            this.io = io;
            Message joined = new SomeoneJoinedMessage(username, match.getPlayers(), match.getMaxPlayers());
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

    /**
     * Sends a {@link MatchStartedMessage}
     */
    @Override
    public void matchStarted() {
        this.sendMessage(new MatchStartedMessage(match.getVisibleObjectives(), match.getVisiblePlayableCards(), match.getDecksTopReigns(),
                match.getPlayers()));
    }

    /**
     * Send a {@link SomeoneJoinedMessage}
     *
     * @param someone Player who joined
     */
    @Override
    public void someoneJoined(Player someone) {
        this.sendMessage(new SomeoneJoinedMessage(someone.getUsername(), match.getPlayers(), match.getMaxPlayers()));
    }

    /**
     * Send a {@link SomeoneQuitMessage}
     *
     * @param someone Player who quit
     */
    @Override
    public void someoneQuit(Player someone) {
        this.sendMessage(new SomeoneQuitMessage(someone.getUsername(), match.getPlayers().size(), match.isFinished()));
    }

    /**
     * Send a {@link SomeoneDrewInitialCardMessage}
     *
     * @param someone Player who drew
     * @param card the drawn card
     */
    @Override
    public void someoneDrewInitialCard(Player someone, InitialCard card) {
        this.sendMessage(new SomeoneDrewInitialCardMessage(someone.getUsername(), card.getId()));
    }

    /**
     * Sends a {@link SomeoneSetInitialSideMessage}
     *
     * @param someone Player who set the initial card's side
     * @param side the chosen side
     */
    @Override
    public void someoneSetInitialSide(Player someone, Side side) {
        this.sendMessage(new SomeoneSetInitialSideMessage(someone.getUsername(), side));
    }

    /**
     * Sends a {@link SomeoneDrewSecretObjectivesMessage}
     * @param someone player who dre the secret objectives
     * @param objectives the (two) {@link Objective} drawn
     */
    @Override
    public void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) {
        Pair<Integer, Integer> IDs = new Pair<Integer, Integer>(objectives.first().getID(), objectives.second().getID());
        this.sendMessage(new SomeoneDrewSecretObjectivesMessage(someone.getUsername(), IDs));
    }

    /**
     * Sends a {@link SomeoneChoseSecretObjectiveMessage}
     * @param someone player who chose the secret objective
     * @param objective the chosen {@link Objective}
     */
    @Override
    public void someoneChoseSecretObjective(Player someone, Objective objective) {
        Integer objectiveID = null;
        if (someone.equals(player))
            objectiveID = objective.getID();
        this.sendMessage(new SomeoneChoseSecretObjectiveMessage(someone.getUsername(), objectiveID));
    }

    /**
     * Sends a {@link SomeonePlayedCardMessage}
     * @param someone player who played the card
     * @param coords coordinates in which the card was played
     * @param card the played card
     * @param side the side on which the card was played
     */
    @Override
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        this.sendMessage(new SomeonePlayedCardMessage(someone.getUsername(), coords, card.getId(), side, someone.getPoints(), someone.getBoard().getAvailableResources()));
    }

    /**
     * Sends a SomeoneDrewCardMessage
     * @param someone the player who drew the card
     * @param source from where the player drew the card
     * @param card the drawn card
     * @param replacementCard the next card to put in the spot of the drawn card
     */
    @Override
    public void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard) {
        Integer repId = null;
        Symbol repReign = null;
        if (replacementCard != null) {
            repId = replacementCard.getId();
            repReign = replacementCard.getReign();
        }
        this.sendMessage(new SomeoneDrewCardMessage(someone.getUsername(), source, card.getId(), repId, repReign));
    }
    /**
     * Sends a {@link MatchFinishedMessage}
     */
    @Override
    public void matchFinished() {
        this.sendMessage(new MatchFinishedMessage(match.getPlayersFinalRanking()));
    }

    /**
     * Sends a {@link DrawInitialCardMessage}
     */
   public void drawInitialCard() {
        try {
            this.player.drawInitialCard();
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }


    /**
     * Sends a {@link ChooseInitialCardSideMessage}
     *
     * @param side the chosen side
     */
    public void chooseInitialCardSide(Side side) {
        try {
            this.player.chooseInitialCardSide(side);
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    /**
     * Sends a {@link DrawSecretObjectivesMessage}
     */
    public void drawSecretObjectives() {
        try {
            this.player.drawSecretObjectives();
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    
    /**
     * Sends a {@link ChooseSecretObjectiveMessage}
     *
     * @param objective the chosen secret objective
     */
    public void chooseSecretObjective(Objective objective) {
        try {
            this.player.chooseSecretObjective(objective);
        } catch (WrongChoiceException | WrongStateException | WrongTurnException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    
    /**
     * Sends a {@link PlayCardMessage}
     *
     * @param coords the coords in which the card should be played
     * @param card the card to play
     * @param side the card's side
     */
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        try {
            this.player.playCard(coords, card, side);
        } catch (WrongChoiceException | WrongStateException | WrongTurnException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    
    /**
     * Sends a {@link DrawCardMessage}
     *
     * @param source the source from which the card should be drawn
     */
    public void drawCard(DrawSource source) {
        try {
            this.player.drawCard(source);
        } catch (HandException | WrongTurnException | WrongStateException | WrongChoiceException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    
    /**
     * Sends a {@link SomeoneSentBroadcastTextMessage}
     *
     * @param someone the player who sent the text
     * @param text the sent text
     */
    @Override
    public void someoneSentBroadcastText(Player someone, String text) {
        Message msg = new SomeoneSentBroadcastTextMessage(someone.getUsername(), text);
        this.sendMessage(msg);
    }

    
    /**
     * Sends a {@link SomeoneSentPrivateTextMessage}
     *
     * @param someone the player who sent the private text
     * @param recipient the text's recipient
     * @param text the sent text
     */
    @Override
    public void someoneSentPrivateText(Player someone, Player recipient, String text) {
        if (recipient.getUsername().equals(this.player.getUsername())) {
            Message msg = new SomeoneSentPrivateTextMessage(someone.getUsername(), recipient.getUsername(), text);
            this.sendMessage(msg);
        }
    }

    
    /**
     * Sends a {@link SendBroadcastTextMessage}
     *
     * @param text the text to send
     */
    public void sendBroadcastText(String text) {
        this.player.sendBroadcastText(text);
    }

    
    /**
     * Sends a {@link SendPrivateTextMessage}
     *
     * @param recipientUsername the recipient's username
     * @param text the text to send
     */
    public void sendPrivateText(String recipientUsername, String text) {
        Player recipient = null;
        for (Player player : this.match.getPlayers()) {
            if (player.getUsername().equals(recipientUsername)) {
                recipient = player;
                break;
            }
        }
        // NOTE: if you want to send error if recipient does not exist, change here
        if (recipient != null) {
            this.player.sendPrivateText(recipient, text);
        }
    }

}
