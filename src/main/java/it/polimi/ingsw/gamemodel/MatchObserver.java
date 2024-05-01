package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.utils.*;

/**
 * Interface to be implemented by any class that wants to be an observer of {@link Match}, so wants to be able to
 * get notified when an event occurs in a match (to which it's subscribed).
 */
public interface MatchObserver {
    /**
     * Notifies that the match has just started.
     */
    void matchStarted();

    /**
     * Notifies that someone has joined the match.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The Player instance that has joined
     */
    void someoneJoined(Player someone);

    /**
     * Notifies that someone has quit from the match.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The Player instance that has quit
     */
    void someoneQuit(Player someone);

    /**
     * Notifies that someone has drawn its initial card.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The player instance that has drawn the card
     * @param card The initial card that has been drawn
     */
    void someoneDrewInitialCard(Player someone, InitialCard card);

    /**
     * Notifies that someone has chosen its initial card side.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The player instance that has chosen the side
     * @param side The chosen initial card side
     */
    void someoneSetInitialSide(Player someone, Side side);

    /**
     * Notifies that someone has drawn two secret objectives.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The player instance that has drawn the objectives
     * @param objectives The two proposed objectives
     */
    void someoneDrewSecretObjective(Player someone, Pair<Objective, Objective> objectives);

    /**
     * Notifies that someone has chosen the secret objective.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The player instance that has chosen the secret objective
     * @param objective The chosen secret objective
     */
    void someoneChoseSecretObjective(Player someone, Objective objective);

    /**
     * Notifies that someone has played a card.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The Player instance that has played a card
     * @param coords The coordinates on which the card has been placed
     * @param card The PlayableCard that has been played
     * @param side The side on which the card has been placed
     */
    void someonePlayedCard(Player someone, Pair<Integer, Integer> coords, PlayableCard card, Side side);

    /**
     * Notifies that someone has drawn a card.
     * The replacement card is the one that has taken the place of the drawn one, it's needed since observers have to
     * know the reign of the new card on top of the decks.
     * Note that Match calls this method on all MatchObservers instance subscribed to itself, then
     * even the MatchObserver causing this event gets notified.
     * @param someone The Player instance that has drawn a card
     * @param source The drawing source from which the card has been drawn
     * @param card The card that has been drawn
     * @param replacementCard The card that has replaced the drawn card
     */
    void someoneDrewCard(Player someone, DrawSource source, PlayableCard card, PlayableCard replacementCard);

    /**
     * Notifies that the match has just finished.
     */
    void matchFinished();
}
