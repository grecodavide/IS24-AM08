package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.net.Socket;
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
    private String username;
    private List<String> chat; // when someoneSentBroadcast/PrivateText, add to this. Then simply show when "chat" command is sent
    private final Scanner scanner;

    public TuiGraphicalView() throws IOException {
        this.printer = new TuiPrinter();
        this.isConnected = true;
        this.chat = new ArrayList<>();
        this.scanner = new Scanner(System.in);

        this.setNetworkInterface(this.setConnectionType());
        this.printer.clearTerminal();
        this.printer.printPrompt("Choose Username:");
        this.setUsername(scanner.nextLine());
        this.chooseMatch(); // TODO: if fail, ask again for username and chooseMatch
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

    private void setUsername(String username) {
        this.username = username;
        this.networkView.setUsername(username);
    }

    private boolean createMatch(String userIn) {
        String matchName = userIn.substring(0, userIn.indexOf(" "));
        if (matchName.length() == userIn.length()) {
            this.printer.printMessage("No max players specified");
        } else {
            Integer maxPlayers;
            try {
                maxPlayers = Integer.valueOf(userIn.substring(matchName.length() + 1)); // mhh
                this.networkView.createMatch(matchName, maxPlayers);
                return true;
            } catch (NumberFormatException ne) {
                this.printer.printMessage("Not a number for max players!");
            }
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

        while (!connectionSet) {
            this.printer.clearTerminal();
            this.printer.printPrompt("What connection type you want to establish?");
            userIn = this.scanner.nextLine();
            this.printer.clearTerminal();
            try {
                switch (userIn) {
                    case "1", "TCP", "tcp", "socket":
                        serverIP = this.askIPAddress();
                        this.printer.clearTerminal();
                        port = this.askPort();
                        networkView = new NetworkViewTCP(this, new Socket(serverIP, port));
                        connectionSet = true;
                        break;

                    case "2", "RMI", "rmi", "remote":
                        serverIP = this.askIPAddress();
                        port = this.askPort();
                        // networkView = ...
                        connectionSet = true;
                        break;

                    default:
                        this.printer.printMessage("Not a valid connection type!");
                        break;
                }

            } catch (IOException e) {
                // nothing to do here, loop over
            }
        }
        this.printer.clearTerminal();
        networkView.getAvailableMatches();

        return networkView;
    }

    private void chooseMatch() {
        this.printer.printMessage("Waiting for matches..");
        while (this.availableMatches == null) {
        }
        this.printer.clearTerminal();
        List<String> printableMatches = new ArrayList<>();
        this.availableMatches
                .forEach((match -> printableMatches.add(match.name() + "(" + match.currentPlayers() + "/" + match.maxPlayers() + ")")));

        String userIn;

        if (this.availableMatches.size() == 0) {
            this.printer.printMessage("No matches available. Create one by specifying its name and max number of players!");
            userIn = this.scanner.nextLine();
            this.printer.clearTerminal();
            this.createMatch(userIn);
        } else {
            this.printer.printMessage(printableMatches);
            this.printer.printPrompt("Specify a name to create a match, or a number to join one, followed by the desired max players.");
            userIn = this.scanner.nextLine();
            this.printer.clearTerminal();
            boolean shouldLoop = true;

            while (shouldLoop) {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveInitialCard'");
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        // this.printer.printPrompt("Choose secret objective:");
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        // this.printer.printPrompt(someoneUsername + " is choosing secret objectives");
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneDrewInitialCard'");
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneSetInitialSide'");
    }


    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneChoseSecretObjective'");
    }

    @Override
    public void notifyLastTurn() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'notifyLastTurn'");
    }

    @Override
    public void someoneJoined(String someoneUsername) {
        if (someoneUsername.equals(this.username)) {
            this.printer.printMessage("Correctly joined the match!");
        } else {
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
        this.printer.printPlayerBoard(this.currentPlayer, this.clientBoards.get(this.currentPlayer));
    }


    // will start when someone tries to start a TUI client
    public static void main(String[] args) throws Exception {
        TuiGraphicalView tui = new TuiGraphicalView();
        while (true) {
            // tmp: dont close immediately
        }
    }


}
