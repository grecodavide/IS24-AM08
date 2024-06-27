package it.polimi.ingsw.server;

import it.polimi.ingsw.client.network.NetworkHandler;
import it.polimi.ingsw.client.network.NetworkHandlerRMI;
import it.polimi.ingsw.controllers.PlayerController;
import it.polimi.ingsw.controllers.PlayerControllerRMI;
import it.polimi.ingsw.controllers.PlayerControllerRMIInterface;
import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongNameException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.network.tcp.TCPServer;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.DeckCreator;
import it.polimi.ingsw.utils.GuiUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The server class of this application. It's appointed with managing remote interactions with clients
 * ({@link NetworkHandler}) before the match starts, after that {@link PlayerController} will ensure the
 * communication.
 * To be specific, it stores all the {@link Match} instances available (not full) or being played at the moment,
 * creates them when requested by clients and restores them from disk (since periodically serialized) after a
 * Server crash.
 */
public class Server extends UnicastRemoteObject implements ServerRMIInterface {
    private final Map<String, Match> matches;
    private final int portRMI;
    private final int portTCP;

    /**
     * Initializes this Server instance and its attributes.
     *
     * @param portRMI The RMI port to listen to
     * @param portTCP The TCP port to listen to
     * @throws RemoteException If this instance couldn't be exported on the public RMI registry, so there's been a
     *                         connection error
     */
    public Server(int portRMI, int portTCP) throws RemoteException {
        super();

        this.portRMI = portRMI;
        this.portTCP = portTCP;

        matches = new HashMap<>();
    }

    /**
     * Returns the available matches as {@link AvailableMatch} instances.
     * This method is called just by remote {@link NetworkHandlerRMI} instances.
     *
     * @return The list of Match which are not full yet.
     */
    @Override
    public List<AvailableMatch> getJoinableMatches() {
        // List of names of matches that are not full (then joinable)
        List<String> joinableMatches = matches.keySet().stream()
                .filter(name -> !matches.get(name).isFull())
                .toList();
        List<AvailableMatch> result = new ArrayList<>();

        for (String name : matches.keySet()) {
            Match match = matches.get(name);
            int maxPlayers = match.getMaxPlayers();
            int currentPlayers = match.getPlayers().size();

            result.add(new AvailableMatch(name, maxPlayers, currentPlayers, match.isRejoinable()));
        }

        return result;
    }

    /**
     * Lets the calling view join on a match with the given player username, if possible; gives back to the client
     * an instance of its PlayerControllerRMI, to start communicating through it with the match.
     * This method is called just by remote {@link NetworkHandlerRMI} instances.
     *
     * @param matchName The unique name of the match to join to
     * @param username  The chosen player username
     * @return An instance of PlayerControllerRMI, used exclusively by the calling view
     * @throws ChosenMatchException         If the chosen match is either already full or doesn't exist
     * @throws AlreadyUsedUsernameException If the given username is already taken
     * @throws WrongStateException          If the match is in a state during which doesn't allow players to join any more
     * @throws WrongNameException           If the name is not valid
     * @throws RemoteException              If the exportation of this object in the RMI registry failed
     */
    @Override
    public PlayerControllerRMIInterface joinMatch(String matchName, String username) throws ChosenMatchException, WrongStateException, AlreadyUsedUsernameException, WrongNameException, RemoteException {
        if (!GuiUtil.isValidName(username))
            throw new WrongNameException("The username must be alphanumeric with maximum 32 characters");
        if (!matches.containsKey(matchName))
            throw new ChosenMatchException("The chosen match doesn't exist");
        if (matches.get(matchName).isFull() && !matches.get(matchName).isRejoinable())
            throw new ChosenMatchException("The chosen match is already full");

        Match chosenMatch = matches.get(matchName);
        PlayerControllerRMI controller = new PlayerControllerRMI(username, chosenMatch);

        UnicastRemoteObject.exportObject(controller, portRMI);

        return controller;
    }

