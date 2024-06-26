package it.polimi.ingsw.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.polimi.ingsw.client.network.RemoteViewInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.*;
import it.polimi.ingsw.network.tcp.IOHandler;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.PlacedCardRecord;

/**
 * Subclass of {@link PlayerController} that implements its abstract methods through TCP interactions.
 */
public final class PlayerControllerTCP extends PlayerController {
    private final IOHandler io;

    /**
     * Instantiates the internal Player with the given username and sets the internal Match reference to the given one,
     * add the new Player instance to the match and subscribe this class instance to the match observers.
     *
     * @param username The username of the new player of the Match
     * @param match    The match to which this PlayerClass must pertain
     * @param io       The I/O handler to be attached to this instance
     */
    public PlayerControllerTCP(String username, Match match, IOHandler io) {
        super(username, match);
        this.io = io;
    }

    /**
     * Utility method to send a message object over the network.
     *
     * @param msg The message object to be sent
     */
    private void sendMessage(Message msg) {
        try {
            this.io.writeMsg(msg);
        } catch (Exception e) {
            this.connectionError();
        }
    }

    /**
     * Utility method called when there's a connection error, it removes the player from the match.
     */
    private void connectionError() {
        match.removePlayer(player);
        match.unsubscribeObserver(this);
    }

    /**
     * Utility method to create an {@link ErrorMessage} object from an exception.
     *
     * @param exception The exception
     * @return The nex ErrorMessage instance
     */
    private ErrorMessage createErrorMessage(Exception exception) {
        return new ErrorMessage(exception.getMessage(), exception.getClass().getName());
    }

    /**
     * Notifies that the match has just started.
     * Note that is supposed to be called by the match.
     */
    @Override
    public void matchStarted() {
        this.sendMessage(new MatchStartedMessage(match.getVisibleObjectives(),
                match.getVisiblePlayableCards(), match.getDecksTopReigns(), match.getPlayers()));
    }

    /**
     * Notifies that someone has joined the match.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     * If and only if the PlayerController receiving this method call is the one linked to given `someone`, it notifies
     * the view about the current lobby information.
     *
     * @param someone The Player instance that has joined
     */
    @Override
    public void someoneJoined(Player someone) {
        this.sendMessage(new SomeoneJoinedMessage(someone.getUsername(), match.getPlayers(),
                match.getMaxPlayers()));
    }

    /**
     * Notifies that someone has quit from the match.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     *
     * @param someone The Player instance that has quit
     */
    @Override
    public void someoneQuit(Player someone) {
        this.sendMessage(new SomeoneQuitMessage(someone.getUsername(), match.getPlayers().size(),
                match.isFinished()));
    }

    /**
     * Notifies that someone has drawn its initial card.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     * If and only if the PlayerController receiving this method call is the one linked to given `someone`, it notifies
     * the view that it received an initial card.
     *
     * @param someone The player instance that has drawn the card
     * @param card    The initial card that has been drawn
     */
    @Override
    public void someoneDrewInitialCard(Player someone, InitialCard card) {
        this.sendMessage(new SomeoneDrewInitialCardMessage(someone.getUsername(), card.getId()));
    }

    /**
     * Notifies that someone has chosen its initial card side.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     *
     * @param someone The player instance that has chosen the side
     * @param side    The chosen initial card side
     */
    @Override
    public void someoneSetInitialSide(Player someone, Side side,
            Map<Symbol, Integer> availableResources) {
        this.sendMessage(
                new SomeoneSetInitialSideMessage(someone.getUsername(), side, availableResources));
    }

