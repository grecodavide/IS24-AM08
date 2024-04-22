package it.polimi.ingsw.controllers;

import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.MatchObserver;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Player;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

import java.util.List;

public abstract class PlayerController implements MatchObserver {
    protected Player player;
    protected Match match;
    public PlayerController(String nickname, Match match) throws AlreadyUsedNicknameException {
        List<String> playersNicknames = match.getPlayers().stream().map(Player::getNickname).toList();

        if (playersNicknames.contains(nickname))
            throw new AlreadyUsedNicknameException("The chosen nickname is already in use");

        this.match = match;

        match.subscribeObserver(this);
    }

    public abstract void drawInitialCard();

    public abstract void chooseInitialCardSide(Side side);

    public abstract void drawSecretObjectives();
    
    public abstract void chooseSecretObjective(Objective objective);

    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side);

    public abstract void drawCard(DrawSource source);

}
