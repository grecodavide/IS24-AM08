package it.polimi.ingsw.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import it.polimi.ingsw.controllers.PlayerControllerRMIInterface;
import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongNameException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.utils.AvailableMatch;

/**
 * RMI interface used to declare all and only the methods callable on a remote Server instance implementing this
 * interface by a client.
 * For security reasons, each method doesn't expose to the receiving view important objects (e.g. Match), but
 * rather values representing them (e.g. Match unique name).
 */
public interface ServerRMIInterface extends Remote {
    /**
     * Returns the available matches (those not full yet) as {@link AvailableMatch} instances.
     *
     * @return The list of Match which are not full yet.
     * @throws RemoteException If the remote server is considered not to be reachable any more and cannot return as usual
     */
    List<AvailableMatch> getJoinableMatches() throws RemoteException;

    /**
     * Lets the calling view join on a match with the given player username, if possible; gives back to the client
     * an instance of its PlayerControllerRMI, to start communicating through it with the match.
     *
     * @param matchName The unique name of the match to join to
     * @param username  The chosen player username
     * @return An instance of PlayerControllerRMI, used exclusively by the calling view
     * @throws RemoteException              If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException         If the chosen match is either already full or doesn't exist
     * @throws AlreadyUsedUsernameException If the given username is already taken
     * @throws WrongStateException          If the match is in a state during which doesn't allow players to join any more
     * @throws WrongNameException           If the name is not valid
     */
    PlayerControllerRMIInterface joinMatch(String matchName, String username) throws RemoteException, ChosenMatchException, AlreadyUsedUsernameException, WrongStateException, WrongNameException;

    /**
     * Lets the calling view create a new match.
     *
     * @param matchName  The unique name to give to the new match
     * @param maxPlayers The maximum number of player allowed on the new match
     * @throws RemoteException      If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException If the given match name is already taken
     */
    void createMatch(String matchName, int maxPlayers) throws RemoteException, ChosenMatchException, WrongNameException;

    /**
     * Ping the server
     */
    boolean ping() throws RemoteException;

}
