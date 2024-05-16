package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalViewInterface;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;

/**
 * TuiGraphicalView
 */

public class TuiGraphicalView extends GraphicalViewInterface {
    private TuiPrinter printer; // init this, call getPlaced() and pass it to printer with foreach in someonePlayedCard to test
    private boolean isConnected;
    private List<String> chat; // when someoneSentBroadcast/PrivateText, add to this. Then simply show when "chat" command is sent
    private final String username;

    public TuiGraphicalView(NetworkView networkView, String username) throws IOException {
        super(networkView);
        this.printer = new TuiPrinter();
        this.isConnected = true;
        this.username = username;
        this.chat = new ArrayList<>();
    }

    @Override
    public void sendError(String text) {
        throw new UnsupportedOperationException("Unimplemented method 'sendError'");
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


    /**
     * infinite loop (until end of game) that acquires next instruction and executes it
     */
    public void start() {
        Scanner scanner = new Scanner(System.in);
        String line;
        this.printer.clearTerminal();
        while (this.isConnected) {
            this.printer.printPrompt();
            line = scanner.nextLine();
            this.printer.clearTerminal();
            this.parseInstruction(line);
        }
        scanner.close();
    }

    // here for testing
    public static void main(String[] args) throws IOException {
        String player1 = "Uga";
        String player2 = "Buga";
        TuiGraphicalView view = new TuiGraphicalView(null, player1); // for now null, tba

        Integer[] player1Hand = {44, 55, 56};
        Integer[] player2Hand = {12, 21, 33};

        Integer[] visibleObjectives = {2, 4};
        Map<DrawSource, Integer> visibleCards = Map.of(DrawSource.FIRST_VISIBLE, 31, DrawSource.SECOND_VISIBLE, 79, DrawSource.THIRD_VISIBLE, 80, DrawSource.FOURTH_VISIBLE, 77);
        Symbol[] visibleReigns = {Symbol.FUNGUS, Symbol.PLANT};
        view.matchStarted(visibleObjectives, visibleCards, visibleReigns, Map.of(player1, player1Hand, player2, player2Hand), Map.of(player1, Color.RED, player2, Color.BLUE));

        ClientBoard player1Board = view.boards.get(player1);
        ClientBoard player2Board = view.boards.get(player2);

        CardsManager manager = CardsManager.getInstance();

        player1Board.setSecretObjective(11);

        player1Board.placeInitial(manager.getInitialCards().get(1), Side.FRONT);
        player1Board.placeCard(new Pair<>(1, 1), manager.getResourceCards().get(20), Side.FRONT, 0, Map.of());
        player1Board.placeCard(new Pair<>(1, -1), manager.getResourceCards().get(1), Side.FRONT, 0, Map.of());
        player1Board.placeCard(new Pair<>(2, 0), manager.getResourceCards().get(30), Side.FRONT, 0, Map.of());
        player1Board.placeCard(new Pair<>(-1, -1), manager.getResourceCards().get(14), Side.BACK, 0, Map.of());
        player1Board.placeCard(new Pair<>(3, -1), manager.getGoldCards().get(41), Side.FRONT, 2, Map.of(Symbol.PLANT, 3, Symbol.INSECT, 3,
                Symbol.ANIMAL, 1, Symbol.FUNGUS, 1, Symbol.PARCHMENT, 1, Symbol.FEATHER, 0, Symbol.INKWELL, 0));

        player2Board.setSecretObjective(12);

        player2Board.placeInitial(manager.getInitialCards().get(2), Side.FRONT);
        player2Board.placeCard(new Pair<>(-1, 1), manager.getResourceCards().get(20), Side.FRONT, 0, Map.of());
        player2Board.placeCard(new Pair<>(-1, -1), manager.getResourceCards().get(1), Side.FRONT, 0, Map.of());
        player2Board.placeCard(new Pair<>(-2, 0), manager.getResourceCards().get(29), Side.FRONT, 0, Map.of());
        player2Board.placeCard(new Pair<>(1, -1), manager.getResourceCards().get(14), Side.BACK, 0, Map.of());
        player2Board.placeCard(new Pair<>(-3, -1), manager.getGoldCards().get(46), Side.FRONT, 2, Map.of(Symbol.PLANT, 2, Symbol.INSECT, 2,
                Symbol.ANIMAL, 2, Symbol.FUNGUS, 1, Symbol.PARCHMENT, 1, Symbol.FEATHER, 1, Symbol.INKWELL, 0));
        
        for (int i = 1; i <= 100; i++)
            view.chat.add("This is the message number:   " + i);

        view.start();
    }

}
