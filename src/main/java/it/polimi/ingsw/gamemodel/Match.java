package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.utils.Pair;

import java.util.List;

public class Match {
    private List<Player> players;
    private int maxPlayers;
    private MatchState currentState;
    private GameDeck<ResourceCard> resourcesDeck;
    private GameDeck<GoldCard> goldsDeck;
    private GameDeck<Objective> objectivesDeck;
    private Pair<ResourceCard, ResourceCard> visibleResources;
    private Pair<GoldCard, GoldCard> visibleGolds;
}
