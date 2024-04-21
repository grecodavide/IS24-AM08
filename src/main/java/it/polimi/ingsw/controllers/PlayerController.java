package it.polimi.ingsw.controllers;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.util.List;

public abstract class PlayerController implements MatchObserver {

    public abstract void drawInitialCard();

    public abstract void chooseInitialCardSide(Side side);

    public abstract void drawSecretObjectives();
    
    public abstract void chooseSecretObjective(Objective objective);

    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side);

    public abstract void drawCard(DrawSource source);

}
