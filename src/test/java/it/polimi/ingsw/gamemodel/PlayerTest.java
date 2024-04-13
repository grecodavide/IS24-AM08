package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.exceptions.WrongTurnException;
import it.polimi.ingsw.utils.Pair;
import org.junit.Test;
import static org.junit.Assert.fail;

public class PlayerTest {
    // No Player method is specifically tested since the only actual action carried out by all of them is to check
    // if the calling player instance corresponds to the match current player (this == match.currentPlayer()) and,
    // if so, call a corresponding method of Match and/or of Board (e.g. Player.chooseInitialCardSide() just calls
    // Match.setInitialSide()); if the player doesn't correspond to the match current player, a WrongTurnException
    // is thrown: this occurrence is tested

    @Test
    public void notCurrentPlayer() {
        GameDeck<GoldCard> goldsDeck = MatchTest.createDeterministicGoldsDeck(40);
        GameDeck<ResourceCard> resourcesDeck = MatchTest.createDeterministicResourcesDeck(40);
        GameDeck<Objective> objectivesDeck = MatchTest.createDeterministicObjectivesDeck(10);
        GameDeck<InitialCard> initialsDeck = MatchTest.createDeterministicInitialsDeck(10);

        Match match = new Match(4, initialsDeck, resourcesDeck, goldsDeck, objectivesDeck);

        Player player1 = new Player("A", match);
        Player player2 = new Player("B", match);
        Player player3 = new Player("C", match);
        Player player4 = new Player("D", match);

        try {
            match.addPlayer(player1);
            match.addPlayer(player2);
            match.addPlayer(player3);
            match.addPlayer(player4);
        } catch (WrongStateException e) {
            throw new RuntimeException(e);
        }

        PlayableCard card = MatchTest.generateRandomResourceCard();
        Objective objective = MatchTest.generateRandomObjective();
        Pair<Integer, Integer> pos = new Pair<>(1, 1);
        Player NotCurrPlayer = match.getPlayers().get(1); // the current player is the one at 0th position

        // Verify that drawInitialCard() throws a WrongTurnException when the caller is not the match current player
        try {
            NotCurrPlayer.drawInitialCard();
            // An exception should be thrown here
            fail("WrongTurnException wasn't thrown but should have been");
        }
        catch (WrongTurnException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify that chooseInitialCardSide() throws a WrongTurnException when the caller is not the match current player
        try {
            NotCurrPlayer.chooseInitialCardSide(Side.FRONT);
            // An exception should be thrown here
            fail("WrongTurnException wasn't thrown but should have been");
        }
        catch (WrongTurnException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify that drawSecretObjectives() throws a WrongTurnException when the caller is not the match current player
        try {
            NotCurrPlayer.drawSecretObjectives();
            // An exception should be thrown here
            fail("WrongTurnException wasn't thrown but should have been");
        }
        catch (WrongTurnException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify that chooseSecretObjective() throws a WrongTurnException when the caller is not the match current player
        try {
            NotCurrPlayer.chooseSecretObjective(objective);
            // An exception should be thrown here
            fail("WrongTurnException wasn't thrown but should have been");
        }
        catch (WrongTurnException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify that drawCard() throws a WrongTurnException when the caller is not the match current player
        try {
            NotCurrPlayer.drawCard(DrawSource.FIRST_VISIBLE);
            // An exception should be thrown here
            fail("WrongTurnException wasn't thrown but should have been");
        }
        catch (WrongTurnException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Verify that playCard() throws a WrongTurnException when the caller is not the match current player
        try {
            NotCurrPlayer.playCard(pos, card, Side.FRONT);
            // An exception should be thrown here
            fail("WrongTurnException wasn't thrown but should have been");
        }
        catch (WrongTurnException e) {}
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
