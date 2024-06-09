# RMI Protocol communication
This document is intended to describe how a client must be implemented and behave to be compatible with the RMI server of this application.

## Outline
- [General notes](#general-notes)
- [Obtain remote server object](#obtain-remote-server-object)
- [Obtain remote controller object](#obtain-remote-controller-object)
- [Implement a commont interface](#implement-a-common-interface)

## Content

## General notes
- Keep in mind that this application has been designed and implemented with a distributed MVC architecture in mind, in particular having the view as the
only remote part of the architecture. It goes without saying that a client hereby is considered to be a view too.
- As usual on a network communication, the client must know the socket on which the server listens (i.e. IP and port).
- It won't be described here how to access a RMI registry, since it's inherent to any RMI connection, not this specific protocol.

### Obtain remote server object
The first step to be able to create a functioning ```Client``` class is to add to its ```main``` method everything needed to listen
to the server remote RMI registry. The server will export itself to the registry as an object that implements the interface ```ServerRMIInterface```, then the received object must be casted to this interface:
```Java
/**
 * RMI interface used to declare all and only the methods callable on a remote Server instance implementing this
 * interface by a client.
 * For security reasons, each method doesn't expose to the receiving view important objects (e.g. Match), but
 * rather values representing them (e.g. Match unique name).
 */
public interface ServerRMIInterface extends Remote {
    /**
     * Returns the unique names of the available matches (those not full yet).
     * @return The list of Match which are not full yet.
     * @throws RemoteException If the remote server is considered not to be reachable any more and cannot return as usual
     */
    List<String> getJoinableMatches() throws RemoteException;

    /**
     * Lets the calling view join on a match with the given player nickname, if possible; gives back to the client
     * an instance of its PlayerControllerRMI, to start communicating through it with the match.
     * @param matchName The unique name of the match to join to
     * @param nickname The chosen player nickname
     * @return An instance of PlayerControllerRMI, used exclusively by the calling view
     * @throws RemoteException If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException If the chosen match is either already full or doesn't exist
     * @throws AlreadyUsedNicknameException If the given nickname is already taken
     * @throws WrongStateException If the match is in a state during which doesn't allow players to join any more
     */
    PlayerControllerRMI joinMatch(String matchName, String nickname) throws RemoteException, ChosenMatchException, AlreadyUsedNicknameException, WrongStateException;

    /**
     * Lets the calling view create a new match.
     * @param matchName The unique name to give to the new match
     * @param maxPlayers The maximum number of player allowed on the new match
     * @throws RemoteException If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException If the given match name is already taken
     */
    void createMatch(String matchName, int maxPlayers) throws RemoteException, ChosenMatchException;
}
```

### Obtain remote controller object
Having received the ```ServerRMIInterface``` object allows the client to request a controller, it can be done calling the ```joinMatch(...)``` method. The received object is obviously remote too and must be casted to a common RMI interface (as usual with RMI): the ```PlayerControllerRMIInterface```:
```Java
/**
 * RMI interface used to declare all and only the methods callable on a remote PlayerControllerRMI instance implementing
 * this interface. Since it's a remote interface, all the methods here defined are meant to notify the occurrence of an
 * event to the remote object. Given this, all methods also contain some parameters specific to the happened event (e.g.
 * a user clicked the button to play a card on the GUI view, then {@link #playCard(Pair, PlayableCard, Side)}) is called.
 */
public interface PlayerControllerRMIInterface extends Remote {
    /**
     * Register the given view as the one attached to the remote PlayerControllerRMI instance; if it has already been
     * called, it won't do anything, since it's call is allowed once per PlayerController object.
     * It's used by a remote View having this class object to send itself over RMI to the PlayerControllerRMI
     * instance.
     * @param view The View to save in the PlayerController internal state
     */
    void registerView(ViewRMIInterface view) throws RemoteException;

    /**
     * Draws an initial card for the player.
     * @throws WrongStateException If the current match state doesn't allow drawing an initial card
     * @throws WrongTurnException If the current turn it's not the one of this player
     */
    void drawInitialCard() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen initial card side.
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     * @throws WrongStateException If the current match state doesn't allow setting the initial card side
     * @throws WrongTurnException If the current turn it's not the one of this player
     */
    void chooseInitialCardSide(Side side) throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Draws two secret objectives.
     * @throws WrongStateException If the current match state doesn't allow drawing secret objectives
     * @throws WrongTurnException If the current turn it's not the one of this player
     */
    void drawSecretObjectives() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen secret objective.
     * @param objective The chosen objective
     * @throws WrongStateException If the current match state doesn't allow choosing a secret objective
     * @throws WrongTurnException If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen objective is not one of the two drawn ones using {@link #drawSecretObjectives()}
     */
    void chooseSecretObjective(Objective objective) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Plays a card.
     * @param coords The coordinates on which to place the card
     * @param card The PlayableCard to play
     * @param side The side on which to play the chosen card
     * @throws WrongStateException If the current match state doesn't allow playing cards
     * @throws WrongTurnException If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen card is not one of those in the player's current hand
     */
    void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Draws a card.
     * @param source The drawing source to draw the card from
     * @throws HandException If the player already has a full hand of cards (three cards)
     * @throws WrongStateException If the current match state doesn't allow drawing cards
     * @throws WrongTurnException If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen DrawSource doesn't have any card left (i.e. it's empty)
     */
    void drawCard(DrawSource source) throws RemoteException, HandException, WrongStateException, WrongTurnException, WrongChoiceException;
}
```
This object from now on will be the one used to communicate with the match, which is effectively located on the server machine, thus the
server remote object won't be used anymore.

### Register the client on the controller
To allow the just obtained controller to notify the client, the client itself must call the ```registerView(view)``` method on the controller passing itself as argument. The controller will now notify the client about occured events on the match.

### Implement a common interface
To let the controller call methods on the client, the client must also implement an interface known both to itself and the controller.
This interface is ```ViewRMIInterface```:
```Java
/**
 * RMI interface used to declare all and only the methods callable on a remote view instance implementing this interface.
 * Since it's a remote interface, all the methods here defined are meant to notify the occurrence of an event to the remote
 * object. Given this, all methods also contain some parameters specific to the happened event.
 * For security reasons, each method doesn't expose to the receiving view important objects (e.g. Player), but
 * rather values representing them (e.g. Player's nickname).
 */
public interface ViewRMIInterface extends Remote {
    /**
     * Notifies that the match has just started.
     * Furthermore, gives to the receiving object all the information (parameters) needed to show to the current match
     * state.
     * @param playersNicknamesAndPawns Map that matches each pawn color to the corresponding player's nickname
     * @param playersHands Map that matches each player's nickname to the corresponding List of cards in the hand
     * @param visibleObjectives Pair of objectives visible to all players
     * @param visiblePlayableCards Map having as values the visible common cards (the keys are just indexes).
     * @param decksTopReigns Pair of reign symbols representing the two visible reigns symbols on top of the two decks;
     *                       the first one is the gold deck one, the second one the resource deck one
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void matchStarted(Map<Color, String> playersNicknamesAndPawns, Map<String, List<PlayableCard>> playersHands, Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards, Pair<Symbol, Symbol> decksTopReigns) throws RemoteException;

    /**
     * Gives to the remote object an initial card to show it in the view.
     * @param initialCard Initial card to give
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void giveInitialCard(InitialCard initialCard) throws RemoteException;

    /**
     * Gives to the remote object a pair of secret objectives to show them in the view and to choose one from them.
     * @param secretObjectives Pair of secret objectives to give
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void giveSecretObjectives(Pair<Objective, Objective> secretObjectives) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn its initial card.
     * @param someoneNickname The nickname of the player who has drawn the card
     * @param card The card drawn
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneDrewInitialCard(String someoneNickname, InitialCard card) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has decided (then set) its initial card side.
     * @param someoneNickname The nickname of the player who has set side
     * @param side The chosen side
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneSetInitialSide(String someoneNickname, Side side) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn a pair of secret objectives.
     * Mind that the objectives are not passed as arguments, since they are secret to all players but the one receiving
     * them. The one meant to receive them receives this message too but obtain the objectives through the
     * giveSecretObjective() method.
     * @param someoneNickname The nickname of the player who has drawn the card
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneDrewSecretObjective(String someoneNickname) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has chosen theirs secret objective.
     * @param someoneNickname The nickname of the player who has chosen theirs secret objective
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneChoseSecretObjective(String someoneNickname) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has played a card.
     * @param someoneNickname The nickname of the player who has played a card
     * @param coords The coordinates where the card has been placed as a Pair of int
     * @param card The card that has been played
     * @param side The side on which the card has been played
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someonePlayedCard(String someoneNickname, Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException;

    /**
     * Notifies that someone (it may or may not be the receiving View instance) has drawn a card.
     * @param someoneNickname The nickname of the player who has played a card
     * @param source The DrawSource from which the card has been drawn
     * @param card The card that has been drawn
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneDrewCard(String someoneNickname, DrawSource source, Card card) throws RemoteException;

    /**
     * Notifies that the match has just started.
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void matchFinished() throws RemoteException;
}
```
Each method must be implemented keeping in mind that it represent the action to be triggered when the corresponding event occurs.