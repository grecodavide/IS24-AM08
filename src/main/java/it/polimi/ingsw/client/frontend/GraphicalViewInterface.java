package it.polimi.ingsw.client.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

// we need networkView here to SEND information to server
public abstract class GraphicalViewInterface {
    protected final Map<String, ClientBoard> boards;
    protected final List<String> players;
    private final NetworkView networkView;
    protected Integer[] visibleObjectives;

    public GraphicalViewInterface(NetworkView networkView) {
        this.networkView = networkView;
        this.boards = new HashMap<>();
        this.players = new ArrayList<>();
    }

    public void matchStarted(Integer[] visibleObjectives, Map<DrawSource, Integer> visibleCards, Symbol[] visibleDeckReigns,
            Map<String, Integer[]> playerHands, Map<String, Color> playerPawnColors) {
        this.visibleObjectives = visibleObjectives;
        for (String username : playerHands.keySet()) {
            this.boards.put(username, new ClientBoard(username, playerPawnColors.get(username), playerHands.get(username)));
            this.players.add(username);
        }
    }

    /**
     * (Temporary demo method) Shows an error in the GUI
     *
     * @param text explaination of the error in natural language
     */
    public abstract void sendError(String text);

    /**
     * (Temporary demo method) Notifies the view that someone played a card
     *
     */
    public abstract void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side,
            Integer points, Map<Symbol, Integer> resources);

    /**
     * (Temporary demo method) Cancel last action by player
     */
    public abstract void cancelLastAction();

}
