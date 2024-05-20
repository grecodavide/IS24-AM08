package it.polimi.ingsw.client.network;

import java.rmi.RemoteException;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.Pair;

public abstract class NetworkView implements RemoteViewInterface {
    GraphicalView graphicalView;
    protected String username;
    protected String ipAddress;
    protected int port;

    /**
     * Initialize the instance all its internal attributes.
     *
     * @param graphicalView The GraphicalView to be subscribed to this NetworkView instance
     * @param ipAddress The server IP address
     * @param port The server port
     */
    public NetworkView(GraphicalView graphicalView, String ipAddress, int port) {
        this.graphicalView = graphicalView;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Sets the player's username.
     *
     * @param username The player's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public NetworkView(GraphicalView graphicalViewInterface) {
        this.graphicalView = graphicalViewInterface;
    }

    public abstract void showError(String cause, Exception Exception);
    
    /**
     * Asks the server to send a list of {@link AvailableMatch}
     */
    public abstract void getAvailableMatches() throws RemoteException;

    // Action Methods

    /**
     * Asks to create a match.
     *
     * @param matchName  The match name
     * @param maxPlayers The match maximum number of players
     */
    public abstract void createMatch(String matchName, Integer maxPlayers) throws ChosenMatchException, RemoteException;

    /**
     * Asks to join a match.
     *
     * @param matchName the match's name
     */
    public abstract void joinMatch(String matchName) throws ChosenMatchException, WrongStateException, AlreadyUsedUsernameException, RemoteException;

    /**
     * Draws an initial card for the player.
     */
    public abstract void drawInitialCard() throws WrongStateException, WrongTurnException, RemoteException;

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     */
    public abstract void chooseInitialCardSide(Side side) throws WrongStateException, WrongTurnException, RemoteException;

    /**
     * Draws two secret objectives.
     */
    public abstract void drawSecretObjectives() throws WrongStateException, WrongTurnException, RemoteException;

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     */
    public abstract void chooseSecretObjective(Objective objective) throws WrongStateException, WrongTurnException, RemoteException, WrongChoiceException;

    /**
     * Plays a card.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     */
    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException, WrongTurnException, RemoteException, WrongChoiceException;

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     */
    public abstract void drawCard(DrawSource source) throws HandException, WrongStateException, WrongTurnException, RemoteException, WrongChoiceException;
}
