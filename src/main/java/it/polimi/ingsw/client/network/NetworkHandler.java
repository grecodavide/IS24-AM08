package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.controllers.PlayerController;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.server.Server;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class used by a generic client to receive from and transmit to a remote {@link Server} instance and a
 * remote {@link PlayerController} instance.
 * It represents an abstract layer, being implemented by: {@link NetworkHandlerRMI} and {@link NetworkHandlerTCP}.
 */
public abstract class NetworkHandler implements RemoteViewInterface {
    protected final GraphicalView graphicalView;
    protected String username;
    protected final String ipAddress;
    protected final int port;
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
        // Create a thread pool of 1 thread
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable pingServer = () -> {
            if (!connected) {
                // If the connection lost is already acknowledged, shutdown the executor
                executor.shutdown();
            } else {
                if (!ping()) {
                    // If there is a connection error, notify the client and shutdown the executor
                    disconnect();
                    graphicalView.notifyConnectionLost();
                    executor.shutdown();
                }
            }
        };

        // Check every two second for connectivity
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
     * Checks for connectivity.
     *
     * @return The status of the connection, true if active, false otherwise
     */
    public abstract boolean ping();

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
     * Getter for the connection status.
     *
     * @return True if connected, false otherwise
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * Sends a message to all the match players
     *
     * @param text The content of the message
     */
    public abstract void sendBroadcastText(String text);

    /**
     * Sends a private message to a match player
     *
     * @param recipient The recipient username
     * @param text      The content of the message
     */
    public abstract void sendPrivateText(String recipient, String text);

    /**
     * Disconnects from the server.
     */
    public abstract void disconnect();

    /**
     * Receives the currently available matches.
     *
     * @param availableMatches The available matches
     */
    public void receiveAvailableMatches(List<AvailableMatch> availableMatches) {
        graphicalView.receiveAvailableMatches(availableMatches);
    }

