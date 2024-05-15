package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.GraphicalViewInterface;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.exceptions.CardException;
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
    private TuiPrinter printer; // init this, call getPlaced() and pass it to printer with foreach in someonePlayedCard to test
    private boolean isConnected;

    public TuiGraphicalView() throws IOException {
        this.boards = new HashMap<>();
        this.printer = new TuiPrinter();
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

    public void printPlayerBoard(String username) {
        ClientBoard board = boards.get(username);
        this.printer.clearTerminal();
        if (board == null) {
            this.printer.printMessage("No such player exists!");
            return;
        }
        Map<Integer, ShownCard> placed = board.getPlaced();
        for (Integer turn : placed.keySet()) {
            try {
                printer.printCard(placed.get(turn));
            } catch (CardException e) {
                e.printStackTrace();
            }
        }
        this.printer.printResources(board.getResources());
        this.printer.printPoints(board.getPoints());
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
            case "q":
                this.printer.clearTerminal();
                this.isConnected = false;
                break;

            case "place":
                break;

            case "show":
                if (end == line.length()) {
                    this.printer.printMessage("No name specified, skipping this");
                } else {
                    this.printPlayerBoard(line.substring(end + 1));
                }
                break;
            case "h":
                this.printer.printMessage("Told you it was TBA");
                break;
            default:
                this.printer.clearTerminal();
                this.printer.printMessage("No such command. Type 'h' to show help (TBA)");
                break;
        }

    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        String line;
        while (this.isConnected) {
            this.printer.printPrompt();
            line = scanner.nextLine();
            this.printer.clearTerminal();
            this.parseInstruction(line);
        }
        scanner.close();
    }

    public static void main(String[] args) throws IOException {
        TuiGraphicalView view = new TuiGraphicalView();
        Integer[] hand = {1, 2, 3};
        view.boards.put("Davide", new ClientBoard(hand));
        ClientBoard board = view.boards.get("Davide");
        CardsManager manager = CardsManager.getInstance();

        board.placeInitial(manager.getInitialCards().get(1), Side.FRONT);
        board.placeCard(new Pair<>(1, 1), manager.getResourceCards().get(20), Side.FRONT, 0, Map.of());
        board.placeCard(new Pair<>(1, -1), manager.getResourceCards().get(1), Side.FRONT, 0, Map.of());
        board.placeCard(new Pair<>(2, 0), manager.getResourceCards().get(30), Side.FRONT, 0, Map.of());
        board.placeCard(new Pair<>(3, -1), manager.getGoldCards().get(41), Side.FRONT, 2, Map.of(Symbol.PLANT, 2, Symbol.INSECT, 3,
                Symbol.ANIMAL, 1, Symbol.FUNGUS, 1, Symbol.PARCHMENT, 1, Symbol.FEATHER, 0, Symbol.INKWELL, 0));

        view.start();
        // view.printPlayerBoard("Luca");
    }

}
