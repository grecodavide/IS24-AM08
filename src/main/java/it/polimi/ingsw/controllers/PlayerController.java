package it.polimi.ingsw.controllers;

import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.MatchObserver;
import it.polimi.ingsw.gamemodel.Player;

/**
 * Controller for a match player, the only agent needing a view and so a controller in this
 * application. This class subclasses instances are given (in RMI case) / reachable (in TCP case) on
 * the network and collected by a corresponding view (RMI view or TCP view); then this class commits
 * its two subclasses {@link PlayerControllerRMI} and {@link PlayerControllerTCP} to implement all
 * the methods needed by a generic view to play in a match. This class implements
 * {@link MatchObserver} since its instances subscribe themselves to a Match, as mentioned in
 * {@link #PlayerController(String, Match)}; this is needed to allow this class to behave as a
 * bridge between a view and a match.
 */
public abstract sealed class PlayerController implements MatchObserver permits PlayerControllerRMI, PlayerControllerTCP {
    protected Player player;
    protected Match match;

    /**
     * Instantiates the internal Player with the given username and sets the internal Match reference to
     * the given one, furthermore add the new Player instance to the match and subscribe this class
     * instance to the match observers.
     *
     * @param username The username of the new player of the Match
     * @param match The match to which this PlayerClass must pertain
     */
    public PlayerController(String username, Match match) {
        this.player = new Player(username, match);
        this.match = match;
    }

    /**
     * Gets the player 
     * @return
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Tries to effectively join a match, adding himself to the list of observers and the corresponding
     * player to the match, if the username is valid
     * 
     * @throws AlreadyUsedUsernameException if the username is already taken
     * @throws WrongStateException if the match currently does not accept new players
     */
    public void sendJoined() throws IllegalArgumentException, AlreadyUsedUsernameException, WrongStateException, ChosenMatchException {
        if (match == null) {
            throw new ChosenMatchException("The specified match does not exist");
        }

        try {
            match.subscribeObserver(this);
            this.match.addPlayer(this.player);
        } catch (AlreadyUsedUsernameException | IllegalArgumentException e) {
            match.unsubscribeObserver(this);
            throw e;
        }
    }
}
