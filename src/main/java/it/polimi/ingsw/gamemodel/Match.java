package it.polimi.ingsw.gamemodel;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Match {
    private final List<Player> players;
    private final int maxPlayers;
    private Player currentPlayer;

    private MatchState currentState;

    // All cards decks
    private final GameDeck<InitialCard> initialsDeck;
    private final GameDeck<ResourceCard> resourcesDeck;
    private final GameDeck<GoldCard> goldsDeck;
    private final GameDeck<Objective> objectivesDeck;

    // All the visible cards on the common table
    private Pair<ResourceCard, ResourceCard> visibleResources;
    private Pair<GoldCard, GoldCard> visibleGolds;
    private Pair<Objective, Objective> visibleObjectives;

    private Pair<Objective, Objective> currentProposedObjectives;

    // Denotes if the match has been started or has finished
    private boolean started = false;
    private boolean lastTurn = false;
    private boolean finished = false;


    /**
     * Constructor to be used to initialize main Match attributes and allocate the attribute players List.
     * @param maxPlayers maximum number of players to be added to the match, chosen by the first player to join
     * @param initialsDeck deck of initial cards
     * @param resourcesDeck deck of resource cards
     * @param goldsDeck deck of gold cards
     * @param objectivesDeck deck of objectives
     */
    public Match(int maxPlayers, GameDeck<InitialCard> initialsDeck, GameDeck<ResourceCard> resourcesDeck, GameDeck<GoldCard> goldsDeck, GameDeck<Objective> objectivesDeck) {
        this.maxPlayers = maxPlayers;
        this.initialsDeck = initialsDeck;
        this.resourcesDeck = resourcesDeck;
        this.goldsDeck = goldsDeck;
        this.objectivesDeck = objectivesDeck;

        this.players = new ArrayList<Player>();
    }

    // Called by the controller
    /**
     * Method that adds a new player to the match; if the player is already in, throws an exception.
     * @param player player to be added to the match
     * @throws IllegalArgumentException thrown if player already in the match
     */
    public void addPlayer(Player player) throws IllegalArgumentException, WrongStateException {
        if(!players.contains(player)) {
            currentState.addPlayer();
            players.add(player);
        } else {
            throw new IllegalArgumentException("Duplicated Player in a Match");
        }
    }

    // Called by the controller
    /**
     *
     * @param player
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    // Called by the controller
    /**
     *
     * @return
     */
    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    /**
     * Method that changes the currentPlayer based on the next turn
     * If it is the first turn, currentPlayer gets initialized as the
     * first one in the players List.The turn order follows the list order.
     * Called by ChoosePlayerState every time a new turn starts
     */
    protected void nextPlayer() {
        // If player has never been initialized OR the current player is the last one
        if (currentPlayer == null || currentPlayer.equals(players.getLast())) {
            // Set currentPlayer as the first one
            currentPlayer = players.getFirst();
        } else {
            // Get the index of the current player and choose the next one
            int currentPlayerIndex = players.indexOf(currentPlayer);
            currentPlayer = players.get(currentPlayerIndex + 1);
        }
    }
    // Called by the state
    /**
     *
     */
    protected void doFinish() {
        finished = true;
    }

    // Called by the controller
    /**
     *
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    protected void doStart() {
        started = true;
    }

    public boolean isStarted() {
        return started;
    }

    // Called by the controller
    /**
     *
     * @return
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }
    // Called by the state

    /**
     *
     * @param state
     */
    protected void setState(MatchState state) {
        this.currentState = state;
    }

    /**
     *
     * @return
     */
    protected Pair<Objective, Objective> proposeSecretObjectives() {
        Objective obj1 = objectivesDeck.pop();
        Objective obj2 = objectivesDeck.pop();
        currentProposedObjectives = new Pair<>(obj1, obj2);
        return currentProposedObjectives;
    }

    // Called by the controller
    /**
     *
     * @param objective
     */
    protected void chooseSecretObjective(Objective objective) {
        // Put back the player's refused secret objective
        objectivesDeck.add(objective);
    }

    /**
     *
     */
    protected void setupPlayers() {
        // Shuffle players List
        Collections.shuffle(players);

        // Set players' colors
        for (int i = 0; i < maxPlayers; i++) {
            players.get(i).setColor(Color.values()[i]);
        }
    }

    /**
     *
     */
    protected void setupDecks() {
        // Shuffle each deck
        initialsDeck.shuffle();
        resourcesDeck.shuffle();
        goldsDeck.shuffle();
        objectivesDeck.shuffle();

        // Pop two resources to be placed on the common table
        ResourceCard resourceCard1 = resourcesDeck.pop();
        ResourceCard resourceCard2 = resourcesDeck.pop();

        // Pop two golds to be placed on the common table
        GoldCard goldCard1 = goldsDeck.pop();
        GoldCard goldCard2 = goldsDeck.pop();

        // Pop two golds to be placed on the common table
        Objective objective1 = objectivesDeck.pop();
        Objective objective2 = objectivesDeck.pop();

        // Set popped cards in Match attributes
        visibleGolds = new Pair<>(goldCard1, goldCard2);
        visibleResources = new Pair<>(resourceCard1, resourceCard2);
        visibleObjectives = new Pair<>(objective1, objective2);
    }

    /**
     *
     */
    protected void setupBoards() {
        // Give starting cards to players
        for (Player player : players) {
            // Pop a card from the resources deck and one from the golds deck
            GoldCard goldCard = goldsDeck.pop();
            ResourceCard resourceCard1 = resourcesDeck.pop();
            ResourceCard resourceCard2 = resourcesDeck.pop();

            // Add each card to the player's hand
            player.getBoard().addHandCard(goldCard);
            player.getBoard().addHandCard(resourceCard1);
            player.getBoard().addHandCard(resourceCard2);

            // Place the initial card to the player's board
            // By default, the initial card is placed on front side
            Pair<Integer, Integer> initialCoords = new Pair<>(0,0);
            InitialCard initial = initialsDeck.pop();
            player.getBoard().placeCard(initialCoords, initial, Side.FRONT);

        }
    }

    /**
     *
     * @param coords
     * @param card
     * @param side
     * @throws WrongStateException
     * @throws WrongCardPlacementException
     */
    protected void makeMove(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException, WrongCardPlacementException {
        Board currentPlayerBoard = currentPlayer.getBoard();

        // If placing the card in the current player's board is allowed by rules
        if (currentPlayerBoard.verifyPlacement(coords, card, side)) {

            // Trigger current state behavior
            currentState.makeMove();

            // Place the card in the current player's board
            // and save the points possibly gained because of the move
            int gainedPoints = currentPlayerBoard.placeCard(coord, card, side);

            // Remove the card from the player's hand
            // since it has been placed on the board
            currentPlayerBoard.removeHandCard(card);

            // Update the current player's points
            currentPlayer.addPoints(gainedPoints);

            // If the current player reaches 20 points or more
            // the last turn of the match starts
            if (currentPlayer.getPoints() >= 20)
                lastTurn = true;

            // If the current player is the last one in the match turns rotation
            // i.e. the last one in the players List
            // AND the current turn is the last one
            // the match is now finished
            if (currentPlayer.equals(players.getLast()) && lastTurn)
                finished = true;
        } else {
            throw new WrongCardPlacementException("Card placement not valid!");
        }
    }
}
