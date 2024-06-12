package it.polimi.ingsw.gamemodel;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.utils.Pair;

/**
 * Represents the match played by {@link Player} instances, therefore implements a slice of game logic
 * using drawCard(...), setInitialSide(...), setSecretObjective(...), proposeSecretObjective(...), etc.
 * Other methods serve the purpose of being called by {@link MatchState} subclasses in order to notify the change
 * of the current game state or trigger some changes in the match, such as setupBoards(...),
 * doStart(...), etc.
 * Few methods are called by the current player of the match, used to trigger a change in the match and so notify that
 * an event occurred, such as nextPlayer(...).
 */
public class Match implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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
    private final Map<DrawSource, PlayableCard> visiblePlayableCards;
    private Pair<Objective, Objective> visibleObjectives;

    private Pair<Objective, Objective> currentProposedObjectives;
    private InitialCard currentGivenInitialCard;

    // Denotes if the match has been started/finished
    private boolean started = false;
    private boolean initialTurnFinished = false;
    private boolean lastTurn = false;
    private boolean finished = false;

    // Current match turn as an integer (incremental)
    private int turn;

    // Players ranking of the match at the end of it.
    // The List order represents the ranking order, the Boolean represent if the related player is a winner.
    // This is needed since the match can end in a tie, in such case the first two/three players of the List will have a
    // True flag.
    private List<Pair<Player, Boolean>> playersFinalRanking;

    // List of observers
    private final transient List<MatchObserver> observers;

    /**
     * Initializes main Match attributes and allocate the attribute players List, assuming no parameter is null.
     *
     * @param maxPlayers     maximum number of players to be added to the match, chosen by the first player joining the match
     * @param initialsDeck   deck of initial cards
     * @param resourcesDeck  deck of resource cards
     * @param goldsDeck      deck of gold cards
     * @param objectivesDeck deck of objectives
     * @throws IllegalArgumentException if the decks provided do not have enough cards to start a game or maxPlayers are not 2,3,4
     */
    public Match(int maxPlayers, GameDeck<InitialCard> initialsDeck, GameDeck<ResourceCard> resourcesDeck, GameDeck<GoldCard> goldsDeck, GameDeck<Objective> objectivesDeck) throws IllegalArgumentException {
        this.maxPlayers = maxPlayers;
        this.initialsDeck = initialsDeck;
        this.resourcesDeck = resourcesDeck;
        this.goldsDeck = goldsDeck;
        this.objectivesDeck = objectivesDeck;
        this.currentState = new WaitState(this);
        this.observers = new ArrayList<>();

        if (goldsDeck.getSize() < maxPlayers + 2)
            throw new IllegalArgumentException("goldsDeck does not have enough cards");
        else if (resourcesDeck.getSize() < maxPlayers * 2 + 2)
            throw new IllegalArgumentException("resourcesDeck does not have enough cards");
        else if (initialsDeck.getSize() < maxPlayers)
            throw new IllegalArgumentException("initialDeck does not have enough cards");
        else if (objectivesDeck.getSize() < 6)
            throw new IllegalArgumentException("objectivesDeck does not have enough cards");
        // TODO: handle this exception!!!
        else if (maxPlayers < 2 || maxPlayers > 4)
            throw new IllegalArgumentException("The players must be at least 2 or maximum 4");

        this.players = new ArrayList<>();
        this.visiblePlayableCards = new HashMap<>();
    }

    /**
     * Adds a new player to the match, assuming it's not null.
     * Note: Called by the Controller when a player joins the match.
     *
     * @param player player to be added to the match
     * @throws IllegalArgumentException if the player is already in the match or too many players would be in the match
     * @throws WrongStateException      if called while in a state that doesn't allow adding players
     */
    public void addPlayer(Player player) throws IllegalArgumentException, WrongStateException, AlreadyUsedUsernameException {
        synchronized (this) {
            List<String> playersUsernames = getPlayers().stream().map(Player::getUsername).toList();

            if (playersUsernames.contains(player.getUsername()))
                throw new AlreadyUsedUsernameException("The chosen username is already in use");

            currentState.addPlayer();
            players.add(player);
            notifyObservers(observer -> observer.someoneJoined(player));
            currentState.transition();
        }
    }

    /**
     * Removes a player from the match, assuming the player is in the match.
     * Note: Called by the Controller when a player quits the match.
     *
     * @param player player to be removed from the match
     */
    public void removePlayer(Player player) {
        synchronized (this) {
            if (players.contains(player)) {
                players.remove(player);
                // If in a state different from the wait state, end the match
                currentState.removePlayer();
                notifyObservers(observer -> observer.someoneQuit(player));
            }
        }
    }

    /**
     * Verifies if the match is full, thus no more players can join.
     * Note: Used by the Controller
     *
     * @return true if the match is full, false otherwise
     */
    public boolean isFull() {
        return !finished && players.size() == maxPlayers;
    }

    /**
     * Modifies the current player according to the next turn: if it's the first turn, the current player is the first
     * one in the players List, the turn order then follows the players List order, in a circular way.
     * Ex. 1st -> 2nd -> 3rd ---> 1st -> 2nd etc.
     * Note: Called by NextTurnState every time a new turn starts.
     */
    protected void nextPlayer() {
        if (!players.isEmpty()) {
            // If player has never been initialized OR the current player is the last one
            if (currentPlayer == null || currentPlayer.equals(players.getLast())) {
                // Set currentPlayer as the first one
                currentPlayer = players.getFirst();

                turn++;
            } else {
                // Get the index of the current player and choose the next one
                int currentPlayerIndex = players.indexOf(currentPlayer);
                currentPlayer = players.get(currentPlayerIndex + 1);
            }
        } else {
            throw new RuntimeException("No players in the match, the next player cannot be set");
        }
    }

    /**
     * Verifies if the match is finished.
     * Note: Called by the Controller and NextTurnState.
     *
     * @return true if the match is finished, false otherwise
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Marks the initial turn as finished, assuming the initial turn hasn't finished yet.
     * Called by ChooseInitialCardState once the initial turn is finished.
     */
    protected void doInitialTurnFinish() {
        initialTurnFinished = true;
    }

    /**
     * Verifies if the initial turn is finished.
     * Note: Called by NextTurnState.
     *
     * @return true if the initial turn is finished, false otherwise
     */
    public boolean isInitialTurnFinished() {
        return initialTurnFinished;
    }

    /**
     * Marks the match as started, assuming the match hasn't started yet.
     * Note: Called by ChooseSecretObjectiveState once the match is ready to start.
     */
    protected void doStart() {
        started = true;
    }

    /**
     * Verifies if the match is started.
     * Note: Called by NextTurnState to check when to effectively start the match.
     *
     * @return true if the match is started, false otherwise
     */
    public boolean isStarted() {
        return started;
    }

    /**
     * Gets the player who's playing (or choosing the secret objective) at the moment.
     * Note: Used by the Controller.
     *
     * @return the player playing at the moment, null if the match has never reached NextTurnState
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the match players.
     *
     * @return the match players in a List, dynamically defined as an ArrayList
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Sets the current match state, assuming it's not null.
     * Note: Called by each state to let the match enter to the next state.
     *
     * @param state the state in which the match has to be
     */
    protected void setState(MatchState state) {
        this.currentState = state;
    }

    /**
     * Draws a card from the initial cards deck
     *
     * @return the card drawn from the initial cards deck
     * @throws WrongStateException if called while in a state that doesn't allow drawing an initial card
     */
    protected InitialCard drawInitialCard() throws WrongStateException {
        currentState.drawInitialCard();

        try {
            currentGivenInitialCard = initialsDeck.pop();
        } catch (DeckException e) {
            throw new RuntimeException(e);
        }

        // Notify observers and trigger state transition
        Player copy = new Player(currentPlayer);
        notifyObservers(observer -> observer.someoneDrewInitialCard(copy, currentGivenInitialCard));
        currentState.transition();

        return currentGivenInitialCard;
    }

    /**
     * Extracts two cards from the deck of objectives and returns them.
     * Note: Called by the Controller.
     *
     * @return two objective cards extracted from the objectives deck
     */
    protected Pair<Objective, Objective> proposeSecretObjectives() throws WrongStateException {
        currentState.proposeSecretObjectives();
        try {
            Objective obj1 = objectivesDeck.pop();
            Objective obj2 = objectivesDeck.pop();

            currentProposedObjectives = new Pair<>(obj1, obj2);

            // Notify observers and trigger state transition
            Player copy = new Player(currentPlayer);
            notifyObservers(observer -> observer.someoneDrewSecretObjective(copy, currentProposedObjectives));
            currentState.transition();

            return currentProposedObjectives;
        } catch (DeckException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks that the given objective is one of the proposed ones to the current player
     * and put the discarded objective back in the objectives deck.
     * Note: Called by the current player
     *
     * @param objective the accepted objective by the player (NOT the discarded one)
     */
    protected void setSecretObjective(Objective objective) throws WrongChoiceException, WrongStateException {
        currentState.chooseSecretObjective();

        // Get proposed objectives
        Objective firstProposedObjective = currentProposedObjectives.first();
        Objective secondProposedObjective = currentProposedObjectives.second();

        // Check if the chosen objective is one of the proposed ones and put it back in the deck
        if (objective.equals(firstProposedObjective))
            objectivesDeck.add(secondProposedObjective);
        else if (objective.equals(secondProposedObjective))
            objectivesDeck.add(firstProposedObjective);
        else
            // If the objective is not one of the proposed ones, throw an exception
            throw new WrongChoiceException("The chosen objective is not one of the proposed ones");

        // Notify observers and trigger state transition
        Player copy = new Player(currentPlayer);
        notifyObservers(observer -> observer.someoneChoseSecretObjective(copy, objective));
        currentState.transition();
    }

    /**
     * Shuffles the players turns order and gives them their pawn color.
     * Note: Called by SetupState.
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
     * Shuffles all thr cards decks and places the visible cards on the board
     * Note: Called by SetupState.
     */
    protected void setupDecks() {
        // Shuffle each deck
        initialsDeck.shuffle();
        resourcesDeck.shuffle();
        goldsDeck.shuffle();
        objectivesDeck.shuffle();

        try {
            // Pop two resources to be placed on the common table
            ResourceCard resourceCard1 = resourcesDeck.pop();
            ResourceCard resourceCard2 = resourcesDeck.pop();

            // Pop two golds to be placed on the common table
            GoldCard goldCard1 = goldsDeck.pop();
            GoldCard goldCard2 = goldsDeck.pop();

            // Pop two golds to be placed on the common table
            Objective objective1 = objectivesDeck.pop();
            Objective objective2 = objectivesDeck.pop();

            // Put golds and resources in visiblePlayableCards
            visiblePlayableCards.put(DrawSource.FIRST_VISIBLE, goldCard1);
            visiblePlayableCards.put(DrawSource.SECOND_VISIBLE, goldCard2);
            visiblePlayableCards.put(DrawSource.THIRD_VISIBLE, resourceCard1);
            visiblePlayableCards.put(DrawSource.FOURTH_VISIBLE, resourceCard2);

            visibleObjectives = new Pair<>(objective1, objective2);
        } catch (DeckException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gives one gold card and two resource cards to each player (hand)
     * and sets the initial card for each of them.
     * Note: Called by WaitState.
     */
    protected void setupBoards() {
        // Give starting cards to players
        for (Player player : players) {
            try {
                // Pop a card from the resources deck and one from the golds deck
                GoldCard goldCard = goldsDeck.pop();
                ResourceCard resourceCard1 = resourcesDeck.pop();
                ResourceCard resourceCard2 = resourcesDeck.pop();

                // Add each card to the player's hand
                player.getBoard().addHandCard(goldCard);
                player.getBoard().addHandCard(resourceCard1);
                player.getBoard().addHandCard(resourceCard2);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Makes the chosen move on the board of the current player (known because of the internal Match state);
     * in particular, checks if the placement is valid, then places the card on the player's board and add points
     * to the player.
     * Note: Called by the current player.
     *
     * @param coords coordinates in which to place the card
     * @param card   card to place
     * @param side   side of the card to be placed
     * @throws WrongStateException  if called while in a state that doesn't allow making moves
     * @throws WrongChoiceException if the move is not allowed (placement not allowed, or not enough resources, or card
     *                              not in player's hand)
     */
    protected void makeMove(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException, WrongChoiceException {
        currentState.makeMove();

        Board currentPlayerBoard = currentPlayer.getBoard();

        PlacementOutcome outcome;
        try {
            outcome = currentPlayerBoard.verifyCardPlacement(coords, card, side);
        } catch (CardException e) {
            throw new WrongChoiceException(e.getMessage());
        }

        // If placing the card in the current player's board is allowed by rules
        switch (outcome) {
            case VALID:
                // Place the card in the current player's board
                // and save the points possibly gained because of the move
                int gainedPoints;
                try {
                    gainedPoints = currentPlayerBoard.placeCard(coords, card, side, turn);
                } catch (CardException e) {
                    throw new RuntimeException(e);
                }

                // Remove the card from the player's hand
                // since it has been placed on the board
                try {
                    currentPlayerBoard.removeHandCard(card);
                } catch (HandException e) {
                    throw new RuntimeException(e);
                }

                // Update the current player's points
                currentPlayer.addPoints(gainedPoints);

                // If the current player reaches 20 points or more
                // the last turn of the match starts
                if (currentPlayer.getPoints() >= 20)
                    lastTurn = true;

                // Notify observers and trigger state transition
                Player copy = new Player(currentPlayer);
                notifyObservers(observer -> observer.someonePlayedCard(copy, coords, card, side));
                currentState.transition();

                break;
            case INVALID_COORDS:
                throw new WrongChoiceException("Invalid coordinates!");
            case INVALID_ENOUGH_RESOURCES:
                throw new WrongChoiceException("Not enough resources!");
        }
    }

    /**
     * Draws a card from the passed source. If the caller wants to draw one of the four visible cards, a rule is applied:
     * the first and second visible cards (FIRST/SECOND_VISIBLE) will be substituted by a gold card if possible, if not,
     * by a resource card, if not again, they will be null (then not substituted); the first and second visible cards
     * (THIRD/FOURTH_VISIBLE) will be substituted by a resource card if possible, if not, by a gold card, if not again,
     * they will be null (then not substituted).
     * Note: Called by the current player.
     *
     * @param source the source to draw a card from
     * @return the card drawn
     * @throws WrongStateException  if called while in a state that doesn't allow making moves
     * @throws WrongChoiceException if the source does not have cards
     */
    protected PlayableCard drawCard(DrawSource source) throws WrongStateException, WrongChoiceException {
        PlayableCard card;
        PlayableCard replacementCard = null;

        currentState.drawCard();

        switch (source) {
            case GOLDS_DECK -> {
                try {
                    card = goldsDeck.pop();
                    replacementCard = goldsDeck.peek();
                } catch (DeckException e) {
                    throw new WrongChoiceException("The gold cards deck is empty!");
                }
            }

            case RESOURCES_DECK -> {
                try {
                    card = resourcesDeck.pop();
                    replacementCard = resourcesDeck.peek();
                } catch (DeckException e) {
                    throw new WrongChoiceException("The resource cards deck is empty!");
                }
            }

            case FIRST_VISIBLE, SECOND_VISIBLE -> {
                card = visiblePlayableCards.get(source);

                // If not present (e.g. on the last turn both decks are empty, so remaining turns will be played
                // drawing the four visible cards, but they won't be substituted by others) throw an exception
                if (card == null)
                    throw new WrongChoiceException("There is no visible card in the chosen position!");

                // If the golds deck is NOT empty, substitute the first/second visible
                // card with a new gold
                if (!goldsDeck.isEmpty()) {
                    replacementCard = goldsDeck.poll();
                    visiblePlayableCards.put(source, replacementCard);
                }
                // If the golds deck is empty, substitute the first/second visible
                // card with a resource
                else {
                    replacementCard = resourcesDeck.poll();
                    visiblePlayableCards.put(source, replacementCard);
                    // If the resources deck is empty too, the GameDeck.poll() method returns null,
                    // then the corresponding visible card will be null
                }
            }

            case THIRD_VISIBLE, FOURTH_VISIBLE -> {
                card = visiblePlayableCards.get(source);

                // If not present (e.g. on the last turn both decks are empty, so remaining turns will be played
                // drawing the four visible cards, but they won't be substituted by others) throw an exception
                if (card == null)
                    throw new WrongChoiceException("There is no visible card in the chosen position!");

                // If the resources deck is NOT empty, substitute the third/fourth visible
                // card with a new resource
                if (!resourcesDeck.isEmpty()) {
                    replacementCard = resourcesDeck.poll();
                    visiblePlayableCards.put(source, replacementCard);
                }
                // If the resources deck is empty, substitute the third/fourth visible
                // card with a gold
                else {
                    replacementCard = goldsDeck.poll();
                    visiblePlayableCards.put(source, replacementCard);
                }
                // If the golds deck is empty too, the GameDeck.poll() method returns null,
                // then the corresponding visible card will be null
            }

            default -> throw new RuntimeException("Unexpected value of source");
        }

        if (goldsDeck.isEmpty() && resourcesDeck.isEmpty())
            lastTurn = true;

        // If the current player is the last one in the match turns rotation, i.e. the last one in the players List
        // AND the current turn is the last one the match is now finished
        if (currentPlayer.equals(players.getLast()) && lastTurn)
            finished = true;

        // Notify observers and trigger state transition
        PlayableCard replacementCardFinal = replacementCard;
        Player copy = new Player(currentPlayer);
        notifyObservers(observer -> observer.someoneDrewCard(copy, source, card, replacementCardFinal));
        currentState.transition();

        return card;
    }

    /**
     * Sets the current player's initial card side.
     *
     * @param side the side to put the initial card on
     * @throws WrongStateException if called while in a state that doesn't allow choosing the initial card side
     */
    protected void setInitialSide(Side side, Map<Symbol, Integer> availableResources) throws WrongStateException {
        currentState.chooseInitialSide();

        try {
            currentPlayer.getBoard().setInitialCard(currentGivenInitialCard, side);
        } catch (CardException e) {
            throw new RuntimeException(e);
        }

        currentGivenInitialCard = null;

        // Notify observers and trigger state transition
        Player copy = new Player(currentPlayer);
        notifyObservers(observer -> observer.someoneSetInitialSide(copy, side, availableResources));
        currentState.transition();
    }

    // Returns a Map that links each player to the number of DIFFERENT objectives achieved
    private Map<Player, Integer> checkObjectivesAchievement() {
        // Map that links each player to the number of DIFFERENT objectives achieved
        Map<Player, Integer> playersAchievedObjectives = new HashMap<>();

        Objective firstObjective = visibleObjectives.first();
        Objective secondObjective = visibleObjectives.second();

        for (Player p : players) {
            Board board = p.getBoard();
            Objective secretObjective = p.getSecretObjective();
            int numAchievedObjectives = 0;

            // Add to the player the points of the specific objective MULTIPLIED BY how many times they met the
            // objective requirement
            if (secretObjective != null)
                p.addPoints(secretObjective.getPoints() * secretObjective.getReq().timesMet(board));
            p.addPoints(firstObjective.getPoints() * firstObjective.getReq().timesMet(board));
            p.addPoints(secondObjective.getPoints() * secondObjective.getReq().timesMet(board));

            // Count the number of achieved objectives by the player
            if (secretObjective != null && secretObjective.getReq().timesMet(board) >= 1)
                numAchievedObjectives++;
            if (firstObjective.getReq().timesMet(board) >= 1)
                numAchievedObjectives++;
            if (secondObjective.getReq().timesMet(board) >= 1)
                numAchievedObjectives++;

            playersAchievedObjectives.put(p, numAchievedObjectives);
        }

        return playersAchievedObjectives;
    }

    /**
     * Calculates the winner (or winners)
     */
    protected void decideWinner() {
        finished = true;
        playersFinalRanking = new ArrayList<>();
        Map<Player, Integer> achievedObjectives = checkObjectivesAchievement();

        List<Player> sortedPlayers = players.stream()
                .sorted(
                        // Create a comparator that firstly sorts based on player points
                        // and secondly, in case of same points, on the number of achieved objectives
                        // Please note: reversed() since the default sort is ascending (min first), but the expected
                        // results requires a descending sort (max points/objectives first)
                        Comparator.comparingInt(Player::getPoints)
                                .thenComparing(achievedObjectives::get)
                                .reversed()
                )
                .toList();

        Player bestPlayer = sortedPlayers.getFirst();
        int bestAchievedObjectives = achievedObjectives.get(bestPlayer);
        int bestPoints = bestPlayer.getPoints();
        boolean isWinner;

        for (Player p : sortedPlayers) {
            // If the current player has as many points and as many achieved objectives as the winner,
            // then they're winner too
            isWinner = p.getPoints() == bestPoints && achievedObjectives.get(p) == bestAchievedObjectives;

            playersFinalRanking.add(new Pair<>(p, isWinner));
        }

        // Notify observers
        notifyObservers(MatchObserver::matchFinished);
    }

    /**
     * Getter for the final ranking of players. Return a valid result if and only if called when the match is in the
     * FinalState, so it's finished.
     *
     * @return Players ranking of the match at the end of it; the List order represents the ranking order, the Boolean
     * represent if the related player is a winner; this is needed since the match can end in a tie, in such case the
     * first two/three players of the List will have a True flag
     */
    public List<Pair<Player, Boolean>> getPlayersFinalRanking() {
        return playersFinalRanking;
    }

    /**
     * Returns the visible objectives.
     *
     * @return a Pair containing the two visible objectives
     */
    public Pair<Objective, Objective> getVisibleObjectives() {
        return visibleObjectives;
    }

    /**
     * Getter for the four visible playable cards (i.e. resource cards and gold cards, not objectives) on the common
     * table.
     *
     * @return a Map that links each visible playable card to a DrawSource (restricted to FIRST_VISIBLE, SECOND_VISIBLE,
     * THIRD_VISIBLE, FOURTH_VISIBLE)
     */
    public Map<DrawSource, PlayableCard> getVisiblePlayableCards() {
        return visiblePlayableCards;
    }

    /**
     * Getter for the current match state.
     *
     * @return the current state of the match
     */
    public MatchState getCurrentState() {
        return currentState;
    }

    /**
     * Getter for the cards back on the top of the decks (i.e. those visible top cards).
     * Both of them always contain just a reign.
     *
     * @return Pair of two reign Symbol (see {@link Symbol}.getReigns()), the first one regards the
     * top card of gold cards deck, the second one regards the top card of resource cards deck
     */
    public Pair<Symbol, Symbol> getDecksTopReigns() {
        PlayableCard goldCard = goldsDeck.peek();
        PlayableCard resourceCard = resourcesDeck.peek();

        Symbol goldReign;
        Symbol resourceReign;

        if (goldCard == null) {
            goldReign = null;
        } else {
            goldReign = goldCard.getReign();
        }

        if (resourceCard == null) {
            resourceReign = null;
        } else {
            resourceReign = resourceCard.getReign();
        }

        return new Pair<>(goldReign, resourceReign);
    }

    /**
     * Getter for the maximum number of player for the match
     *
     * @return The maximum number of player
     */
    public int getMaxPlayers() {
        return maxPlayers;
    }

    /**
     * Adds the given MatchObserver to those observers notified on match events.
     *
     * @param observer The observer to be notified from now on when an event occurs
     */
    public void subscribeObserver(MatchObserver observer) {
        observers.add(observer);
    }

    /**
     * Removes the given MatchObserver to those observers notified on match events.
     *
     * @param observer The observer to be removed
     */
    public void unsubscribeObserver(MatchObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all match observers that the match has started.
     * It's called by WaitState methods after the match setup, that's why it needs to be protected.
     */
    protected void notifyMatchStart() {
        notifyObservers(MatchObserver::matchStarted);
    }

    /**
     * Sends a broadcast message in the chat. Notifies all observers of the event.
     * Called by Player
     *
     * @param sender player that sends the message
     * @param text   content of the message
     */
    protected void sendBroadcastText(Player sender, String text) {
        notifyObservers(observer -> observer.someoneSentBroadcastText(sender, text));
    }

    /**
     * Sends a private message to the recipient chat. Notifies all observers of the event.
     * Called by Player
     *
     * @param sender    player that sends the message
     * @param recipient player that receives the message
     * @param text      content of the message
     */
    protected void sendPrivateText(Player sender, Player recipient, String text) {
        notifyObservers(observer -> observer.someoneSentPrivateText(sender, recipient, text));
    }

    /**
     * Notifies asynchronously all match observers, calling the passed MatchObserverCallable on each of them.
     * To be more specific: creates a thread for each match observer, each thread is appointed to call the passed callable
     * on a MatchObserver instance; then runs all of them; finally joins on them (waiting each of them to return and exit).
     *
     * @param observerCallable The "method" to be called on each observer of the match
     */
    private void notifyObservers(MatchObserverCallable observerCallable) {
        if(observers.isEmpty())
            return;

        ExecutorService executor = Executors.newFixedThreadPool(observers.size());

        for (MatchObserver observer : observers)
            executor.submit(() -> observerCallable.call(observer));

        executor.shutdown();
    }

    public synchronized boolean isRejoinable() {
        return players.stream().anyMatch((p) -> !p.isConnected()) && isStarted();
    }

}
