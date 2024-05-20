package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

/**
 * TuiGraphicalView
 */

public class TuiGraphicalView extends GraphicalView {
    private TuiPrinter printer; // init this, call getPlaced() and pass it to printer with foreach in someonePlayedCard to test
    private boolean isConnected;
    private boolean matchCreator;
    private boolean receivedError;

    private List<String> chat; // when someoneSentBroadcast/PrivateText, add to this. Then simply show when "chat" command is sent
    private final Scanner scanner;

    public TuiGraphicalView() throws IOException {
        this.printer = new TuiPrinter();
        this.isConnected = false;
        this.receivedError = false;
        this.matchCreator = false;
        this.chat = new ArrayList<>();
        this.scanner = new Scanner(System.in);

        this.setNetworkInterface(this.setConnectionType());
        this.printer.clearTerminal();
        this.printer.printPrompt("Choose Username:");
        this.setUsername(scanner.nextLine());

        this.chooseMatch("Type a number to join a match, or a name followed by the max players to create one");
        // TODO: error handling
        this.printer.printWelcomeScreen();
    }


    ///////////////////////
    // AUXILIARY METHODS //
    ///////////////////////
    private String askIPAddress() {
        this.printer.printPrompt("Specify an IP address:");
        return this.scanner.nextLine();
    }

    private Integer askPort() {
        this.printer.printPrompt("Specify a Port:");
        String in;
        Integer port;
        while (true) {
            in = this.scanner.nextLine();
            try {
                port = Integer.valueOf(in);
                return port;
            } catch (Exception e) {
                this.printer.printMessage("Not a valid port! must be a number");
            }
        }
    }


    private boolean createMatch(String userIn) {
        this.matchCreator = true;
        try {
            String matchName = userIn.substring(0, userIn.indexOf(" "));
            Integer maxPlayers = Integer.valueOf(userIn.substring(matchName.length() + 1)); // mhh
            this.networkView.createMatch(matchName, maxPlayers);
            return true;
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            this.printer.printMessage("No max players specified!");
        }

        return false;
    }

    private NetworkView setConnectionType() {
        String userIn;
        this.printer.clearTerminal();
        boolean connectionSet = false;
        String serverIP;
        Integer port;
        NetworkView networkView = null;

        this.printer.clearTerminal();
        this.printer.printPrompt("What connection type you want to establish?");
        while (!connectionSet) {
            userIn = this.askInput();
            this.printer.clearTerminal();
            switch (userIn) {
                case "1", "TCP", "tcp", "socket":
                    serverIP = this.askIPAddress();
                    this.printer.clearTerminal();
                    port = this.askPort();
                    networkView = new NetworkViewTCP(this, serverIP, port);
                    connectionSet = true;
                    break;

                case "2", "RMI", "rmi", "remote":
                    serverIP = this.askIPAddress();
                    this.printer.clearTerminal();
                    port = this.askPort();
                    // networkView = new NetworkViewRMI(this, new PlayerControllerRMI(this.username, match));
                    connectionSet = true;
                    break;

                default:
                    this.printer.printPrompt("Not a valid connection type! Specify a connection type.");
                    break;
            }

        }
        this.printer.clearTerminal();
        networkView.getAvailableMatches();

        return networkView;
    }

    private void chooseMatch(String prompt) {
        this.printer.printMessage("Waiting for matches..");
        while (this.availableMatches == null);

        this.printer.clearTerminal();
        List<String> printableMatches = new ArrayList<>();
        this.availableMatches.forEach(match -> {
            String joinable;
            if (match.currentPlayers() < match.maxPlayers())
                joinable = "JOINABLE";
            else
                joinable = "NOT JOINABLE";

            printableMatches.add(match.name() + "(" + match.currentPlayers() + "/" + match.maxPlayers() + ": " + joinable + ")");
        });

        String userIn;
        boolean shouldLoop = true;

        if (this.availableMatches.size() == 0) {
            while (shouldLoop) {
                prompt = "No matches available. Create one by specifying its name and max number of players!";
                this.printer.printMessage(prompt);
                userIn = this.askInput();
                shouldLoop = !this.createMatch(userIn);
            }
        } else {
            while (shouldLoop) {
                this.printer.printMessage(printableMatches);
                this.printer.printPrompt(prompt);
                userIn = this.askInput();

                try {
                    Integer matchToJoin = Integer.valueOf(userIn) - 1;
                    this.networkView.joinMatch(this.availableMatches.get(matchToJoin).name());
                    shouldLoop = false;
                } catch (NumberFormatException e) {
                    this.createMatch(userIn);
                    shouldLoop = false;
                }
            }
        }
    }

    private String askInput() {
        try {
            System.in.read(new byte[System.in.available()]);
        } catch (Exception e) {
        }
        String userIn = this.scanner.nextLine();
        this.printer.clearTerminal();
        return userIn;
    }

    ///////////////
    // Game flow //
    ///////////////
    @Override
    public void changePlayer() {
        this.printer.printPlayerBoard(this.currentPlayer, this.clientBoards.get(this.currentPlayer));
    }

    @Override
    public void giveLobbyInfo(List<String> playersUsernames) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveLobbyInfo'");
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {
        super.giveInitialCard(initialCard);
        this.printer.clearTerminal();
        this.printer.printInitialSideBySide(initialCard, 1);
        this.printer.printPrompt("Choose initial card (b for back, front otherwise):");
        String userIn = this.askInput();
        Side side;
        if (userIn.equals("b")) {
            side = Side.BACK;
        } else {
            side = Side.FRONT;
        }

        this.networkView.chooseInitialCardSide(side);
    }

    // TODO: same objective ?????????
    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        this.printer.clearTerminal();
        this.printer.printObjectivePair("Possible secret objectives:", visibleObjectives, 1);
        this.printer.printPrompt("Choose secret objective (1 for first, second otherwise):");
        String userIn = this.askInput();
        Objective objective;
        if (userIn.equals("1")) {
            objective = secretObjectives.first();
        } else {
            objective = secretObjectives.second();
        }

        this.clientBoards.get(this.username).setSecretObjective(objective);
        this.networkView.chooseSecretObjective(objective);
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        this.printer.printPrompt(someoneUsername + " is choosing secret objectives");
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        this.printer.printPrompt(someoneUsername + " is drawing initial card");
        super.someoneDrewInitialCard(someoneUsername, card);
    }


    @Override
    public void notifyLastTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyLastTurn'");
    }

    @Override
    public void someoneJoined(String someoneUsername) {
        if (someoneUsername.equals(this.username)) {
            this.isConnected = true;
        } else {
            this.printer.clearTerminal();
            this.printer.printWelcomeScreen();
            this.printer.printMessage(someoneUsername + " joined the match!");
        }
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneQuit'");
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'matchFinished'");
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneSentBroadcastText'");
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneSentPrivateText'");
    }

    @Override
    protected void notifyMatchStarted() {
        // this.printer.printPlayerBoard(this.currentPlayer, this.clientBoards.get(this.currentPlayer));
    }

    @Override
    public void showError(String cause, Exception exception) {
        this.receivedError = true;
        this.printer.printMessage("ERROR: " + cause + " " + exception.getClass());
    }

    @Override
    public void makeMove() {
        // this.networkView.playCard(coords, card, side);
    }

    // will start when someone tries to start a TUI client
    public static void main(String[] args) throws Exception {
        TuiGraphicalView tui = new TuiGraphicalView();
        while (true) {
            // tmp: dont close immediately
        }
    }
}
