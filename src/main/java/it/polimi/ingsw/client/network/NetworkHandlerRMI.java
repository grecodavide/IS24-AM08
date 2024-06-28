package it.polimi.ingsw.client.network;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.List;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.controllers.PlayerController;
import it.polimi.ingsw.controllers.PlayerControllerRMI;
import it.polimi.ingsw.controllers.PlayerControllerRMIInterface;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.server.ServerRMIInterface;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.Pair;

/**
 * Class used by a generic client to receive from and transmit to a remote {@link Server} instance and a
 * remote {@link PlayerControllerRMI} instance using the RMI protocol.
 */
public class NetworkHandlerRMI extends NetworkHandler {
    private final ServerRMIInterface server;
    private PlayerControllerRMIInterface controller;
    private boolean exported = false;

    /**
     * Initialize the instance all its internal attributes.
     *
     * @param graphicalView The GraphicalView to be subscribed to this NetworkHandler instance
     * @param ipAddress     The server IP address
     * @param port          The server port
     * @throws RemoteException If the remote server is considered not reachable any more and cannot return as usual
     */
    public NetworkHandlerRMI(GraphicalView graphicalView, String ipAddress, int port) throws RemoteException {
        super(graphicalView, ipAddress, port);
        System.getProperties().setProperty("sun.rmi.transport.tcp.responseTimeout", "2000");
        // Try to get a remote Server instance from the network
        Registry registry = LocateRegistry.getRegistry(ipAddress, port);
        try {
            this.server = (ServerRMIInterface) registry.lookup("CodexNaturalisRMIServer");
            connected = true;
        } catch (NotBoundException e) {
            // If the registry exists but the lookup string isn't found, exit the application since it's
            // a programmatic error (it regards the code, not the app life cycle)
            throw new RuntimeException(e);
        }
    }

    /**
     * Asks the server to send a list of {@link AvailableMatch}.
     */
    @Override
    public void getAvailableMatches() {
        try {
            List<AvailableMatch> matches = server.getJoinableMatches();
            this.receiveAvailableMatches(matches);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Asks to join a match.
     *
     * @param matchName the match's name
     */
    @Override
    public void joinMatch(String matchName) {
        try {
            controller = server.joinMatch(matchName, this.username);

            // Export the object only if it was not previously exported
            if (!exported) {
                UnicastRemoteObject.exportObject(this, 0);
                exported = true;
            }
            controller.registerView(this);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Asks to create a match.
     *
     * @param matchName  The match name
     * @param maxPlayers The match maximum number of players
     */
    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        try {
            server.createMatch(matchName, maxPlayers);
            this.joinMatch(matchName);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Draws an initial card for the player.
     */
    @Override
    public void drawInitialCard() {
        try {
            controller.drawInitialCard();
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     */
    @Override
    public void chooseInitialCardSide(Side side) {
        try {
            controller.chooseInitialCardSide(side);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Draws two secret objectives.
     */
    @Override
    public void drawSecretObjectives() {
        try {
            controller.drawSecretObjectives();
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     */
    @Override
    public void chooseSecretObjective(Objective objective) {
        try {
            controller.chooseSecretObjective(objective);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
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
        try {
            controller.playCard(coords, card, side);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     */
    @Override
    public void drawCard(DrawSource source) {
        try {
            controller.drawCard(source);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Sends a message to all the match players
     *
     * @param text The content of the message
     */
    @Override
    public void sendBroadcastText(String text) {
        try {
            controller.sendBroadcastText(text);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    /**
     * Sends a private message to a match player
     *
     * @param recipient The recipient username
     * @param text The content of the message
     */
    @Override
    public void sendPrivateText(String recipient, String text) {
        try {
            controller.sendPrivateText(recipient, text);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
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
            controller.ping();
            super.lastPing = Calendar.getInstance().getTime().toInstant();
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
