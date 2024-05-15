package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    private TuiCardPrinter printer; // init this, call getPlaced() and pass it to printer with foreach in someonePlayedCard to test

    public TuiGraphicalView() throws IOException {
        this.boards = new HashMap<>();
        this.printer = new TuiCardPrinter();
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
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, Integer points, Map<Symbol, Integer> resources) {
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
            System.out.println("No such player exists!");
            return; // handle error
        }
        Map<Integer, ShownCard> placed = board.getPlaced();
        for (Integer turn : placed.keySet()) {
            try {
                printer.printCard(placed.get(turn));
            } catch (CardException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TuiGraphicalView view = new TuiGraphicalView();
        Integer[] hand = {1, 2, 3};
        view.boards.put("Davide", new ClientBoard(hand));
        ClientBoard board = view.boards.get("Davide");
        CardsManager manager = CardsManager.getInstance();

        board.placeInitial(manager.getInitialCards().get(1), Side.FRONT);
        board.placeCard(new Pair<>(1, 1), manager.getResourceCards().get(1), Side.FRONT, 0, Map.of());
        board.placeCard(new Pair<>(1, -1), manager.getResourceCards().get(20), Side.FRONT, 0, Map.of());
        board.placeCard(new Pair<>(2, 0), manager.getResourceCards().get(30), Side.FRONT, 0, Map.of());
        board.placeCard(new Pair<>(3, -1), manager.getGoldCards().get(78), Side.FRONT, 0, Map.of());

        view.printPlayerBoard("Davide");
        // view.printPlayerBoard("Luca");
    }
 
}
