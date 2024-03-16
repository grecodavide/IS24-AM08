package it.polimi.ingsw.model;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Match {
    private List<Player> players;
    private int maxPlayers;
    private MatchState currentState;
    private Player currentPlayer;
    private GameDeck<InitialCard> initialsDeck;
    private GameDeck<ResourceCard> resourcesDeck;
    private GameDeck<GoldCard> goldsDeck;
    private GameDeck<Objective> objectivesDeck;
    private Pair<ResourceCard, ResourceCard> visibleResources;
    private Pair<GoldCard, GoldCard> visibleGolds;
    private Pair<Objective, Objective> visibleObjectives;
    private boolean finished = false;
    private Pair<Objective, Objective> currentProposedObjectives;

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
     * Method that adds a new player to the match; if the player is already in, throws an IllegalArgument exception.
     * @param player player to be added to the match
     * @throws IllegalArgumentException thrown if player already in the match
     */
    public void addPlayer(Player player) throws IllegalArgumentException {
        if(!players.contains(player))
            players.add(player);
        else
            throw new IllegalArgumentException("Duplicated Player in a Match");
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

    // Called by the controller
    /**
     *
     * @return
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
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
        // Randomize players' turns
        Collections.shuffle(players);
        
        // Initialize currentPlayer
        currentPlayer = players.getFirst();

        // Set colors to players
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
}
