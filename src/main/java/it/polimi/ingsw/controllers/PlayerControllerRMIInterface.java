package it.polimi.ingsw.controllers;

import it.polimi.ingsw.client.network.RemoteViewInterface;
import it.polimi.ingsw.exceptions.*;
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
     * @throws AlreadyUsedUsernameException If the player username is already taken
     * @throws WrongNameException If the player username doesn't meet the alphanumerical criteria
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException If there's an error with the chosen match
     * @throws WrongStateException If the current match state doesn't allow registering a view
     */
    void registerView(RemoteViewInterface view) throws RemoteException, ChosenMatchException, WrongStateException, AlreadyUsedUsernameException, WrongNameException;

    /**
     * Draws an initial card for the player.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing an initial card
     * @throws WrongTurnException  If the current turn it's not the one of this player
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void drawInitialCard() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     * @throws WrongStateException If the current match state doesn't allow setting the initial card side
     * @throws WrongTurnException  If the current turn it's not the one of this player
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void chooseInitialCardSide(Side side) throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Draws two secret objectives.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing secret objectives
     * @throws WrongTurnException  If the current turn it's not the one of this player
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void drawSecretObjectives() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     * @throws WrongStateException  If the current match state doesn't allow choosing a secret objective
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen objective is not one of the two drawn ones using {@link #drawSecretObjectives()}
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
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
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
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
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void drawCard(DrawSource source) throws RemoteException, HandException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Sends a text to all the players in the match.
     *
     * @param text The text to be sent
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void sendBroadcastText(String text) throws RemoteException;

    /**
     * Sends a text just to a specific player in the match.
     *
     * @param recipient The username of the recipient
     * @param text      The text to be sent to the recipient
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void sendPrivateText(String recipient, String text) throws RemoteException;

    /**
     * Pings the server in order to perceive if the connection is still alive and working.
     *
     * @throws RemoteException If the connection to this class instance is not alive anymore
     * @return True if the connection is alive, false otherwise
     */
    boolean ping() throws RemoteException;
}
