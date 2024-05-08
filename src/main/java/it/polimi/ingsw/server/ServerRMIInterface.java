package it.polimi.ingsw.server;

import it.polimi.ingsw.controllers.PlayerControllerRMI;
import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongStateException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI interface used to declare all and only the methods callable on a remote Server instance implementing this
 * interface by a client.
 * For security reasons, each method doesn't expose to the receiving view important objects (e.g. Match), but
 * rather values representing them (e.g. Match unique name).
 */
public interface ServerRMIInterface extends Remote {
    /**
     * Returns the unique names of the available matches (those not full yet).
     *
     * @return The list of Match which are not full yet.
     * @throws RemoteException If the remote server is considered not to be reachable any more and cannot return as usual
     */
    List<String> getJoinableMatches() throws RemoteException;

    /**
     * Lets the calling view join on a match with the given player nickname, if possible; gives back to the client
     * an instance of its PlayerControllerRMI, to start communicating through it with the match.
     *
     * @param matchName The unique name of the match to join to
     * @param nickname  The chosen player nickname
     * @return An instance of PlayerControllerRMI, used exclusively by the calling view
     * @throws RemoteException              If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException         If the chosen match is either already full or doesn't exist
     * @throws AlreadyUsedNicknameException If the given nickname is already taken
     * @throws WrongStateException          If the match is in a state during which doesn't allow players to join any more
     */
    PlayerControllerRMI joinMatch(String matchName, String nickname) throws RemoteException, ChosenMatchException, AlreadyUsedNicknameException, WrongStateException;

    /**
     * Lets the calling view create a new match.
     *
     * @param matchName  The unique name to give to the new match
     * @param maxPlayers The maximum number of player allowed on the new match
     * @throws RemoteException      If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException If the given match name is already taken
     */
    void createMatch(String matchName, int maxPlayers) throws RemoteException, ChosenMatchException;
}
