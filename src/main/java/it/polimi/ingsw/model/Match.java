package it.polimi.ingsw.model;

import com.sun.tools.javac.util.Pair;

import java.util.List;

public class Match {
    private List<Players> players;
    private int maxPlayers;
    private MatchState currentState;
    private GameDeck<ResourceCard> resourcesDeck;
    private GameDeck<GoldCard> goldsDeck;
    private GameDeck<Objective> objectivesDeck;
    private Pair<ResourceCard, ResourceCard> visibleResources;
    private Pair<GoldCard, GoldCard> visibleGolds;
}
