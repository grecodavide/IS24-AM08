package it.polimi.ingsw.controllers;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.RemoteException;
import java.util.List;

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
     * Instantiates the internal Player with the given username and sets the internal Match reference to the given one,
     * furthermore add the new Player instance to the match and subscribe this class instance to the match observers.
     *
     * @param username The username of the new player of the Match
     * @param match    The match to which this PlayerClass must pertain
     * @throws AlreadyUsedUsernameException If the username is already taken by another player of the same match
     * @throws WrongStateException          If a new player cannot be added on the current state of the Match
     */
    public PlayerController(String username, Match match) throws AlreadyUsedUsernameException, WrongStateException {
        this.player = new Player(username, match);
        this.match = match;

        match.addPlayer(player);

        // Subscribe to match this PlayerController as a MatchObserver
        match.subscribeObserver(this);
    }

    public Player getPlayer() {
        return player;
    }
}
