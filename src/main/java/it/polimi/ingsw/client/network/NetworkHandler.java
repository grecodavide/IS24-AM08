package it.polimi.ingsw.client.network;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

public abstract class NetworkHandler implements RemoteViewInterface {
    protected GraphicalView graphicalView;
    protected String username;
    protected String ipAddress;
    protected int port;
    protected boolean connected = false;

    /**
     * Initialize the instance all its internal attributes.
     *
     * @param graphicalView The GraphicalView to be subscribed to this NetworkHandler instance
     * @param ipAddress     The server IP address
     * @param port          The server port
     */
    public NetworkHandler(GraphicalView graphicalView, String ipAddress, int port) {
        this.graphicalView = graphicalView;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    /**
     * Periodically check the connection status
     */
    public void startConnectionCheck() {
        // we create a thread pool of 1 thread
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable pingServer = () -> {
            if (!connected) {
                // If the connection lost is already acknowledged
                // shutdown the executor
                executor.shutdown();
            } else {
                if (!ping()) {
                    // If there is a connection error
                    // notify the client and shutdown the executor
                    disconnect();
                    graphicalView.notifyConnectionLost();
                    executor.shutdown();
                }
            }
        };

        // and we check every two second for connectivity
        executor.scheduleAtFixedRate(pingServer, 0, 2, TimeUnit.SECONDS);
    }

    /**
     * Sets the player's username.
     *
     * @param username The player's username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Asks the server to send a list of {@link AvailableMatch}
     */
    public abstract void getAvailableMatches();

    
    /**
     * This method will check connectivity
     */
    public abstract boolean ping();

    // Action Methods

    /**
     * Asks to create a match.
     *
     * @param matchName  The match name
     * @param maxPlayers The match maximum number of players
     */
    public abstract void createMatch(String matchName, Integer maxPlayers);

    /**
     * Asks to join a match.
     *
     * @param matchName the match's name
     */
    public abstract void joinMatch(String matchName);

    /**
     * Draws an initial card for the player.
     */
    public abstract void drawInitialCard();

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     */
    public abstract void chooseInitialCardSide(Side side);

    /**
     * Draws two secret objectives.
     */
    public abstract void drawSecretObjectives();

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     */
    public abstract void chooseSecretObjective(Objective objective);

    /**
     * Plays a card.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     */
    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side);

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     */
    public abstract void drawCard(DrawSource source);

    /**
     * If the connection is still open
     * @return if it is connected
     */
    public boolean isConnected() {
        return connected;
    }
    public void receiveAvailableMatches(List<AvailableMatch> availableMatches) {
        graphicalView.receiveAvailableMatches(availableMatches);
    }

    @Override
    public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
                             Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
                             Pair<Symbol, Symbol> decksTopReigns) {
        graphicalView.matchStarted(playersUsernamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards, decksTopReigns);
    }

    @Override
    public void matchResumed(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
                             Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
                             Pair<Symbol, Symbol> decksTopReigns, Objective secretObjective, Map<String, Map<Symbol, Integer>> availableResources, Map<String, Map<Pair<Integer, Integer>, PlacedCard>> placedCards, Map<String, Integer> playerPoints, String currentPlayer, boolean drawPhase) throws RemoteException {
        graphicalView.resumeMatch(playersUsernamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards, decksTopReigns, secretObjective, availableResources, placedCards, playerPoints, currentPlayer, drawPhase);
    }

    @Override
    public void giveInitialCard(InitialCard initialCard) {
        graphicalView.giveInitialCard(initialCard);
    }

    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        graphicalView.giveSecretObjectives(secretObjectives);
    }

    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        graphicalView.someoneDrewInitialCard(someoneUsername, card);
    }

    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side, Map<Symbol, Integer> availableResources) {
        graphicalView.someoneSetInitialSide(someoneUsername, side, availableResources);
    }

    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        graphicalView.someoneDrewSecretObjective(someoneUsername);
    }

    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        graphicalView.someoneChoseSecretObjective(someoneUsername);
    }

    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points,
                                  Map<Symbol, Integer> availableResources) {
        graphicalView.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
    }

    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
                                Pair<Symbol, Symbol> deckTopReigns) {
        graphicalView.someoneDrewCard(someoneUsername, source, card, replacementCard, deckTopReigns);
    }

    @Override
    public void someoneJoined(String someoneUsername, List<String> joinedPlayers) {
        graphicalView.someoneJoined(someoneUsername, joinedPlayers);
    }

    @Override
    public void someoneQuit(String someoneUsername) {
        graphicalView.someoneQuit(someoneUsername);
    }

    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        graphicalView.matchFinished(ranking);
    }

    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        graphicalView.someoneSentBroadcastText(someoneUsername, text);
    }

    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        graphicalView.someoneSentPrivateText(someoneUsername, text);
    }

    public abstract void sendBroadcastText(String text);

    public abstract void sendPrivateText(String recipient, String text);

    public abstract void disconnect();
}
