package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.client.network.NetworkViewRMI;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.RequestStatus;

/*
 * TODO: - Implement correctly the messages: have a list of strings, containing all messages to be
 * shown - When a text gets sent, add it to list of messages - Come up with a decent enough prompt -
 * Show current player name (either as a message, or in a corner) - Can be done but not necessary:
 * add a view with objectives, hand etc all in one place - Method to show chat - Method to write to
 * chat - Debug what happens when it fails to join match (getAvailableMatches gets stuck)
 */

/**
 * Class that handles client game loop from TUI
 */
public class GraphicalViewTUI extends GraphicalView {
    private final TuiPrinter printer;
    private final Scanner scanner;
    private String lastError;
    private boolean ongoing;
    private List<String> playersWithObjective;
    private final PlayerControls playerControls;

    private List<String> chat;

    public GraphicalViewTUI() {
        super();
        this.ongoing = true;
        this.playersWithObjective = new ArrayList<>();
        this.playerControls = new PlayerControls();
        this.playerControls.disable();
        this.chat = new ArrayList<>();
        try {
            this.printer = new TuiPrinter();
        } catch (Exception e) {
            throw new RuntimeException("Could not access terminal. Quitting now");
        }

        this.scanner = new Scanner(System.in);
    }

    private void startInterface() {
        this.printer.clearTerminal();
        this.setNetwork();
        this.printer.clearTerminal();
        this.setMatch();
        new Thread(() -> {
            this.startPlayerControls();
        }).start();
    }

    ///////////////////////
    // AUXILIARY METHODS //
    ///////////////////////
    private String askUser(String prompt) {
        this.clearStdin();
        this.printer.printPrompt(prompt);
        String userIn = this.scanner.nextLine();
        this.printer.clearTerminal();
        return userIn;
    }

    @Override
    public void setLastRequestStatus(RequestStatus status) {
        synchronized (this.lastRequest) {
            super.setLastRequestStatus(status);
            if (!status.equals(RequestStatus.PENDING)) {
                this.lastRequest.notifyAll();
            }
        }
    }

    private boolean getServerResponse() {
        this.printer.printCenteredMessage("Waiting for server...", 1);
        try {
            synchronized (this.lastRequest) {
                while (this.lastRequest.getStatus().equals(RequestStatus.PENDING)) {
                    this.lastRequest.wait();
                }
            }
            return this.lastRequest.getStatus().equals(RequestStatus.SUCCESSFUL);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }

    }

    private synchronized String printHand(String prompt, ClientBoard board) {
        this.printer.clearTerminal();
        this.printer.printPlayerBoard(this.username, board);
        this.printer.printHandAtBottom(board.getHand());
        return this.askUser(prompt);
    }

    private PlayableCard chooseCardFromHand(ClientBoard board) {
        List<PlayableCard> hand = board.getHand();

        String userIn = this.printHand("Choose card to play (1, 2, 3):", board);

        PlayableCard card = null;
        Integer maxValue = hand.size();
        while (card == null) {
            try {
                Integer index = Integer.valueOf(userIn) - 1;
                if (index >= 0 && index < maxValue) {
                    card = hand.get(index);
                }
            } catch (NumberFormatException e) {
                userIn = this.printHand("Not a valid number! try again", board);
            }
        }

        return card;
    }

    private Side chooseCardSide(PlayableCard card) {
        this.printer.clearTerminal();
        this.printer.printPlayableFrontAndBack(card, 0);
        String userIn = this.askUser("Choose side (b for back, default to front)");
        switch (userIn) {
            case "b":
                return Side.BACK;
            default:
                return Side.FRONT;
        }
    }

    private Pair<Integer, Integer> chooseCoords(ClientBoard board) {
        String prompt = "Choose coordinates for card (e.g. 1,1)";
        String userIn;

        Integer x = null, y = null;
        Integer splitIndex;
        while (x == null || y == null) {
            this.printer.clearTerminal();
            this.printer.printPlayerBoard(this.username, board);
            userIn = this.askUser(prompt);

            splitIndex = userIn.indexOf(",");
            if (splitIndex != -1) {
                try {
                    x = Integer.valueOf(userIn.substring(0, splitIndex));
                } catch (NumberFormatException e) {
                    prompt = "X coordinate is not a number! Try again.";
                }

                try {
                    y = Integer.valueOf(userIn.substring(splitIndex + 1, userIn.length()));
                } catch (NumberFormatException e) {
                    prompt = "Y coordinate is not a number! Try again.";
                }
            } else {
                prompt = "Not a valid format! try again.";
            }
        }

        return new Pair<Integer, Integer>(x, y);
    }

