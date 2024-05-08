package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.ViewRMIInterface;
import it.polimi.ingsw.exceptions.HandException;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.exceptions.WrongTurnException;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * RMI interface used to declare all and only the methods callable on a remote PlayerControllerRMI instance implementing
 * this interface. Since it's a remote interface, all the methods here defined are meant to notify the occurrence of an
 * event to the remote object. Given this, all methods also contain some parameters specific to the happened event (e.g.
 * a user clicked the button to play a card on the GUI view, then {@link #playCard(Pair, PlayableCard, Side)}) is called.
 */
public interface PlayerControllerRMIInterface extends Remote {
    /**
     * Register the given view as the one attached to the remote PlayerControllerRMI instance; if it has already been
     * called, it won't do anything, since it's call is allowed once per PlayerController object.
     * It's used by a remote View having this class object to send itself over RMI to the PlayerControllerRMI
     * instance.
     *
     * @param view The View to save in the PlayerController internal state
     */
    void registerView(ViewInterface view) throws RemoteException;

    /**
     * Draws an initial card for the player.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing an initial card
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    void drawInitialCard() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     * @throws WrongStateException If the current match state doesn't allow setting the initial card side
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    void chooseInitialCardSide(Side side) throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Draws two secret objectives.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing secret objectives
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    void drawSecretObjectives() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     * @throws WrongStateException  If the current match state doesn't allow choosing a secret objective
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen objective is not one of the two drawn ones using {@link #drawSecretObjectives()}
     */
    void chooseSecretObjective(Objective objective) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Plays a card.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     * @throws WrongStateException  If the current match state doesn't allow playing cards
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen card is not one of those in the player's current hand
     */
    void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     * @throws HandException        If the player already has a full hand of cards (three cards)
     * @throws WrongStateException  If the current match state doesn't allow drawing cards
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen DrawSource doesn't have any card left (i.e. it's empty)
     */
    void drawCard(DrawSource source) throws RemoteException, HandException, WrongStateException, WrongTurnException, WrongChoiceException;
}
