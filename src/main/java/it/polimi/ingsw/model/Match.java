package it.polimi.ingsw.model;

import com.sun.org.apache.xml.internal.security.Init;
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

    public Match(int maxPlayers, GameDeck<InitialCard> initialsDeck, GameDeck<ResourceCard> resourcesDeck, GameDeck<GoldCard> goldsDeck, GameDeck<Objective> objectivesDeck) {
        this.maxPlayers = maxPlayers;
        this.initialsDeck = initialsDeck;
        this.resourcesDeck = resourcesDeck;
        this.goldsDeck = goldsDeck;
        this.objectivesDeck = objectivesDeck;

        this.players = new ArrayList<Player>();
    }

    // Called by the controller
    public void addPlayer(Player player) throws IllegalArgumentException {
        if(!players.contains(player))
            players.add(player);
        else
            throw new IllegalArgumentException("Duplicated Player in a Match");
    }

    // Called by the controller
    public void removePlayer(Player player) {
        players.remove(player);
    }

    // Called by the controller
    public boolean isFull() {
        return players.size() == maxPlayers;
    }

    // Called by the state
    protected void doFinish() {
        finished = true;
    }

    // Called by the controller
    public boolean isFinished() {
        return finished;
    }

    // Called by the controller
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    // Called by the state
    protected void setState(MatchState state) {
        this.currentState = state;
    }

    protected Pair<Objective, Objective> proposeSecretObjectives() {
        Objective obj1 = objectivesDeck.pop();
        Objective obj2 = objectivesDeck.pop();
        currentProposedObjectives = new Pair<>(obj1, obj2);
        return currentProposedObjectives;
    }

    // Called by the controller
    protected void chooseSecretObjective(Objective objective) {
        // Put back the player's refused secret objective
        objectivesDeck.add(objective);
    }

    protected void setupPlayers() {
        // Randomize players
        Collections.shuffle(players);
        currentPlayer = players.getFirst();

        // Set colors to players
        for (int i = 0; i < maxPlayers; i++) {
            players.get(i).setColor(Color.values()[i]);
        }
    }
    protected void setupDecks() {
        // Shuffle decks
        initialsDeck.shuffle();
        resourcesDeck.shuffle();
        goldsDeck.shuffle();
        objectivesDeck.shuffle();

        // Cards on the table
        GoldCard goldCard1 = goldsDeck.pop();
        GoldCard goldCard2 = goldsDeck.pop();

        ResourceCard resourceCard1 = resourcesDeck.pop();
        ResourceCard resourceCard2 = resourcesDeck.pop();

        Objective objective1 = objectivesDeck.pop();
        Objective objective2 = objectivesDeck.pop();

        visibleGolds = new Pair<>(goldCard1, goldCard2);
        visibleResources = new Pair<>(resourceCard1, resourceCard2);
        visibleObjectives = new Pair<>(objective1, objective2);

        // Shuffle players??
        // Set first user turn
        currentPlayer = players.getFirst();
    }

    protected void setupBoards() {
        // Give cards to players
        for (Player player : players) {
            GoldCard goldCard = goldsDeck.pop();
            ResourceCard resourceCard1 = resourcesDeck.pop();
            ResourceCard resourceCard2 = resourcesDeck.pop();

            player.getBoard().addHandCard(goldCard);
            player.getBoard().addHandCard(resourceCard1);
            player.getBoard().addHandCard(resourceCard2);

            // Add initial card to the player's board
            // By default, the initial card is provided on front side
            Pair<Integer, Integer> initialCoords = new Pair<>(0,0);
            InitialCard initial = initialsDeck.pop();
            player.getBoard().placeCard(initialCoords, initial, Side.FRONT);

        }
    }


}
