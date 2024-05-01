package it.polimi.ingsw.server;

import it.polimi.ingsw.controllers.PlayerControllerRMI;
import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.ChosenMatchException;
import it.polimi.ingsw.exceptions.WrongStateException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ServerRMIInterface extends Remote {
    List<String> getJoinableMatches() throws RemoteException;
    PlayerControllerRMI joinMatch(String matchName, String nickname) throws RemoteException, ChosenMatchException, AlreadyUsedNicknameException, WrongStateException;
    void createMatch(String matchName, int maxPlayers) throws RemoteException, ChosenMatchException;
}
