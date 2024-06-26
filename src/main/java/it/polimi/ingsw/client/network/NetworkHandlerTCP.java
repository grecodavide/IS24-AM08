package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.controllers.PlayerControllerTCP;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.network.messages.Message;
import it.polimi.ingsw.network.messages.actions.*;
import it.polimi.ingsw.network.tcp.ClientReceiver;
import it.polimi.ingsw.network.tcp.IOHandler;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.Pair;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;

/**
 * Class used by a generic client to receive from and transmit to a remote {@link Server} instance and a
 * remote {@link PlayerControllerTCP} instance using the TCP protocol.
 */
public class NetworkHandlerTCP extends NetworkHandler {
    private final IOHandler io;
    private final Socket socket;

    /**
     * Initialize the instance all its internal attributes.
     *
     * @param graphicalView The GraphicalView to be subscribed to this NetworkHandler instance
     * @param ipAddress     The server IP address
     * @param port          The server port
     * @throws RemoteException If the remote server is considered not reachable any more and cannot return as usual
     */
    public NetworkHandlerTCP(GraphicalView graphicalView, String ipAddress, Integer port) throws IOException {
        super(graphicalView, ipAddress, port);
        this.socket = new Socket(ipAddress, port);
        this.io = new IOHandler(socket);
        new Thread(new ClientReceiver(this, socket)).start();
        connected = true;
        super.startConnectionCheck();
    }

    /**
     * Notifies the view about a remote error.
     *
     * @param exception The exception thrown remotely
     */
    public void notifyError(Exception exception) {
        this.graphicalView.notifyError(exception);
    }

    /**
     * Gets the player's username.
     *
     * @return The player's username
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Gets the I/O handler.
     *
     * @return The I/O handler
     */
    public IOHandler getIO() {
        return this.io;
    }

    /**
     * Asks the server to send a list of {@link AvailableMatch}.
     */
    @Override
    public void getAvailableMatches() {
        this.sendMessage(new GetAvailableMatchesMessage(this.username));
    }

    /**
     * Asks to create a match.
     *
     * @param matchName  The match name
     * @param maxPlayers The match maximum number of players
     */
    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        this.sendMessage(new CreateMatchMessage(this.username, matchName, maxPlayers));
    }

    /**
     * Asks to join a match.
     *
     * @param matchName the match's name
     */
    @Override
    public void joinMatch(String matchName) {
        this.sendMessage(new JoinMatchMessage(this.username, matchName));
    }

    /**
     * Draws an initial card for the player.
     */
    @Override
    public void drawInitialCard() {
        this.sendMessage(new DrawInitialCardMessage(this.username));
    }

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     */
    @Override
    public void chooseInitialCardSide(Side side) {
        this.sendMessage(new ChooseInitialCardSideMessage(this.username, side));
    }

    /**
     * Draws two secret objectives.
     */
    @Override
    public void drawSecretObjectives() {
        this.sendMessage(new DrawSecretObjectivesMessage(this.username));
    }

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     */
    @Override
    public void chooseSecretObjective(Objective objective) {
        this.sendMessage(new ChooseSecretObjectiveMessage(this.username, objective.getID()));
    }

    /**
     * Plays a card.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     */
    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        this.sendMessage(new PlayCardMessage(this.username, coords, card.getId(), side));
    }

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     */
    @Override
    public void drawCard(DrawSource source) {
        this.sendMessage(new DrawCardMessage(this.username, source));
    }

    /**
     * Sends a message to all the match players
     *
     * @param text The content of the message
     */
    @Override
    public void sendBroadcastText(String text) {
        this.sendMessage(new SendBroadcastTextMessage(this.username, text));
    }

    /**
     * Sends a private message to a match player
     *
     * @param recipient The recipient username
     * @param text      The content of the message
     */
    @Override
    public void sendPrivateText(String recipient, String text) {
        this.sendMessage(new SendPrivateTextMessage(this.username, recipient, text));
    }

    /**
     * Disconnects from the server.
     */
    @Override
    public void disconnect() {
        connected = false;
    }

    /**
     * Checks for connectivity.
     *
     * @return The status of the connection, true if active, false otherwise
     */
    @Override
    public boolean ping() {
        try {
            io.writeMsg("ping");
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private void sendMessage(Message msg) {
        try {
            this.io.writeMsg(msg);
        } catch (IOException e) {
            // handle IO
        }
    }
}