    /**
     * Notifies that someone has drawn two secret objectives.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     * If and only if the PlayerController receiving this method call is the one linked to given `someone`, it notifies
     * the view about the proposed objectives, the other views will just receive a notification about the player's username.
     *
     * @param someone    The player instance that has drawn the objectives
     * @param objectives The two proposed objectives
     */
    @Override
    public void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) {
        Pair<Integer, Integer> IDs =
                new Pair<>(objectives.first().getID(), objectives.second().getID());
        this.sendMessage(new SomeoneDrewSecretObjectivesMessage(someone.getUsername(), IDs));
    }

    /**
     * Notifies that someone has chosen the secret objective.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     * The view will just receive `someone` username, no the objective.
     *
     * @param someone   The player instance that has chosen the secret objective
     * @param objective The chosen secret objective
     */
    @Override
    public void someoneChoseSecretObjective(Player someone, Objective objective) {
        Integer objectiveID = null;
        if (someone.equals(player))
            objectiveID = objective.getID();
        this.sendMessage(
                new SomeoneChoseSecretObjectiveMessage(someone.getUsername(), objectiveID));
    }

    /**
     * Notifies that someone has played a card.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     *
     * @param someone The Player instance that has played a card
     * @param coords  The coordinates on which the card has been placed
     * @param card    The PlayableCard that has been played
     * @param side    The side on which the card has been placed
     */
    @Override
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card,
            Side side) {
        this.sendMessage(new SomeonePlayedCardMessage(someone.getUsername(), coords, card.getId(),
                side, someone.getPoints(), someone.getBoard().getAvailableResources()));
    }

    /**
     * Notifies that someone has drawn a card.
     * The replacement card is the one that has taken the place of the drawn one, it's needed since observers have to
     * know the reign of the new card on top of the decks.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     *
     * @param someone         The Player instance that has drawn a card
     * @param source          The drawing source from which the card has been drawn
     * @param card            The card that has been drawn
     * @param replacementCard The card that has replaced the drawn card, null if the draw source is a deck
     */
    @Override
    public void someoneDrewCard(Player someone, DrawSource source, PlayableCard card,
            PlayableCard replacementCard) {
        Integer repId = null;
        if (replacementCard != null) {
            repId = replacementCard.getId();
        }
        this.sendMessage(new SomeoneDrewCardMessage(someone.getUsername(), source, card.getId(),
                repId, match.getDecksTopReigns()));
    }

    /**
     * Notifies that someone sent a message in the public chat.
     *
     * @param someone The player that send the message
     * @param text    Content of the message
     */
    @Override
    public void someoneSentBroadcastText(Player someone, String text) {
        Message msg = new SomeoneSentBroadcastTextMessage(someone.getUsername(), text);
        this.sendMessage(msg);
    }

    /**
     * Notifies that someone sent a private message to another user.
     * If the recipient is the current player, then the view is notified,
     * otherwise the message is ignored.
     *
     * @param someone   The player that sent the message
     * @param recipient The recipient of the message
     * @param text      Content of the message
     */
    @Override
    public void someoneSentPrivateText(Player someone, Player recipient, String text) {
        if (recipient.getUsername().equals(this.player.getUsername())
                || someone.getUsername().equals(this.player.getUsername())) {
            Message msg = new SomeoneSentPrivateTextMessage(someone.getUsername(),
                    recipient.getUsername(), text);
            this.sendMessage(msg);
        }
    }

    /**
     * Notifies that the match has just finished.
     */
    @Override
    public void matchFinished() {
        this.sendMessage(new MatchFinishedMessage(match.getPlayersFinalRanking()));
    }


    public void drawInitialCard() {
        try {
            this.player.drawInitialCard();
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }


    public void chooseInitialCardSide(Side side) {
        try {
            this.player.chooseInitialCardSide(side);
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    public void drawSecretObjectives() {
        try {
            this.player.drawSecretObjectives();
        } catch (WrongTurnException | WrongStateException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    public void chooseSecretObjective(Objective objective) {
        try {
            this.player.chooseSecretObjective(objective);
        } catch (WrongChoiceException | WrongStateException | WrongTurnException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        try {
            this.player.playCard(coords, card, side);
        } catch (WrongChoiceException | WrongStateException | WrongTurnException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    public void drawCard(DrawSource source) {
        try {
            this.player.drawCard(source);
        } catch (HandException | WrongTurnException | WrongStateException
                | WrongChoiceException e) {
            this.sendMessage(this.createErrorMessage(e));
        }
    }

    /**
     * Sends a broadcast in the chat.
     *
     * @param text Text of the message
     */
    public void sendBroadcastText(String text) {
        this.player.sendBroadcastText(text);
    }

    /**
     * Sends a private message in the chat.
     *
     * @param recipientUsername username of the recipient
     * @param text      text of the message
     */
    public void sendPrivateText(String recipientUsername, String text) {
        Player recipient = null;
        for (Player player : this.match.getPlayers()) {
            if (player.getUsername().equals(recipientUsername)) {
                recipient = player;
                break;
            }
        }

        // if you want to send error if recipient does not exist, change here
        if (recipient != null) {
            this.player.sendPrivateText(recipient, text);
        }
    }

    /**
     * Notifies the view that match has resumed after a server crash.
     */
    @Override
    public void matchResumed() {
        Map<String, Color> playersUsernamesAndPawns = new HashMap<>();
        Map<String, List<Integer>> playersHands = new HashMap<>();
        Pair<Integer, Integer> visibleObjectives;
        Map<DrawSource, Integer> visiblePlayableCards = new HashMap<>();
        Pair<Symbol, Symbol> decksTopReigns;
        Integer secretObjective;
        Map<String, Map<Symbol, Integer>> availableResources = new HashMap<>();
        Map<String, Map<Integer, PlacedCardRecord>> placedCards = new HashMap<>();
        Map<String, Integer> playerPoints = new HashMap<>();
        String currentPlayer;
        boolean drawPhase;

        this.match.getPlayers().forEach(player -> {
            String username = player.getUsername();
            Board board = player.getBoard();
            playersUsernamesAndPawns.put(username, player.getPawnColor());
            playersHands.put(username, board.getCurrentHand().stream().map(Card::getId).collect(Collectors.toList()));
            availableResources.put(username, board.getAvailableResources());

            Map<Integer, PlacedCardRecord> placed = new HashMap<>();
            board.getPlacedCards()
                    .forEach((coords, placedCard) -> placed.put(placedCard.getTurn(),
                            new PlacedCardRecord(placedCard.getCard().getId(), coords.first(),
                                    coords.second(), placedCard.getPlayedSide())));

            placedCards.put(username, placed);
            playerPoints.put(username, player.getPoints());
        });

        Pair<Objective, Objective> visibleObjectivesValue = this.match.getVisibleObjectives();
        // Get a Set of Entry, which contains key and value, and create a new Hashmap with key and value.ID
        visibleObjectives = new Pair<>(visibleObjectivesValue.first().getID(), visibleObjectivesValue.second().getID());
        visiblePlayableCards = this.match.getVisiblePlayableCards().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getId()));
        decksTopReigns = this.match.getDecksTopReigns();
        secretObjective = this.player.getSecretObjective().getID();
        currentPlayer = this.match.getCurrentPlayer().getUsername();
        drawPhase = this.match.getCurrentState().getClass().equals(AfterMoveState.class);


        Message msg = new MatchResumedMessage(playersUsernamesAndPawns, playersHands,
        visibleObjectives, visiblePlayableCards, decksTopReigns, secretObjective,
        availableResources, placedCards, playerPoints, currentPlayer, drawPhase);

        this.sendMessage(msg);
    }
}
