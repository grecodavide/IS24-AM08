package it.polimi.ingsw.client.network;

import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

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
     * @throws RemoteException If the remote object is considered not to be reachable anymore and cannot return as usual
     */
    void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException;

    /**
     * Notifies that the match has resumed.
     * Furthermore, gives to the receiving object all the information (parameters) needed to restore to the current match
     * state.
     *
     * @param playersUsernamesAndPawns Map that matches each pawn color to the corresponding player's username
     * @param playersHands             Map that matches each player's username to the corresponding List of cards in the hand
     * @param visibleObjectives        Pair of objectives visible to all players
     * @param visiblePlayableCards     Map having as values the visible common cards (the keys are just indexes).
     * @param decksTopReigns           Pair of reign symbols representing the two visible reigns symbols on top of the two decks;
     *                                 the first one is the gold deck one, the second one the resource deck one
     * @param secretObjective          Secret objective of the current player
     * @param availableResources       Available resources of all the players
     * @param placedCards              Placed cards of all the players
     * @param playerPoints             Points of all the players
     * @param currentPlayer            The current player
     * @param drawPhase                If the match is resumed in draw phase
     * @throws RemoteException If the remote object is considered not to be reachable anymore and cannot return as usual
     */
    void matchResumed(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
                      Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
                      Pair<Symbol, Symbol> decksTopReigns, Objective secretObjective, Map<String, Map<Symbol, Integer>> availableResources,
                      Map<String, Map<Pair<Integer, Integer>, PlacedCard>> placedCards, Map<String, Integer> playerPoints, String currentPlayer, boolean drawPhase) throws RemoteException;

    /**
     * Gives to the receiving graphical view (the client) a list of the currently available matches.
     *
     * @param availableMatches The available matches
     * @throws RemoteException If the remote object is considered not to be reachable anymore and cannot return as usual
     */
    void receiveAvailableMatches(List<AvailableMatch> availableMatches) throws RemoteException;

    /**
     * Gives to the receiving graphical view (the client) its initial card.
     *
     * @param initialCard The initial card to be given
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
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
     * @param someoneUsername The username of the player who has set the initial card side
     * @param side            The chosen side
     * @param availableResources The current available resources of the player having someoneUsername as username
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneSetInitialSide(String someoneUsername, Side side, Map<Symbol, Integer> availableResources) throws RemoteException;

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
     * @param points          The points of the player who played a card
     * @param availableResources The current available resources of the player who played a card
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side,
                           int points, Map<Symbol, Integer> availableResources) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn a card.
     *
     * @param someoneUsername The username of the player who has played a card
     * @param source          The DrawSource from which the card has been drawn
     * @param card            The card that has been drawn
     * @param replacementCard The card that replaced the drawn one
     * @param deckTopReigns   The decks top reigns
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard, Pair<Symbol, Symbol> deckTopReigns) throws RemoteException;

    /**
     * Notifies that a player has joined the match.
     *
     * @param someoneUsername The username of the player who has joined
     * @param joinedPlayers The players currently in the match
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneJoined(String someoneUsername, List<String> joinedPlayers) throws RemoteException;

    /**
     * Notifies that a player has quit from the match.
     *
     * @param someoneUsername The username of the player who has quit
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneQuit(String someoneUsername) throws RemoteException;

    /**
     * Notifies that the match has just finished.
     *
     * @param ranking The match final ranking
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void matchFinished(List<LeaderboardEntry> ranking) throws RemoteException;

    /**
     * Notifies that a new message in the global chat is sent
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException;

    /**
     * Notifies that a new private message is sent in private chat to the current user
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException;

}
