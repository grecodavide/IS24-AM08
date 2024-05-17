package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalViewInterface;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;

/**
 * TuiGraphicalView
 */

public class TuiGraphicalView extends GraphicalViewInterface {
    private TuiPrinter printer; // init this, call getPlaced() and pass it to printer with foreach in someonePlayedCard to test
    private boolean isConnected;
    private List<String> chat; // when someoneSentBroadcast/PrivateText, add to this. Then simply show when "chat" command is sent
    private String username;
    private final Scanner scanner;

    public TuiGraphicalView(NetworkView networkView, String username) throws IOException {
        super(networkView);
        this.printer = new TuiPrinter();
        this.isConnected = true;
        this.username = username;
        this.chat = new ArrayList<>();
        this.scanner = new Scanner(System.in);
    }

    // --------------- //
    // PRIVATE METHODS //
    // --------------- //

    // extracts the username passed as string, form an instruction
    private String getPassedUsername(String instruction, Integer startIndex) {
        if (startIndex == instruction.length()) {
            return this.username;
        } else {
            String arg = instruction.substring(startIndex + 1);
            // if the next element is a number, use it. Else directly search username
            try {
                return this.players.get(Integer.valueOf(arg) - 1);
            } catch (NumberFormatException e) {
                return arg;
            }
        }
    }

    // TO BE DELETED
    private void parseInstruction(String line) {
        String instruction;
        String user;
        Integer argStartIndex;
        ClientBoard b;
        argStartIndex = line.indexOf(" ");
        if (argStartIndex == -1) {
            argStartIndex = line.length();
        }
        instruction = line.substring(0, argStartIndex);
        switch (instruction) {
            case "quit", "q":
                this.printer.clearTerminal();
                this.isConnected = false;
                break;

            case "place", "p":
                break;

            case "list", "l":
                this.printer.printPlayerList(this.players);
                break;

            case "chat view", "cv":
                this.printer.printChat(chat);
                break;
            case "chat send", "cs":
                // TBA
                break;
            case "show", "s":
                user = getPassedUsername(line, argStartIndex);
                this.printer.printPlayerBoard(user, this.boards.get(user));
                break;
            case "hand", "h":
                user = getPassedUsername(line, argStartIndex);
                b = this.boards.get(user);
                this.printer.printHand(user, b.getColor(), b.getHand());
                break;
            case "help", "-h":
                this.printer.printHelp();
                break;
            case "objective", "o":
                b = this.boards.get(this.username);
                this.printer.printObjectives(this.username, b.getColor(), b.getObjective(), this.visibleObjectives);
                break;
            default:
                this.printer.clearTerminal();
                this.printer.printMessage("No such command. Type 'help' to show help (TBA)");
                break;
        }

    }

    // -------------- //
    // PUBLIC METHODS //
    // -------------- //

    @Override
    public void sendError(String text) {
        throw new UnsupportedOperationException("Unimplemented method 'sendError'");
    }

    // order by: execution flow

    
    /**
     * Asks the user (not necessarily a {@link Player}) some kind of data, expecting an answer from stdin
     * 
     * @param prompt The prompt to show in order to make the request
     * 
     * @returns the user's answer
     */
    public String askData(String prompt) {
        this.printer.clearTerminal();
        this.printPrompt(prompt);
        return this.scanner.nextLine();
    }


    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, Integer points,
            Map<Symbol, Integer> resources) {
        boards.get(someoneUsername).placeCard(coords, card, side, points, resources);
    }

    @Override
    public void cancelLastAction() {
        throw new UnsupportedOperationException("Unimplemented method 'cancelLastAction'");
    }

    /**
     * Clears the tui and changes to `false` the `isConnected` flag
     */
    public void quitGame(){
        this.printer.clearTerminal();
        this.isConnected = false;
    }

    /**
     * TBA
     */
    public void placeCard(){
        // TBA
    }

    /**
     * Calls the method that prints the list of players connected to the game
     */
    public void printPlayerList(){
        this.printer.printPlayerList(this.players);
    }

    /**
     * Calls the method that prints the board of a specific player
     * @param line string representing the command given by the user.
     *             It's used to extract the username of the desired player
     */
    public void printPlayerBoard(String line){
        Integer argStartIndex = line.indexOf(" ");
        String user = getPassedUsername(line, argStartIndex);

        this.printer.printPlayerBoard(user, this.boards.get(user));
    }

    /**
     * Calls the method that prints the hand-held cards of the player
     * @param line string representing the command given by the user.
     *             It's used to extract the username of the desired player
     *
     */
    public void printHand(String line){
        Integer argStartIndex = line.indexOf(" ");
        String user = getPassedUsername(line, argStartIndex);
        ClientBoard clientBoard = this.boards.get(user);

        this.printer.printHand(user, clientBoard.getColor(), clientBoard.getHand());
    }

    /**
     * Calls the method that prints the objectives (secret and common) of the playing user
     */
    public void printObjectives(){
        ClientBoard clientBoard = this.boards.get(this.username);

        this.printer.printObjectives(this.username, clientBoard.getColor(), clientBoard.getObjective(), this.visibleObjectives);
    }

    /**
     * Calls the method that prints the most recent chat messages
     */
    public void printChat(){
        this.printer.printChat(this.chat);
    }

    /**
     * Calls the method that prints all the available commands to the user
     */
    public void printHelp(){
        this.printer.printHelp();
    }

    /**
     * Calls the method that prints the prompt-bar
     * @param customMessage string representing the prompt-bar's brief indication
     */
    public void printPrompt(String customMessage){
        this.printer.printPrompt(customMessage);
    }

    /**
     * Infinite loop (until end of game) that acquires next instruction and executes it
     */
    public void start() {
        String line;
        this.printer.clearTerminal();
        while (this.isConnected) {
            printPrompt("Command");
            line = scanner.nextLine();
            this.printer.clearTerminal();
            this.parseInstruction(line); //deprecated
        }
        scanner.close();
    }

    // will start when someone tries to start a TUI client
    public static void main(String[] args) {
        // TuiGraphicalView tui = new TuiGraphicalView(networkView, username);
    }

}