    private void parseMatchDecision(String prompt, List<AvailableMatch> joinables, List<AvailableMatch> notJoinables) {
        String userIn;
        boolean requestSent = false;
        Integer splitIndex;

        while (!requestSent) {
            if (!joinables.isEmpty() || !notJoinables.isEmpty()) {
                this.printer.printMatchesLobby(joinables, notJoinables, 0);
            }
            userIn = this.askUser(prompt);
            splitIndex = userIn.indexOf(" ");
            if (splitIndex == -1) {
                // join
                requestSent = true;
                try {
                    Integer index = Integer.valueOf(userIn) - 1;
                    if (index >= 0 && index < joinables.size()) {
                        this.joinMatch(joinables.get(index).name());
                    } else {
                        prompt = "Not a valid index! Try again.";
                    }
                } catch (Exception e) {
                    prompt = "Not a number! Try again.";
                }
                this.joinMatch(userIn);
            } else {
                // create
                String matchName = userIn.substring(0, splitIndex);
                Integer maxPlayers;
                try {
                    maxPlayers = Integer.valueOf(userIn.substring(splitIndex + 1, userIn.length()));
                    requestSent = true;
                    this.createMatch(matchName, maxPlayers);
                } catch (NumberFormatException e) {
                    prompt = "Bad format: max players was not a number! Try again:";
                }
            }
        }
    }

    private void clearStdin() {
        try {
            System.in.read(new byte[System.in.available()]);
        } catch (IOException e) {
        }
    }

    private void parsePlayerControl() {
        ClientBoard board = this.clientBoards.get(this.username);
        ClientBoard currentPlayerBoard = this.clientBoards.get(this.currentPlayer);
        String userIn, player;

        userIn = scanner.nextLine();
        this.printer.clearTerminal();
        switch (userIn) {
            case "o":
                this.printer.printObjectives(username, board.getColor(), board.getObjective(), this.visibleObjectives);
                break;
            case "h":
                this.printer.printHand(this.username, board.getColor(), board.getHand());
                break;
            case "b":
                this.printer.printPlayerBoard(this.username, board);
                break;

            case "1":
                player = this.players.get(0);
                this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                break;
            case "2":
                player = this.players.get(1);
                this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                break;
            case "3":
                if (this.players.size() > 2) {
                    player = this.players.get(2);
                    this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                    break;
                }
            case "4":
                if (this.players.size() > 3) {
                    player = this.players.get(3);
                    this.printer.printPlayerBoard(player, this.clientBoards.get(player));
                    break;
                }

            default:
                this.printer.printPlayerBoard(this.currentPlayer, currentPlayerBoard);
                break;
        }
        this.printer.printPrompt("Prova o");
    }

    // not working
    private void startPlayerControls() {
        while (this.ongoing) {
            synchronized (this.playerControls) {
                if (this.playerControls.isEnabled()) {
                    try {
                        if (System.in.available() > 0) {
                            this.parsePlayerControl();
                        } else {
                            Thread.sleep(200);
                        }
                    } catch (InterruptedException | IOException e) {
                    }
                } else {
                    try {
                        this.playerControls.wait();
                    } catch (InterruptedException e) {
                    }
                }

            }
        }
    }

    private void givePlayerControls() {
        synchronized (this.playerControls) {
            this.playerControls.enable();
            this.printer.printPrompt("Prova o");
            this.playerControls.notifyAll();
        }
    }

    private void removePlayerControls() {
        this.playerControls.disable();
        this.clearStdin();
    }

    ////////////////////////
    // PRE MATCH METHODS //
    ///////////////////////
    private void setNetwork() {
        String userIn, IPAddr;
        Integer port = null;

        IPAddr = this.askUser("Choose IP address:");
        userIn = this.askUser("Choose port:");
        while (port == null) {
            try {
                port = Integer.valueOf(userIn);
            } catch (NumberFormatException e) {
                userIn = this.askUser("Not a number. Choose a port:");
            }
        }

        String prompt = "Choose connection type (1 for TCP, 2 for RMI)";

        this.networkView = null;
        while (this.networkView == null) {
            userIn = this.askUser(prompt);
            try {
                switch (userIn) {
                    case "1", "tcp", "TCP":
                        this.setNetworkInterface(new NetworkViewTCP(this, IPAddr, port));
                        break;
                    case "2", "rmi", "RMI":
                        this.setNetworkInterface(new NetworkViewRMI(this, IPAddr, port));
                        break;
                    default:
                        prompt = "Not a valid connection type! Choose connection type (1 for TCP, 2 for RMI)";
                        break;
                }
            } catch (Exception e) {
                this.printer.clearTerminal();
                this.printer.printMessage("Could not connect! Try again");
                this.setNetwork();
            }
        }
    }


