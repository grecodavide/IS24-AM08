package it.polimi.ingsw.controllers;

import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.MatchObserver;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

public abstract class PlayerController implements MatchObserver {
    private Player player;
    private Match match;
    public PlayerController(Player player, Match match) {
        this.player = player;
        this.match = match;
    }

    public abstract void drawInitialCard();

    public abstract void chooseInitialCardSide(Side side);

    public abstract void drawSecretObjectives();
    
    public abstract void chooseSecretObjective(Objective objective);

    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side);

    public abstract void drawCard(DrawSource source);

}
