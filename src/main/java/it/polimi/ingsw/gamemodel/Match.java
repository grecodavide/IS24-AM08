package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.utils.Pair;
import java.util.ArrayList;
import it.polimi.ingsw.utils.MutablePair;
import java.util.Collections;
import java.util.List;
import it.polimi.ingsw.exceptions.*;

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
    private MutablePair<ResourceCard, ResourceCard> visibleResources;
    private MutablePair<GoldCard, GoldCard> visibleGolds;
    private Pair<Objective, Objective> visibleObjectives;

    private Pair<Objective, Objective> currentProposedObjectives;

    // Denotes if the match has been started or has finished
    private boolean started = false;
    private boolean initialTurnFinished = false;
    private boolean lastTurn = false;
    private boolean finished = false;

    // Current match turn as an integer (incremental)
    private int turn;


    /**
     * Initialize main Match attributes and allocate the attribute players List, assuming no parameter is null.
     * @param maxPlayers maximum number of players to be added to the match, chosen by the first player joining the match
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
     * Add a new player to the match, assuming it's not null.
     * Note: It's called by the Controller when a player joins the match.
     * @param player player to be added to the match
     * @throws IllegalArgumentException if the player is already in the match
     */
    public void addPlayer(Player player) throws IllegalArgumentException, WrongStateException {
        if(!players.contains(player)) {
            currentState.addPlayer();
            players.add(player);
        } else {
            throw new IllegalArgumentException("Duplicated Player in a Match");
        }
    }

    /**
     * Remove a player from the match, assuming the player is in the match.
     * Note: It's called by the Controller when a player quits the match.
     * @param player player to be removed from the match
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Verify if the match is full, thus no more players can join.
     * Note: It's used by the Controller
     * @return true if the match is full, false otherwise
     */
    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    /**
     * Modify the current player according to the next turn:
     * If it's the first turn, the current player gets initialized as the
     * first one in the players List; the turn order then follows the players List order, in a circular way.
     * Ex. 1 -> 2 -> 3 -> 1 -> etc.
     * Note It's called by NextTurnState every time a new turn starts.
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

    /**
     * Mark the match as finished, assuming the match hasn't finished yet.
     * Note: It's called by FinalState once the match is ready to finish.
     */
    protected void doFinish() {
        finished = true;
    }

    /**
     * Verify if the match is finished.
     * Note: It's called by the Controller and NextTurnState.
     * @return true if the match is finished, false otherwise
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Mark the initial turn as finished, assuming the initial turn hasn't finished yet.
     * Note: It's called by ChooseInitialCardState once the initial turn is finished.
     */
    protected void doInitialTurnFinish() {
        initialTurnFinished = true;
    }

    /**
     * Verify if the initial turn is finished.
     * Note: It's called by NextTurnState.
     * @return true if the initial turn is finished, false otherwise
     */
    public boolean isInitialTurnFinished() {
        return initialTurnFinished;
    }

    /**
     * Mark the match as started, assuming the match hasn't started yet.
     * Note: It's called by ChooseSecretObjectiveState once the match is ready to start.
     */
    protected void doStart() {
        started = true;
    }

    /**
     * Verify if the match is started.
     * Note: It's called by NextTurnState to check when to effectively start the match.
     * @return true if the match is started, false otherwise
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Get the player who's playing (or choosing the secret objective) at the moment.
     * Note: It's used by the Controller.
     * @return the player playing at the moment, null if the match has never reached NextTurnState
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Get the match players.
     * @return the match players in a List, dynamically defined as an ArrayList
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Set the current match state, assuming it's not null.
     * Note: It's called by each state to let the match enter to the next state.
     * @param state the state in which the match has to be
     */
    protected void setState(MatchState state) {
        this.currentState = state;
    }

    /**
     * Extract two cards from the deck of objectives and return them.
     * Note: It's called by the controller.
     * @return the two cards extracted
     */
    protected Pair<Objective, Objective> proposeSecretObjectives() {
        Objective obj1 = objectivesDeck.pop();
        Objective obj2 = objectivesDeck.pop();
        currentProposedObjectives = new Pair<>(obj1, obj2);
        return currentProposedObjectives;
    }

    /**
     * Check that the given objective is one of the proposed ones to the current player
     * and put the discarded objective back in the objectives deck.
     * Note: It's called by Player
     * @param objective the accepted objective by the player
     */
    protected void chooseSecretObjective(Objective objective) throws WrongThreadException {
        // Get proposed objectives
        Objective firstProposedObjective = currentProposedObjectives.first();
        Objective secondProposedObjective = currentProposedObjectives.second();
        // Check if the chosen objective is one of the proposed ones and put it back in the deck
        if (objective.equals(firstProposedObjective)) {
            objectivesDeck.add(secondProposedObjective);
        } else if (objective.equals(secondProposedObjective)) {
            objectivesDeck.add(firstProposedObjective);
        } else {
            // If the objective is not one of the proposed ones
            // throw an exception
            throw new WrongThreadException("The chosen objective is not one of the proposed ones"); 
        }
    }

    /**
     * Shuffle the players turn order and give them their pawn color
     * Note: It's called by SetupState
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
     * Shuffle cards decks and place the visible cards on the board
     * Note: It's called by SetupState
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
        visibleGolds = new MutablePair<GoldCard, GoldCard>(goldCard1, goldCard2);
        visibleResources = new MutablePair<ResourceCard, ResourceCard>(resourceCard1, resourceCard2);
        visibleObjectives = new Pair<>(objective1, objective2);
    }

    /**
     * Give one gold card and two resource cards to each player hand
     * and set the initial card
     * Note: It's called by WaitState
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
            InitialCard initialCard = initialsDeck.pop();
            player.getBoard().setInitialCard(initialCard);

        }
    }

    /**
     * Check if the placement is valid, then place the card on the board and add points to the player
     * Note: It's called by Player
     * @param coords coordinates in which to place the card
     * @param card card to place
     * @param side side of the card to be placed
     * @throws WrongStateException if called during a state that does not allow making moves
     * @throws WrongCardPlacementException if the placement is not valid
     */
    protected void makeMove(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException {
        Board currentPlayerBoard = currentPlayer.getBoard();
        // TODO: Fix implementation with new verifyCardPlacement method
        // If placing the card in the current player's board is allowed by rules
        if (currentPlayerBoard.verifyCardPlacement(coords, card, side)) {

            // Trigger current state behavior
            currentState.makeMove();

            // Place the card in the current player's board
            // and save the points possibly gained because of the move
            int gainedPoints = currentPlayerBoard.placeCard(coords, card, side, turn);

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

    /**
     * Draw a card from the requested source, and pick a new card from the deck if a visible card is chosen.
     * Note: It's called by Player
     * @param source represents the source of the draw
     * @throws WrongStateException if called during a state that does not allow making moves
     * @throws WrongChoiceException if the source does not have cards
     * @return the card drawn
     */
    protected PlayableCard drawCard(DrawSource source) throws WrongStateException, WrongChoiceException {
        currentState.drawCard();
        PlayableCard card;
        switch (source) {
            case GOLDS_DECK:
                if (goldsDeck.isEmpty())
                    throw new WrongChoiceException("Golds deck is empty!");
                card = goldsDeck.pop();
                break;
            case RESOURCES_DECK:
                if (resourcesDeck.isEmpty()) {
                    throw new WrongChoiceException("Resources deck is empty!");
                }
                card = resourcesDeck.pop();
                break;
            case FIRST_VISIBLE_GOLDS:
                card = visibleGolds.getFirst();
                if (card == null)
                    throw new WrongChoiceException("There is no visible gold in position one!");
                visibleGolds.setFirst(goldsDeck.poll());
                break; 
            case SECOND_VISIBLE_GOLDS:
                card = visibleGolds.getSecond();
                if (card == null)
                    throw new WrongChoiceException("There is no visible gold in position two!");
                visibleGolds.setSecond(goldsDeck.poll());
                break;
            case FIRST_VISIBLE_RESOURCES:
                card = visibleResources.getFirst();
                if (card == null)
                    throw new WrongChoiceException("There is no visible resource in position one!");
                visibleResources.setFirst(resourcesDeck.poll());
                break;
            case SECOND_VISIBLE_RESOURCES:
                card = visibleResources.getSecond();
                if (card == null)
                    throw new WrongChoiceException("There is no visible resource in position two!");
                visibleResources.setSecond(resourcesDeck.poll());
                break;
            default:
                throw new WrongChoiceException("Unexpected value: " + source);
        }

        if (goldsDeck.isEmpty() && resourcesDeck.isEmpty())
            lastTurn = true;
        return card;
    }

    protected void chooseInitialSide(Side side) throws WrongStateException {
        // TODO
        currentState.chooseInitialSide();
        currentPlayer.getBoard().setInitialSide(side);
    }
}