    // FIXME: error handling causes getAvailableMatches to block. Name never gets changed on Server??
    private void setMatch() {
        String prompt;
        List<AvailableMatch> joinables = new ArrayList<>(), notJoinables = new ArrayList<>();
        this.setUsername(this.askUser("Choose username:"));

        this.lastRequest.setStatus(RequestStatus.PENDING);
        this.networkView.getAvailableMatches();

        if (!this.getServerResponse()) {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage("Could not receive availbale matches, try again!", 1);
            this.setMatch();
            return;
        } else {
            this.printer.clearTerminal();
        }

        this.printer.clearTerminal();
        if (this.availableMatches.size() == 0) {
            prompt = "No matches available. Create one by typing match name and max players (e.g. MatchTest 2):";
        } else {
            this.availableMatches.forEach(match -> {
                if (match.currentPlayers() < match.maxPlayers()) {
                    joinables.add(match);
                } else {
                    notJoinables.add(match);
                }
            });
            prompt = "Join a match by typing its name, or create one by typing its name and max players (e.g. MatchTest 2)";
        }
        this.parseMatchDecision(prompt, joinables, notJoinables);

        if (!this.getServerResponse()) {
            this.printer.clearTerminal();
            this.printer.printCenteredMessage("Something went wrong.. Try again!", 1);
            this.setMatch();
        } else {
            this.printer.clearTerminal();
        }
    }

    @Override
    public void someoneJoined(String someoneUsername, List<String> joinedPlayers) {
        super.someoneJoined(someoneUsername, joinedPlayers);
        this.printer.clearTerminal();
        if (!this.username.equals(someoneUsername)) {
            this.printer.printCenteredMessage(someoneUsername + " joined the match!", 1);
        } else {
            this.printer.printCenteredMessage("Joined match!", 0);
            joinedPlayers.add("Joined players:");
            this.printer.printMessage(joinedPlayers);
            this.printer.printPrompt("");
        }
    }

    ///////////////////
    // MATCH METHODS //
    ///////////////////
    @Override
    public void giveInitialCard(InitialCard initialCard) {
        super.giveInitialCard(initialCard);
        this.printer.clearTerminal();
        this.printer.printInitialSideBySide(initialCard, 1);
        String userIn = this.askUser("Choose side for initial card (b for back, defaults to front):");
        Side side;
        switch (userIn) {
            case "b":
                side = Side.BACK;
                break;
            default:
                side = Side.FRONT;
                break;
        }

        super.chooseInitialCardSide(side);
        if (!this.getServerResponse()) {
            this.giveInitialCard(initialCard);
        } else {
            this.printer.clearTerminal();
        }
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side, Map<Symbol, Integer> availableResources) {
        this.printer.clearTerminal();
        if (this.username.equals(someoneUsername)) {
            this.printer.printPlayerBoard(this.username, this.clientBoards.get(this.username));
            this.printer.printMessage("Correctly played card! waiting for others to choose theirs");
        }
        super.someoneSetInitialSide(someoneUsername, side, availableResources);
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        super.giveSecretObjectives(secretObjectives);
        this.printer.clearTerminal();
        this.printer.printObjectivePair("Your choices:", secretObjectives, 1);
        String userIn = this.askUser("Choose secret objective (2 for second, defaults to first)");
        Objective objective;
        switch (userIn) {
            case "2":
                objective = secretObjectives.second();
                break;
            default:
                objective = secretObjectives.first();
                break;
        }

        super.chooseSecretObjective(objective);
        if (!this.getServerResponse()) {
            this.giveSecretObjectives(secretObjectives);
            return;
        }
    }


    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        super.someoneChoseSecretObjective(someoneUsername);
        this.playersWithObjective.add(someoneUsername);

