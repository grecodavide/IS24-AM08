package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.utils.*;

/**
 * 
 */
public interface MatchObserver {
    void matchStarted();

    void someoneDrewInitialCard(Player someone, InitialCard card);

    void someoneSetInitialSide(Player someone, Side side);

    void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives);

    void someoneChoseSecretObjective(Player someone);

    void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side);
    
    void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard);

    void matchFinished();

}
