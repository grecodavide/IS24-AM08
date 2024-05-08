package it.polimi.ingsw.controllers;

import it.polimi.ingsw.exceptions.AlreadyUsedNicknameException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.gamemodel.Match;
import it.polimi.ingsw.gamemodel.MatchTest;
import org.junit.Test;

import java.rmi.RemoteException;

import static org.junit.Assert.fail;

public class PlayerControllerRMITest {
    private Match match;
    private PlayerController player1;
    private PlayerController player2;
    private PlayerController player3;
    private PlayerController player4;

    public PlayerControllerRMITest() {
        match = new Match(4, MatchTest.createDeterministicInitialsDeck(10), MatchTest.createDeterministicResourcesDeck(40), MatchTest.createDeterministicGoldsDeck(40), MatchTest.createDeterministicObjectivesDeck(10));
    }

    @Test
    public void constructor() {
        try {
            player1 = new PlayerControllerRMI("player1", match, 1099);
        } catch (AlreadyUsedNicknameException | WrongStateException | RemoteException e) {
            fail("player1 init shouldn't throw exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            player2 = new PlayerControllerRMI("player1", match, 1099);
            // An exception is supposed to be thrown here
            fail("player 2 init should have thrown AlreadyUsedNicknameException");
        } catch (AlreadyUsedNicknameException e) {
        } catch (WrongStateException | RemoteException e) {
            fail("player2 initialization shouldn't thrown this specific exception exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            player3 = new PlayerControllerRMI("player3", match, 8000);
            // An exception is supposed to be thrown here
            fail("player 3 init should have thrown RemoteException");
        } catch (RemoteException e) {
        } catch (AlreadyUsedNicknameException | WrongStateException e) {
            fail("player3 (right) init shouldn't throw this specific exception: " + e.getMessage());
            throw new RuntimeException(e);
        }

        try {
            player2 = new PlayerControllerRMI("player2", match, 1099);
            player3 = new PlayerControllerRMI("player3", match, 1099);
            player4 = new PlayerControllerRMI("player4", match, 1099);
        } catch (AlreadyUsedNicknameException | WrongStateException | RemoteException e) {
            throw new RuntimeException(e);
        }

        System.out.println("");

    }


}
