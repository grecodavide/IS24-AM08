package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.ViewInterface;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Subclass of {@link PlayerController} that implements its abstract methods through RMI interactions.
 * Each instance of this class is supposed to be sent through {@link it.polimi.ingsw.server.Server#joinMatch(String, String)})
 * to an RMI View, this latter will then send its View instance to the PlayerController object, calling
 * {@link #registerView(ViewInterface)} on it.
 */
public final class PlayerControllerRMI extends PlayerController implements PlayerControllerRMIInterface {
    // The remote View instance
    private ViewInterface view;

    /**
     * Instantiates the internal Player with the given nickname and sets the internal Match reference to the given one,
     * add the new Player instance to the match and subscribe this class instance to the match observers.
     * Moreover, this specific subclass exports the just instantiated object on the RMI registry listening on the given
     * port.
     *
     * @param nickname The nickname of the new player of the Match
     * @param match    The match to which this PlayerClass must pertain
     * @param port     The port on which the RMI Registry listens for requests
     * @throws AlreadyUsedNicknameException If the nickname is already taken by another player of the same match
     * @throws WrongStateException          If a new player cannot be added on the current state of the Match
     * @throws RemoteException              If the exportation process on the RMI registry fails due to network errors
     */
    public PlayerControllerRMI(String nickname, Match match, int port) throws AlreadyUsedNicknameException, WrongStateException, RemoteException {
        super(nickname, match);

        UnicastRemoteObject.exportObject(this, port);
    }

    /**
     * Sets the internal View attribute to the given argument; if it has already been called, it won't
     * do anything, since it's call is allowed once per PlayerController object.
     * It's used by a remote View having this class object to send itself over RMI to the PlayerControllerRMI
     * instance.
     * Note that this method is supposed to be called by a view.
     *
     * @param view The View to save in the PlayerController internal state
     */
    @Override
    public void registerView(ViewInterface view) {
        if (this.view == null)
            this.view = view;
    }

    /**
     * Draws an initial card for the player. Since this is done through RMI, it just involves a call to
     * {@link Player#drawInitialCard()}.
     * Note that this method is supposed to be called by a view.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing an initial card
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    @Override
    public void drawInitialCard() throws WrongStateException, WrongTurnException {
        player.drawInitialCard();
    }

    /**
     * Communicates the chosen initial card side. Since this is done through RMI, it just involves a call to
     * {@link Player#chooseInitialCardSide(Side)}.
     * Note that this method is supposed to be called by a view.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     * @throws WrongStateException If the current match state doesn't allow setting the initial card side
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    @Override
    public void chooseInitialCardSide(Side side) throws WrongStateException, WrongTurnException {
        player.chooseInitialCardSide(side);
    }

    /**
     * Draws two secret objectives. Since this is done through RMI, it just involves a call to
     * {@link Player#drawSecretObjectives()}.
     * Note that this method is supposed to be called by a view.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing secret objectives
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    @Override
    public void drawSecretObjectives() throws WrongStateException, WrongTurnException {
        player.drawSecretObjectives();
    }

    /**
     * Communicates the chosen secret objective. Since this is done through RMI, it just involves a call to
     * {@link Player#chooseSecretObjective(Objective)}.
     * Note that this method is supposed to be called by a view.
     *
     * @param objective The chosen objective
     * @throws WrongStateException  If the current match state doesn't allow choosing a secret objective
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen objective is not one of the two drawn ones using {@link #drawSecretObjectives()}
     */
    @Override
    public void chooseSecretObjective(Objective objective) throws WrongStateException, WrongTurnException, WrongChoiceException {
        player.chooseSecretObjective(objective);
    }

    /**
     * Plays a card. Since this is done through RMI, it just involves a call to
     * {@link Player#playCard(Pair, PlayableCard, Side)}.
     * Note that this method is supposed to be called by a view.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     * @throws WrongStateException  If the current match state doesn't allow playing cards
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen card is not one of those in the player's current hand
     */
    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException, WrongTurnException, WrongChoiceException {
        player.playCard(coords, card, side);
    }

    /**
     * Draws a card. Since this is done through RMI, it just involves a call to
     * {@link Player#drawCard(DrawSource)}.
     * Note that this method is supposed to be called by a view.
     *
     * @param source The drawing source to draw the card from
     * @throws HandException        If the player already has a full hand of cards (three cards)
     * @throws WrongStateException  If the current match state doesn't allow drawing cards
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen DrawSource doesn't have any card left (i.e. it's empty)
     */
    @Override
    public void drawCard(DrawSource source) throws HandException, WrongStateException, WrongTurnException, WrongChoiceException {
        player.drawCard(source);
    }

    /**
     * Sends a broadcast in the chat. Since this is done through RMI, it just involves a call to
     * {@link Player#sendBroadcastText(String)}.
     * Note that this method is supposed to be called by a view.
     *
     * @param text Text of the message
     */
    @Override
    public void sendBroadcastText(String text) {
        player.sendBroadcastText(text);
    }

    /**
     * Sends a private message in the chat. Since this is done through RMI, it just involves a call to
     * {@link Player#sendPrivateText(Player, String)} )}.
     * Note that this method is supposed to be called by a view.
     *
     * @param recipient nickname of the recipient
     * @param text      text of the message
     */
    @Override
    public void sendPrivateText(String recipient, String text) {
        if (match.getPlayers().stream().anyMatch(p -> p.getNickname().equals(recipient))) {
            Player p = match.getPlayers().stream()
                    .filter(pl -> pl.getNickname().equals(recipient))
                    .toList().getFirst();
            player.sendPrivateText(p, text);
        }
    }

    /**
     * Notifies that the match has just started.
     * Note that is supposed to be called by the match.
     */
    @Override
    public void matchStarted() {
        if (view == null) {
            onConnectionError();
        } else {
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
        if (view == null) {
            onConnectionError();
        } else {
            try {
                if (player.equals(someone)) {
                    List<String> playersNicknames = match.getPlayers().stream().map(Player::getNickname).toList();
                    view.giveLobbyInfo(playersNicknames);
                } else {
                    view.someoneJoined(someone.getNickname());
                }
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
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
        if (view == null) {
            onConnectionError();
        } else {
            try {
                view.someoneQuit(someone.getNickname());
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
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
        if (view == null) {
            onConnectionError();
        } else {
            try {
                if (player.equals(someone)) {
                    view.giveInitialCard(card);
                } else {
                    view.someoneDrewInitialCard(someone.getNickname(), card);
                }
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
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
    public void someoneSetInitialSide(Player someone, Side side) {
        if (view == null) {
            onConnectionError();
        } else {
            try {
                view.someoneSetInitialSide(someone.getNickname(), side);
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
    }

    /**
     * Notifies that someone has drawn two secret objectives.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     * If and only if the PlayerController receiving this method call is the one linked to given `someone`, it notifies
     * the view about the proposed objectives, the other views will just receive a notification about the player's nickname.
     *
     * @param someone    The player instance that has drawn the objectives
     * @param objectives The two proposed objectives
     */
    @Override
    public void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) {
        if (view == null) {
            onConnectionError();
        } else {
            try {
                if (player.equals(someone)) {
                    view.giveSecretObjectives(objectives);
                } else {
                    view.someoneDrewSecretObjective(someone.getNickname());
                }
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
    }

    /**
     * Notifies that someone has chosen the secret objective.
     * Note that this method is supposed to be called by a match, moreover the match calls this method on all the
     * MatchObservers instance subscribed to itself, then even the MatchObserver causing this event gets notified.
     * The view will just receive `someone` nickname, no the objective.
     *
     * @param someone   The player instance that has chosen the secret objective
     * @param objective The chosen secret objective
     */
    @Override
    public void someoneChoseSecretObjective(Player someone, Objective objective) {
        if (view == null) {
            onConnectionError();
        } else {
            try {
                view.someoneChoseSecretObjective(someone.getNickname());
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
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
    public void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        if (view == null) {
            onConnectionError();
        } else {
            try {
                view.someonePlayedCard(someone.getNickname(), coords, card, side);
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
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
     * @param replacementCard The card that has replaced the drawn card
     */
    @Override
    public void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard) {
        if (view == null) {
            onConnectionError();
        } else {
            try {
                view.someoneDrewCard(someone.getNickname(), source, card);
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
    }

    /**
     * Notifies that someone sent a message in the public chat.
     *
     * @param someone The player that send the message
     * @param text    Content of the message
     */
    @Override
    public void someoneSentBroadcastText(Player someone, String text) {
        if (view == null) {
            onConnectionError();
        } else {
            try {
                view.someoneSentBroadcastText(someone.getNickname(), text);
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
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
        if (view == null) {
            onConnectionError();
        } else {
            if (recipient.equals(this.player)) {
                try {
                    view.someoneSentPrivateText(someone.getNickname(), text);
                } catch (RemoteException e) {
                    onConnectionError();
                }
            }
        }
    }

    /**
     * Notifies that the match has just finished.
     */
    @Override
    public void matchFinished() {
        if (view == null) {
            onConnectionError();
        } else {
            try {
                view.matchFinished();
            } catch (RemoteException e) {
                onConnectionError();
            }
        }
    }

    /**
     * Removes the player linked to this PlayerControllerRMI instance when there's a connection error.
     */
    private void onConnectionError() {
        match.removePlayer(player);
        System.err.println("There has been a connection error with player: " + player.getNickname());
    }
}
