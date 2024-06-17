package it.polimi.ingsw.client.network;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.controllers.PlayerControllerRMIInterface;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.server.ServerRMIInterface;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.Pair;

public class NetworkViewRMI extends NetworkView {
    private final ServerRMIInterface server;
    private PlayerControllerRMIInterface controller;
    private boolean exported = false;

    public NetworkViewRMI(GraphicalView graphicalView, String ipAddress, int port) throws RemoteException {
        super(graphicalView, ipAddress, port);

        // Try to get a remote Server instance from the network
        Registry registry = LocateRegistry.getRegistry(ipAddress, port);
        try {
            this.server = (ServerRMIInterface) registry.lookup("CodexNaturalisRMIServer");
            connected = true;
            this.startConnectionCheck();
        } catch (NotBoundException e) {
            // If the registry exists but the lookup string isn't found, exit the application since it's
            // a programmatic error (it regards the code, not the app life cycle)
            throw new RuntimeException(e);
        }
    }

    @Override
    public void getAvailableMatches() {
        try {
            List<AvailableMatch> matches = server.getJoinableMatches();
            this.receiveAvailableMatches(matches);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

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

    @Override
    public void createMatch(String matchName, Integer maxPlayers) {
        try {
            server.createMatch(matchName, maxPlayers);
            this.joinMatch(matchName);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void drawInitialCard() {
        try {
            controller.drawInitialCard();
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void chooseInitialCardSide(Side side) {
        try {
            controller.chooseInitialCardSide(side);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void drawSecretObjectives() {
        try {
            controller.drawSecretObjectives();
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void chooseSecretObjective(Objective objective) {
        try {
            controller.chooseSecretObjective(objective);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) {
        try {
            controller.playCard(coords, card, side);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void drawCard(DrawSource source) {
        try {
            controller.drawCard(source);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void sendBroadcastText(String text) {
        try {
            controller.sendBroadcastText(text);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void sendPrivateText(String recipient, String text) {
        try {
            controller.sendPrivateText(recipient, text);
        } catch (Exception e) {
            this.graphicalView.notifyError(e);
        }
    }

    @Override
    public void disconnect() {
        connected = false;
    }

    @Override
    public boolean ping() {
        try {
            return server.ping();
        } catch (RemoteException e) {
            return false;
        }
    }
}
