package it.polimi.ingsw.server;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import it.polimi.ingsw.controllers.PlayerControllerRMI;
import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.CardsManager;
import org.w3c.dom.html.HTMLMapElement;

public class Server extends UnicastRemoteObject implements ServerRMIInterface {
    private final Map<String, Match> matches;

    private final int portRMI;
    private final int portTCP;

    public Server(int portRMI, int portTCP) throws RemoteException {
        super();

        this.portRMI = portRMI;
        this.portTCP = portTCP;

        matches = new HashMap<>();

    }

    @Override
    public List<String> getJoinableMatches() {
        return matches.keySet().stream().filter(name -> !matches.get(name).isFull()).toList();
    }

    @Override
    public PlayerControllerRMI joinMatch(String matchName, String nickname) throws RemoteException, ChosenMatchException, AlreadyUsedNicknameException, WrongStateException, WrongStateException, AlreadyUsedNicknameException {
        if (!matches.containsKey(matchName))
            throw new ChosenMatchException("The chosen match doesn't exist");
        if (matches.get(matchName).isFull())
            throw new ChosenMatchException("The chosen match is already full");

        Match chosenMatch = matches.get(matchName);

        return new PlayerControllerRMI(nickname, chosenMatch, portRMI);
    }

    @Override
    // TODO: Implement this method, synchronize it
    public void createMatch(String matchName, int maxPlayers) throws RemoteException, ChosenMatchException {
        if (matches.containsKey(matchName))
            throw new ChosenMatchException("A match with the chosen name already exists");

        Match newMatch = getNewMatch(maxPlayers);

        matches.put(matchName, newMatch);
    }

    public Map<String, Match> getJoinableMatchesMap() {
        HashMap<String, Match> result = new HashMap<>();
        for (String name : matches.keySet()) {
            result.put(name, matches.get(name));
        }
        return result;
    }

    public Match getMatch(String name) {
        return matches.get(name);
    }

    // TODO: Implement this method
    public static Match getNewMatch(int maxPlayers) {
        GameDeck<Objective> objectivesDeck = new GameDeck<>();
        GameDeck<InitialCard> initialsDeck = new GameDeck<>();
        GameDeck<GoldCard> goldsDeck = new GameDeck<>();
        GameDeck<ResourceCard> resourcesDeck = new GameDeck<>();

        CardsManager.getInstance().getObjectives().forEach((id, card) -> objectivesDeck.add(card));
        CardsManager.getInstance().getInitialCards().forEach((id, card) -> initialsDeck.add(card));
        CardsManager.getInstance().getGoldCards().forEach((id, card) -> goldsDeck.add(card));
        CardsManager.getInstance().getResourceCards().forEach((id, card) -> resourcesDeck.add(card));

        return new Match(maxPlayers, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);
    }

    public void startRMIServer() throws RemoteException {
        Registry registry = LocateRegistry.createRegistry(portRMI);
        registry.rebind("CodexNaturalisRMIServer", this);
    }

    // TODO: Implement this method
    public void startTCPServer() {
    }

    public static String promptAndInput(String message, Scanner scanner) {
        System.out.print(message);
        return scanner.nextLine();
    }

    public static void main(String[] args) throws RemoteException {
        int portRMI = Integer.parseInt(args[0]);
        int portTCP = Integer.parseInt(args[1]);

        Scanner scanner = new Scanner(System.in);
        Server server = new Server(portRMI, portTCP);
        String choice;

        server.startRMIServer();
        server.startTCPServer();

        do {
            choice = promptAndInput("What do you want to do?\n\t0: exit\n\t1: create match\n\t2: show matches\n", scanner);

            switch (choice) {
                case "1" -> {
                    String matchName = promptAndInput("Match name: ", scanner);
                    int maxPlayers = Integer.parseInt(promptAndInput("Maximum number of players: ", scanner));

                    try {
                        server.createMatch(matchName, maxPlayers);
                    } catch (ChosenMatchException e) {
                        System.out.println(e.getMessage());
                    }
                }

                case "2" -> server.matches.keySet().forEach(System.out::println);
            }
        } while (!choice.equals("0"));
    }    

}
