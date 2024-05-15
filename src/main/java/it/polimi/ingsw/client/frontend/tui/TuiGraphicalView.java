package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.*;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalViewInterface;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;

/**
 * TuiGraphicalView
 */

public class TuiGraphicalView implements GraphicalViewInterface {
    private Map<String, ClientBoard> boards;
    private List<String> players;
    private TuiPrinter printer; // init this, call getPlaced() and pass it to printer with foreach in someonePlayedCard to test
    private boolean isConnected;

    public TuiGraphicalView() throws IOException {
        this.boards = new HashMap<>();
        this.printer = new TuiPrinter();
        this.players = new ArrayList<>();
        this.isConnected = true;
    }

    @Override
    public void setNetworkInterface(NetworkView networkView) {
        throw new UnsupportedOperationException("Unimplemented method 'setNetworkInterface'");
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


    private void parseInstruction(String line) {
        String instruction;
        Integer end;
        end = line.indexOf(" ");
        if (end == -1) {
            end = line.length();
        }
        instruction = line.substring(0, end);
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

            case "show", "s":
                if (end == line.length()) {
                    this.printer.printMessage("No name specified, skipping this");
                } else {
                    String user = line.substring(end + 1);

                    // if the next element is a number, use it. Else directly search username
                    try {
                        String username = this.players.get(Integer.valueOf(user) - 1);
                        this.printer.printPlayerBoard(username, this.boards.get(username));
                    } catch (NumberFormatException e) {
                        this.printer.printPlayerBoard(user, this.boards.get(user));
                    }
                }
                break;
            case "help", "h":
                this.printer.printMessage("Told you it was TBA");
                break;
            default:
                this.printer.clearTerminal();
                this.printer.printMessage("No such command. Type 'h' to show help (TBA)");
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
        TuiGraphicalView view = new TuiGraphicalView();
        Integer[] hand = {1, 2, 3};

        String player1 = "Uga";
        view.boards.put(player1, new ClientBoard(hand));
        view.players.add(player1);
        ClientBoard player1Board = view.boards.get(player1);

        String player2 = "Buga";
        view.boards.put(player2, new ClientBoard(hand));
        view.players.add(player2);
        ClientBoard player2Board = view.boards.get(player2);

        CardsManager manager = CardsManager.getInstance();

        player1Board.placeInitial(manager.getInitialCards().get(1), Side.FRONT);
        player1Board.placeCard(new Pair<>(1, 1), manager.getResourceCards().get(20), Side.FRONT, 0, Map.of());
        player1Board.placeCard(new Pair<>(1, -1), manager.getResourceCards().get(1), Side.FRONT, 0, Map.of());
        player1Board.placeCard(new Pair<>(2, 0), manager.getResourceCards().get(30), Side.FRONT, 0, Map.of());
        player1Board.placeCard(new Pair<>(-1, -1), manager.getResourceCards().get(14), Side.BACK, 0, Map.of());
        player1Board.placeCard(new Pair<>(3, -1), manager.getGoldCards().get(41), Side.FRONT, 2, Map.of(Symbol.PLANT, 3, Symbol.INSECT, 3,
                Symbol.ANIMAL, 1, Symbol.FUNGUS, 1, Symbol.PARCHMENT, 1, Symbol.FEATHER, 0, Symbol.INKWELL, 0));


        player2Board.placeInitial(manager.getInitialCards().get(2), Side.FRONT);
        player2Board.placeCard(new Pair<>(-1, 1), manager.getResourceCards().get(20), Side.FRONT, 0, Map.of());
        player2Board.placeCard(new Pair<>(-1, -1), manager.getResourceCards().get(1), Side.FRONT, 0, Map.of());
        player2Board.placeCard(new Pair<>(-2, 0), manager.getResourceCards().get(29), Side.FRONT, 0, Map.of());
        player2Board.placeCard(new Pair<>(1, -1), manager.getResourceCards().get(14), Side.BACK, 0, Map.of());
        player2Board.placeCard(new Pair<>(-3, -1), manager.getGoldCards().get(46), Side.FRONT, 2, Map.of(Symbol.PLANT, 2, Symbol.INSECT, 2,
                Symbol.ANIMAL, 2, Symbol.FUNGUS, 1, Symbol.PARCHMENT, 1, Symbol.FEATHER, 1, Symbol.INKWELL, 0));


        view.start();
    }

}
