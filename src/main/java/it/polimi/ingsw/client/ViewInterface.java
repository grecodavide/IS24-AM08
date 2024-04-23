package it.polimi.ingsw.client;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ViewInterface extends Remote {
    void giveInitialCard(InitialCard initialCard) throws RemoteException;

    void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException;

    void matchStarted(Map<Color, String> playersNicknamesAndPawns,  Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksCardsBacks) throws RemoteException;

    void someoneDrewInitialCard(Player someone, InitialCard card) throws RemoteException;

    void someoneSetInitialSide(Player someone, Side side) throws RemoteException;

    void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives) throws RemoteException;

    void someoneChoseSecretObjective(Player someone, Objective objective) throws RemoteException;

    void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException;

    void someoneDrewCard(Player someone, DrawSource source, Card card) throws RemoteException;

    void matchFinished() throws RemoteException;
}