    /**
     * Create a new blank match.
     *
     * @param matchName  The unique name to give to the new match
     * @param maxPlayers The maximum number of player allowed on the new match
     * @throws ChosenMatchException If the given match name is already taken
     * @throws WrongNameException   If the chosen player username doesn't meet the alphanumerical criteria
     */
    @Override
    public void createMatch(String matchName, int maxPlayers) throws ChosenMatchException, WrongNameException {
        if (!GuiUtil.isValidName(matchName)) {
            throw new WrongNameException("The match name must be alphanumeric with maximum 32 characters");
        }
        synchronized (matches) {
            if (matches.containsKey(matchName))
                throw new ChosenMatchException("A match with the chosen name already exists");

            Match newMatch = getNewMatch(maxPlayers);
            newMatch.subscribeObserver(new MatchStatusObserver(matchName, matches));
            matches.put(matchName, newMatch);
        }
    }

    /**
     * Pings the server in order to perceive if the connection is still alive and working.
     * Always return true, since the false is implicit in returning a {@link RemoteException}
     * when the connection is not working anymore.
     *
     * @return True if the connection is alive, false otherwise
     */
    @Override
    public boolean ping() {
        return true;
    }

    /**
     * @return
     */
    public Map<String, Match> getJoinableMatchesMap() {
        synchronized (matches) {
            HashMap<String, Match> result = new HashMap<>();
            for (String name : matches.keySet()) {
                result.put(name, matches.get(name));
            }
            return result;
        }
    }

    /**
     * Gets a {@link Match} from those saved in the server.
     *
     * @param name The unique name of the match
     * @return The match instance
     */
    public Match getMatch(String name) {
        return matches.get(name);
    }

    /**
     * Start the RMI server.
     *
     * @throws RemoteException If the remote registry couldn't be exported or the communication with it failed.
     */
    public void startRMIServer() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(portRMI);
        registry.rebind("CodexNaturalisRMIServer", this);
    }

    /**
     * Starts the TCP server.
     */
    public void startTCPServer() {
        TCPServer tcpServer = new TCPServer(portTCP, this);
        new Thread(tcpServer::listen).start();
    }

    public static void main(String[] args) throws RemoteException {
        int portRMI;
        int portTCP;

        // If some arguments are missing, notify it to the user and exit
        if (args.length < 2) {
            System.err.println("Arguments missing, run the server executable with RMI port and TCP port arguments.");
            System.err.println("Defaulting to RMI 2222 and TCP 9999 ports...");
            portRMI = 2222;
            portTCP = 9999;
        } else {
            portRMI = Integer.parseInt(args[0]);
            portTCP = Integer.parseInt(args[1]);
        }

        Server server = new Server(portRMI, portTCP);

        server.loadCrashedMatches();
        server.startRMIServer();
        server.startTCPServer();
    }

    /**
     * Utility method used to restore all the matches saved in the disk after the server crashed.
     */
    private void loadCrashedMatches() {
        // Look for *.match files in the current directory
        File dir = new File(".");
        File[] files = dir.listFiles((file, name) -> name.toLowerCase().endsWith(".match"));
        // If any file is found
        if (files != null) {
            for (File file : files) {
                try {
                    // Read each .match file from disk
                    FileInputStream fileIn = new FileInputStream(file);
                    ObjectInputStream in = new ObjectInputStream(fileIn);

                    // Deserialize the .match file in a Match object
                    String matchName = file.getName().replaceAll("(?i)(.*)\\.match", "$1");
                    Match match = (Match) in.readObject();
                    matches.put(matchName, match);
                    match.getPlayers().forEach((p) -> p.setConnected(false));
                    match.subscribeObserver(new MatchStatusObserver(matchName, matches));
                    in.close();
                    fileIn.close();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("A match couldn't be loaded from disk");
                }
            }
        }
    }

    /**
     * Utility method to create a new blank match. It cannot be called remotely (e.g. by RMI)
     *
     * @param maxPlayers The maximum number of players allowed
     * @return The new match instance
     */
    private static Match getNewMatch(int maxPlayers) {
        DeckCreator creator = new DeckCreator();
        return new Match(maxPlayers, creator.createInitialDeck(), creator.createResourceDeck(), creator.createGoldDeck(),
                creator.createObjectiveDeck());
    }
}