        // if (this.playersWithObjective.size() == this.players.size() &&
        // !this.username.equals(this.currentPlayer)) {
        // this.printer.clearTerminal();
        // this.givePlayerControls();
        // }
    }


    // gets called only on others, never on current player
    @Override
    public void changePlayer() {
        this.printer.clearTerminal();
        ClientBoard board = this.clientBoards.get(this.currentPlayer);

        new Thread(() -> {
            if (board.getPlaced().isEmpty()) { // choosing initial side
                this.printer.printCenteredMessage(this.currentPlayer + " is choosing initial side!", 0);
            } else if (!this.playersWithObjective.contains(this.currentPlayer)) { // choosing objective
                this.printer.printCenteredMessage(this.currentPlayer + " is choosing secret objective!", 0);
            } else {
                if (!this.playerControls.isEnabled()) {
                    this.givePlayerControls();
                }
            }
        }).start();
    }


    // TO BE CHECKED: does the last turn message appear?
    @Override
    public void makeMove() {
        this.removePlayerControls();

        List<String> messages = new ArrayList<>();
        if (this.lastRequest.getStatus().equals(RequestStatus.FAILED)) {
            messages.add(lastError + " Try again.");
        }
        if (this.lastTurn) {
            messages.add("This is the last turn! Play carefully");
        }
        if (!messages.isEmpty()) {
            this.printer.printMessage(messages);
        }

        ClientBoard board = this.clientBoards.get(this.username);
        PlayableCard card = this.chooseCardFromHand(board);
        Side side = this.chooseCardSide(card);
        Pair<Integer, Integer> coords = this.chooseCoords(board);

        this.printer.clearTerminal();
        this.printer.printPlayerBoard(this.username, board);
        this.printer.printCard(new ShownCard(card, side, coords));
        String userIn = this.askUser("Are you sure? (n to abort)");
        if (userIn.equals("n")) {
            this.makeMove();
            return;
        }

        super.playCard(coords, card, side);
        if (!this.getServerResponse()) {
            this.makeMove();
        } else {
            this.printer.clearTerminal();
        }
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points,
            Map<Symbol, Integer> availableResources) {
        super.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);

        if (this.username.equals(someoneUsername)) {
            this.printer.clearTerminal();
            DrawSource source = null;
            this.printer.printAvailableResources(availableResources, 0);
            String userIn, prompt = "Choose a draw source: ";
            while (source == null) {
                this.printer.printDrawingScreen(decksTopReign, visiblePlayableCards);
                userIn = askUser(prompt);
                switch (userIn) {
                    case "G", "g":
                        source = DrawSource.GOLDS_DECK;
                        break;
                    case "R", "r":
                        source = DrawSource.RESOURCES_DECK;
                        break;
                    case "1":
                        source = DrawSource.FIRST_VISIBLE;
                        break;
                    case "2":
                        source = DrawSource.SECOND_VISIBLE;
                        break;
                    case "3":
                        source = DrawSource.THIRD_VISIBLE;
                        break;
                    case "4":
                        source = DrawSource.FOURTH_VISIBLE;
                        break;
                    default:
                        prompt = "Not a valid source! Try again.";
                        break;
                }
            }

            super.drawCard(source);
            if (!getServerResponse()) {
                this.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
                return;
            }
        }
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        this.printer.printCenteredMessage(someoneUsername + " quit!", 0);
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        this.printer.clearTerminal();
        ranking.forEach(entry -> {
            if (this.username.equals(entry.username())) {
                this.printer.printEndScreen(entry.winner());
            }
        });
        this.ongoing = false;
    }

    @Override
    public void notifyError(Exception exception) {
        super.notifyError(exception);
        this.lastError = exception.getMessage();
    }

    @Override
    protected void notifyMatchStarted() {}


    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        super.someoneSentPrivateText(someoneUsername, text);

        if (this.username.equals(someoneUsername)) {
            this.chat.add("(me): " + text);
        } else {
            this.chat.add("(" + someoneUsername + "): " + text);
        }
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        super.someoneSentBroadcastText(someoneUsername, text);

        if (this.username.equals(someoneUsername)) {
            this.chat.add("[me]: " + text);
        } else {
            this.chat.add("[" + someoneUsername + "]: " + text);
        }
    }


    public static void main(String[] args) {
        GraphicalViewTUI tui = new GraphicalViewTUI();
        tui.startInterface();
        while (tui.ongoing) {

        }
    }

}
