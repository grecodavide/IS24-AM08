package it.polimi.ingsw.client;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ViewInterface extends Remote {
    void matchStarted(Map<Color, String> playersNicknamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException;

    void giveInitialCard(InitialCard initialCard) throws RemoteException;

    void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException;

    void someoneDrewInitialCard(String someoneNickname, InitialCard card) throws RemoteException;

    void someoneSetInitialSide(String someoneNickname, Side side) throws RemoteException;

    void someoneDrewSecretObjective(String someoneNickname) throws RemoteException;

    void someoneChoseSecretObjective(String someoneNickname, Objective objective) throws RemoteException;

    void someonePlayedCard(String someoneNickname, Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException;

    void someoneDrewCard(String someoneNickname, DrawSource source, Card card) throws RemoteException;

    void matchFinished() throws RemoteException;
}
