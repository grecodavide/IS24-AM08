package it.polimi.ingsw.client.network;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

/**
 * Network interface used to declare all and only the methods callable on a remote view instance implementing this interface or
 * by message listener for TCP.
 * Since it's a remote interface, all the methods here defined are meant to notify the occurrence of an event to the remote
 * object. Given this, all methods also contain some parameters specific to the happened event.
 * For security reasons, each method doesn't expose to the receiving view important objects (e.g. Player), but
 * rather values representing them (e.g. Player's username).
 */
public interface RemoteViewInterface extends Remote {
    /**
     * Updates the list of players in the lobby before the start of the match
     *
     * @param playersUsernames list of usernames
     */
    void giveLobbyInfo(List<String> playersUsernames) throws RemoteException;

    /**
     * Notifies that the match has just started.
     * Furthermore, gives to the receiving object all the information (parameters) needed to show to the current match
     * state.
     *
     * @param playersUsernamesAndPawns Map that matches each pawn color to the corresponding player's username
     * @param playersHands             Map that matches each player's username to the corresponding List of cards in the hand
     * @param visibleObjectives        Pair of objectives visible to all players
     * @param visiblePlayableCards     Map having as values the visible common cards (the keys are just indexes).
     * @param decksTopReigns           Pair of reign symbols representing the two visible reigns symbols on top of the two decks;
     *                                 the first one is the gold deck one, the second one the resource deck one
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException;

    
    /**
     * Gives the graphical view a list of available matches
     * 
     * @param availableMatchs The available matches
     */
    void receiveAvailableMatches(List<AvailableMatch> availableMatchs) throws RemoteException;

    void giveInitialCard(InitialCard initialCard) throws RemoteException;

    /**
     * Gives to the remote object a pair of secret objectives to show them in the view and to choose one from them.
     *
     * @param secretObjectives Pair of secret objectives to give
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn its initial card.
     *
     * @param someoneUsername The username of the player who has drawn the card
     * @param card            The card drawn
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneDrewInitialCard(String someoneUsername, InitialCard card) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has decided (then set) its initial card side.
     *
     * @param someoneUsername The username of the player who has set side
     * @param side            The chosen side
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneSetInitialSide(String someoneUsername, Side side) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn a pair of secret objectives.
     * Mind that the objectives are not passed as arguments, since they are secret to all players but the one receiving
     * them. The one meant to receive them receives this message too but obtain the objectives through the
     * giveSecretObjective() method.
     *
     * @param someoneUsername The username of the player who has drawn the card
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneDrewSecretObjective(String someoneUsername) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has chosen theirs secret objective.
     *
     * @param someoneUsername The username of the player who has chosen theirs secret objective
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneChoseSecretObjective(String someoneUsername) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has played a card.
     *
     * @param someoneUsername The username of the player who has played a card
     * @param coords          The coordinates where the card has been placed as a Pair of int
     * @param card            The card that has been played
     * @param side            The side on which the card has been played
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn a card.
     *
     * @param someoneUsername The username of the player who has played a card
     * @param source          The DrawSource from which the card has been drawn
     * @param card            The card that has been drawn
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard, Symbol replacementCardReign) throws RemoteException;

    /**
     * @param someoneUsername
     * @throws RemoteException
     */
    void someoneJoined(String someoneUsername) throws RemoteException;

    /**
     * @param someoneUsername
     * @throws RemoteException
     */
    void someoneQuit(String someoneUsername) throws RemoteException;

    /**
     * Notifies that the match has just started.
     *
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void matchFinished(List<LeaderboardEntry> ranking) throws RemoteException;

    /**
     * Notifies that a new message in the global chat is sent
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     */
    void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException;

    /**
     * Notifies that a new private message is sent in private chat to the current user
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     */
    void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException;

}
