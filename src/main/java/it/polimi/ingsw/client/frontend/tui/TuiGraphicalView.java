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
    }

    private void setUsername(String username) {
        this.username = username;
        this.networkView.setUsername(username);
    }

    // --------------- //
    // PRIVATE METHODS //
    // --------------- //

    // // extracts the username passed as string, form an instruction
    // private String getInstructionTarget(String instruction, Integer startIndex) {
    //     if (startIndex == instruction.length()) {
    //         return this.username;
    //     } else {
    //         String arg = instruction.substring(startIndex + 1);
    //         // if the next element is a number, use it. Else directly search username
    //         try {
    //             return this.players.get(Integer.valueOf(arg) - 1);
    //         } catch (NumberFormatException e) {
    //             return arg;
    //         }
    //     }
    // }

    // // TO BE DELETED
    // private void parseInstruction(String line) {
    //     String instruction;
    //     String user;
    //     Integer argStartIndex;
    //     ClientBoard b;
    //     argStartIndex = line.indexOf(" ");
    //     if (argStartIndex == -1) {
    //         argStartIndex = line.length();
    //     }
    //     instruction = line.substring(0, argStartIndex);
    //     switch (instruction) {
    //         case "quit", "q":
    //             this.printer.clearTerminal();
    //             this.isConnected = false;
    //             break;

    //         case "place", "p":
    //             break;

    //         case "list", "l":
    //             this.printer.printMessage(this.players);
    //             break;

    //         case "chat view", "cv":
    //             this.printer.printChat(chat);
    //             break;
    //         case "chat send", "cs":
    //             // TBA
    //             break;
    //         case "show", "s":
    //             user = getInstructionTarget(line, argStartIndex);
    //             this.printer.printPlayerBoard(user, this.clientBoards.get(user));
    //             break;
    //         case "hand", "h":
    //             user = getInstructionTarget(line, argStartIndex);
    //             b = this.clientBoards.get(user);
    //             this.printer.printHand(user, b.getColor(), b.getHand());
    //             break;
    //         case "help", "-h":
    //             this.printer.printHelp();
    //             break;
    //         case "objective", "o":
    //             b = this.clientBoards.get(this.username);
    //             this.printer.printObjectives(this.username, b.getColor(), b.getObjective(), this.visibleObjectives);
    //             break;
    //         default:
    //             this.printer.clearTerminal();
    //             this.printer.printMessage("No such command. Type 'help' to show help (TBA)");
    //             break;
    //     }

    // }


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

    // -------------- //
    // PUBLIC METHODS //
    // -------------- //

    // order by: execution flow
    public NetworkView setConnectionType() {
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

    public void chooseMatch() {
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
                }

            }
        }
    }



    // /**
    //  * Clears the tui and changes to `false` the `isConnected` flag
    //  */
    // public void quitGame() {
    //     this.printer.clearTerminal();
    //     this.isConnected = false;
    // }

    // /**
    //  * TBA
    //  */
    // public void placeCard() {
    //     // TBA
    // }

    // /**
    //  * Calls the method that prints the list of players connected to the game
    //  */
    // public void printPlayerList() {
    //     this.printer.printMessage(this.players);
    // }

    // /**
    //  * Calls the method that prints the board of a specific player
    //  * 
    //  * @param line string representing the command given by the user. It's used to extract the username
    //  *        of the desired player
    //  */
    // public void printPlayerBoard(String line) {
    //     Integer argStartIndex = line.indexOf(" ");
    //     String user = getInstructionTarget(line, argStartIndex);

    //     this.printer.printPlayerBoard(user, this.clientBoards.get(user));
    // }

    // /**
    //  * Calls the method that prints the hand-held cards of the player
    //  * 
    //  * @param line string representing the command given by the user. It's used to extract the username
    //  *        of the desired player
    //  *
    //  */
    // public void printHand(String line) {
    //     Integer argStartIndex = line.indexOf(" ");
    //     String user = getInstructionTarget(line, argStartIndex);
    //     ClientBoard clientBoard = this.clientBoards.get(user);

    //     this.printer.printHand(user, clientBoard.getColor(), clientBoard.getHand());
    // }

    // /**
    //  * Calls the method that prints the objectives (secret and common) of the playing user
    //  */
    // public void printObjectives() {
    //     ClientBoard clientBoard = this.clientBoards.get(this.username);

    //     this.printer.printObjectives(this.username, clientBoard.getColor(), clientBoard.getObjective(), this.visibleObjectives);
    // }

    // /**
    //  * Calls the method that prints the most recent chat messages
    //  */
    // public void printChat() {
    //     this.printer.printChat(this.chat);
    // }

    // /**
    //  * Calls the method that prints all the available commands to the user
    //  */
    // public void printHelp() {
    //     this.printer.printHelp();
    // }

    // /**
    //  * Calls the method that prints the prompt-bar
    //  * 
    //  * @param customMessage string representing the prompt-bar's brief indication
    //  */
    // public void printPrompt(String customMessage) {
    //     this.printer.printPrompt(customMessage);
    // }

    // /**
    //  * Calls the method that prints the welcome screen in the middle of the tui view
    //  */
    // public void printWelcomeScreen() {
    //     this.printer.printWelcomeScreen();
    // }

    // /**
    //  * Infinite loop (until end of game) that acquires next instruction and executes it
    //  */
    // public void start() {
    //     String line;
    //     this.printer.clearTerminal();
    //     while (this.isConnected) {
    //         printPrompt("Command:");
    //         line = scanner.nextLine();
    //         this.printer.clearTerminal();
    //         this.parseInstruction(line); // deprecated
    //     }
    //     scanner.close();
    // }

    @Override
    public void changePlayer() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changePlayer'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'giveSecretObjectives'");
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
    public void someoneDrewSecretObjective(String someoneUsername) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'someoneDrewSecretObjective'");
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
        Scanner scanner = new Scanner(System.in);
        tui.setNetworkInterface(tui.setConnectionType());
        tui.printer.clearTerminal();
        tui.printer.clearTerminal();
        tui.printer.printPrompt("Username: ");
        tui.setUsername(scanner.nextLine());
        tui.chooseMatch();
    }


}