    /**
     * Notifies that the match has just started.
     * Furthermore, gives to the receiving object all the information (parameters) needed to show to the current match
     * state.
     *
     * @param playersUsernamesAndPawns Map that matches each pawn color to the corresponding player's username
     * @param playersHands             Map that matches each player's username to the corresponding List of cards in the hand
     * @param visibleObjectives        Pair of objectives visible to all players
     * @param visiblePlayableCards     Map having as values the visible common cards (the keys are just indexes).
     * @param decksTopReigns           Pair of reign symbols representing the two visible reigns symbols on top of the two decks;
     *                                 the first one is the gold deck one, the second one the resource deck one
     */
    @Override
    public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
                             Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
                             Pair<Symbol, Symbol> decksTopReigns) {
        graphicalView.matchStarted(playersUsernamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards, decksTopReigns);
    }

    /**
     * Notifies that the match has resumed.
     * Furthermore, gives to the receiving object all the information (parameters) needed to show to the current match
     * state.
     *
     * @param playersUsernamesAndPawns Map that matches each pawn color to the corresponding player's username
     * @param playersHands             Map that matches each player's username to the corresponding List of cards in the hand
     * @param visibleObjectives        Pair of objectives visible to all players
     * @param visiblePlayableCards     Map having as values the visible common cards (the keys are just indexes).
     * @param decksTopReigns           Pair of reign symbols representing the two visible reigns symbols on top of the two decks;
     *                                 the first one is the gold deck one, the second one the resource deck one
     * @param secretObjective          Secret objective of the current player
     * @param availableResources       Available resources of all the players
     * @param placedCards              Placed cards of all the players
     * @param playerPoints             Points of all the players
     * @param currentPlayer            The current player
     * @param drawPhase                If the match is resumed in draw phase
     */
    @Override
    public void matchResumed(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
                             Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
                             Pair<Symbol, Symbol> decksTopReigns, Objective secretObjective, Map<String, Map<Symbol, Integer>> availableResources,
                             Map<String, Map<Pair<Integer, Integer>, PlacedCard>> placedCards, Map<String, Integer> playerPoints,
                             String currentPlayer, boolean drawPhase) {
        graphicalView.resumeMatch(playersUsernamesAndPawns, playersHands, visibleObjectives, visiblePlayableCards, decksTopReigns, secretObjective, availableResources, placedCards, playerPoints, currentPlayer, drawPhase);
    }

    /**
     * Gives to the receiving graphical view (the client) its initial card.
     *
     * @param initialCard The initial card to be given
     */
    @Override
    public void giveInitialCard(InitialCard initialCard) {
        graphicalView.giveInitialCard(initialCard);
    }

    /**
     * Gives to the remote object a pair of secret objectives to show them in the view and to choose one from them.
     *
     * @param secretObjectives Pair of secret objectives to give
     */
    @Override
    public void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) {
        graphicalView.giveSecretObjectives(secretObjectives);
    }

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn its initial card.
     *
     * @param someoneUsername The username of the player who has drawn the card
     * @param card            The card drawn
     */
    @Override
    public void someoneDrewInitialCard(String someoneUsername, InitialCard card) {
        graphicalView.someoneDrewInitialCard(someoneUsername, card);
    }

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has decided (then set) its initial card side.
     *
     * @param someoneUsername    The username of the player who has set the initial card side
     * @param side               The chosen side
     * @param availableResources The current available resources of the player having someoneUsername as username
     */
    @Override
    public void someoneSetInitialSide(String someoneUsername, Side side, Map<Symbol, Integer> availableResources) {
        graphicalView.someoneSetInitialSide(someoneUsername, side, availableResources);
    }

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn a pair of secret objectives.
     * Mind that the objectives are not passed as arguments, since they are secret to all players but the one receiving
     * them. The one meant to receive them receives this message too but obtain the objectives through the
     * giveSecretObjective() method.
     *
     * @param someoneUsername The username of the player who has drawn the card
     */
    @Override
    public void someoneDrewSecretObjective(String someoneUsername) {
        graphicalView.someoneDrewSecretObjective(someoneUsername);
    }

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has chosen theirs secret objective.
     *
     * @param someoneUsername The username of the player who has chosen theirs secret objective
     */
    @Override
    public void someoneChoseSecretObjective(String someoneUsername) {
        graphicalView.someoneChoseSecretObjective(someoneUsername);
    }

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has played a card.
     *
     * @param someoneUsername    The username of the player who has played a card
     * @param coords             The coordinates where the card has been placed as a Pair of int
     * @param card               The card that has been played
     * @param side               The side on which the card has been played
     * @param points             The points of the player who played a card
     * @param availableResources The current available resources of the player who played a card
     */
    @Override
    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points,
                                  Map<Symbol, Integer> availableResources) {
        graphicalView.someonePlayedCard(someoneUsername, coords, card, side, points, availableResources);
    }

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn a card.
     *
     * @param someoneUsername The username of the player who has played a card
     * @param source          The DrawSource from which the card has been drawn
     * @param card            The card that has been drawn
     * @param replacementCard The card that replaced the drawn one
     * @param deckTopReigns   The decks top reigns
     */
    @Override
    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
                                Pair<Symbol, Symbol> deckTopReigns) {
        graphicalView.someoneDrewCard(someoneUsername, source, card, replacementCard, deckTopReigns);
    }

    /**
     * Notifies that a player has joined the match.
     *
     * @param someoneUsername The username of the player who has joined
     * @param joinedPlayers   The players currently in the match
     */
    @Override
    public void someoneJoined(String someoneUsername, List<String> joinedPlayers) {
        graphicalView.someoneJoined(someoneUsername, joinedPlayers);
    }

    /**
     * Notifies that a player has quit from the match.
     *
     * @param someoneUsername The username of the player who has quit
     */
    @Override
    public void someoneQuit(String someoneUsername) {
        graphicalView.someoneQuit(someoneUsername);
    }

    /**
     * Notifies that the match has just finished.
     *
     * @param ranking The match final ranking
     */
    @Override
    public void matchFinished(List<LeaderboardEntry> ranking) {
        graphicalView.matchFinished(ranking);
    }

    /**
     * Notifies that a new message in the global chat is sent
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     */
    @Override
    public void someoneSentBroadcastText(String someoneUsername, String text) {
        graphicalView.someoneSentBroadcastText(someoneUsername, text);
    }

    /**
     * Notifies that a new private message is sent in private chat to the current user
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     */
    @Override
    public void someoneSentPrivateText(String someoneUsername, String text) {
        graphicalView.someoneSentPrivateText(someoneUsername, text);
    }
}
