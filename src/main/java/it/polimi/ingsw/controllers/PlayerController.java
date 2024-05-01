package it.polimi.ingsw.controllers;

import java.util.List;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.MatchObserver;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

/**
 * Controller for a match player, the only agent needing a view and so a controller in this application.
 * This class subclasses instances are given (in RMI case) / reachable (in TCP case) on the network and collected
 * by a corresponding view (RMI view or TCP view); then this class commits its two subclasses {@link PlayerControllerRMI}
 * and {@link PlayerControllerTCP} to implement all the methods needed by a generic view to play in a match.
 * This class implements {@link MatchObserver} since its instances subscribe themselves to a Match, as mentioned in
 * {@link #PlayerController(String, Match)}; this is needed to allow this class to behave as a bridge between a view
 * and a match.
 */
public abstract sealed class PlayerController implements MatchObserver permits PlayerControllerRMI, PlayerControllerTCP {
    protected Player player;
    protected Match match;

    /**
     * Instantiates the internal Player with the given nickname and sets the internal Match reference to the given one,
     * furthermore add the new Player instance to the match and subscribe this class instance to the match observers.
     * @param nickname The nickname of the new player of the Match
     * @param match The match to which this PlayerClass must pertain
     * @throws AlreadyUsedNicknameException If the nickname is already taken by another player of the same match
     * @throws WrongStateException If a new player cannot be added on the current state of the Match
     */
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

    /**
     * Draws an initial card.
     * @throws WrongStateException If the current match state doesn't allow drawing an initial card
     * @throws WrongTurnException If the current turn it's not the one of this player
     */
    public abstract void drawInitialCard() throws WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen initial card side.
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     * @throws WrongStateException If the current match state doesn't allow setting the initial card side
     * @throws WrongTurnException If the current turn it's not the one of this player
     */
    public abstract void chooseInitialCardSide(Side side) throws WrongStateException, WrongTurnException;

    /**
     * Draws two secret objectives.
     * @throws WrongStateException If the current match state doesn't allow drawing secret objectives
     * @throws WrongTurnException If the current turn it's not the one of this player
     */
    public abstract void drawSecretObjectives() throws WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen secret objective.
     * @param objective The chosen objective
     * @throws WrongStateException If the current match state doesn't allow choosing a secret objective
     * @throws WrongTurnException If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen objective is not one of the two drawn ones using {@link #drawSecretObjectives()}
     */
    public abstract void chooseSecretObjective(Objective objective) throws WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Plays a card.
     * @param coords The coordinates on which to place the card
     * @param card The PlayableCard to play
     * @param side The side on which to play the chosen card
     * @throws WrongStateException If the current match state doesn't allow playing cards
     * @throws WrongTurnException If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen card is not one of those in the player's current hand
     */
    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Draws a card.
     * @param source The drawing source to draw the card from
     * @throws HandException If the player already has a full hand of cards (three cards)
     * @throws WrongStateException If the current match state doesn't allow drawing cards
     * @throws WrongTurnException If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen DrawSource doesn't have any card left (i.e. it's empty)
     */
    public abstract void drawCard(DrawSource source) throws HandException, WrongStateException, WrongTurnException, WrongChoiceException;

}
