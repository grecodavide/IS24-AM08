# RMI Protocol communication
This document is intended to describe how a client must be implemented and behave to be compatible with the RMI server of this application.

## Outline
- [General notes](#general-notes)
- [Obtain remote server object](#obtain-remote-server-object)
- [Obtain remote controller object](#obtain-remote-controller-object)
- [Implement a common interface](#implement-a-common-interface)

## Content

## General notes
- Keep in mind that this application has been designed and implemented with a distributed MVC architecture in mind, in particular having the view as the
only remote part of the architecture. It goes without saying that a client hereby is considered to be a view too.
- As usual on a network communication, the client must know the socket on which the server listens (i.e. IP and port).
- It won't be described here how to access an RMI registry, since it's inherent to any RMI connection, not this specific protocol.

### Obtain remote server object
The first step to be able to create a functioning ```Client``` class is to add to its ```main``` method everything needed to listen
to the server remote RMI registry. The server will export itself to the registry as an object that implements the interface ```ServerRMIInterface```, then the received object must be cast to this interface:
```Java
/**
 * RMI interface used to declare all and only the methods callable on a remote Server instance implementing this
 * interface by a client.
 * For security reasons, each method doesn't expose to the receiving view important objects (e.g. Match), but
 * rather values representing them (e.g. Match unique name).
 */
public interface ServerRMIInterface extends Remote {
    /**
     * Returns the available matches (those not full yet) as {@link AvailableMatch} instances.
     *
     * @return The list of Match which are not full yet.
     * @throws RemoteException If the remote server is considered not to be reachable any more and cannot return as usual
     */
    List<AvailableMatch> getJoinableMatches() throws RemoteException;

    /**
     * Lets the calling view join on a match with the given player username, if possible; gives back to the client
     * an instance of its PlayerControllerRMI, to start communicating through it with the match.
     *
     * @param matchName The unique name of the match to join to
     * @param username  The chosen player username
     * @return An instance of PlayerControllerRMI, used exclusively by the calling view
     * @throws RemoteException              If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException         If the chosen match is either already full or doesn't exist
     * @throws AlreadyUsedUsernameException If the given username is already taken
     * @throws WrongStateException          If the match is in a state during which doesn't allow players to join any more
     * @throws WrongNameException           If the name is not valid
     */
    PlayerControllerRMIInterface joinMatch(String matchName, String username) throws RemoteException, ChosenMatchException, AlreadyUsedUsernameException, WrongStateException, WrongNameException;

    /**
     * Lets the calling view create a new match.
     *
     * @param matchName  The unique name to give to the new match
     * @param maxPlayers The maximum number of player allowed on the new match
     * @throws RemoteException      If the remote server is considered not to be reachable any more and cannot return as usual
     * @throws ChosenMatchException If the given match name is already taken
     */
    void createMatch(String matchName, int maxPlayers) throws RemoteException, ChosenMatchException, WrongNameException;

    /**
     * Pings the server in order to perceive if the connection is still alive and working.
     *
     * @throws RemoteException If the connection to this class instance is not alive anymore
     */
    boolean ping() throws RemoteException;
}
```

### Obtain remote controller object
Having received the ```ServerRMIInterface``` object allows the client to request a controller, it can be done calling the ```joinMatch(...)``` method. The received object is obviously remote too and must be cast to a common RMI interface (as usual with RMI): the ```PlayerControllerRMIInterface```:
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
     *
     * @param view The View to save in the PlayerController internal state
     */
    void registerView(RemoteViewInterface view) throws RemoteException, ChosenMatchException, WrongStateException, AlreadyUsedUsernameException, WrongNameException;

    /**
     * Draws an initial card for the player.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing an initial card
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    void drawInitialCard() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     * @throws WrongStateException If the current match state doesn't allow setting the initial card side
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    void chooseInitialCardSide(Side side) throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Draws two secret objectives.
     *
     * @throws WrongStateException If the current match state doesn't allow drawing secret objectives
     * @throws WrongTurnException  If the current turn it's not the one of this player
     */
    void drawSecretObjectives() throws RemoteException, WrongStateException, WrongTurnException;

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     * @throws WrongStateException  If the current match state doesn't allow choosing a secret objective
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen objective is not one of the two drawn ones using {@link #drawSecretObjectives()}
     */
    void chooseSecretObjective(Objective objective) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Plays a card.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     * @throws WrongStateException  If the current match state doesn't allow playing cards
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen card is not one of those in the player's current hand
     */
    void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws RemoteException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     * @throws HandException        If the player already has a full hand of cards (three cards)
     * @throws WrongStateException  If the current match state doesn't allow drawing cards
     * @throws WrongTurnException   If the current turn it's not the one of this player
     * @throws WrongChoiceException If the chosen DrawSource doesn't have any card left (i.e. it's empty)
     */
    void drawCard(DrawSource source) throws RemoteException, HandException, WrongStateException, WrongTurnException, WrongChoiceException;

    /**
     * Sends a text to all the players in the match.
     *
     * @param text The text to be sent
     */
    void sendBroadcastText(String text) throws RemoteException;

    /**
     * Sends a text just to a specific player in the match.
     *
     * @param recipient The username of the recipient
     * @param text The text to be sent to the recipient
     */
    void sendPrivateText(String recipient, String text) throws RemoteException;
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
     * Furthermore, gives to the receiving object all the information (parameters) needed to show to the current match
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
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points, Map<Symbol, Integer> availableResources) throws RemoteException;

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
     * Notifies that someone has joined the match.
     *
     * @param someoneUsername The username of the player that joined
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneJoined(String someoneUsername, List<String> joinedPlayers) throws RemoteException;

    /**
     * Notifies that someone has quit the match.
     *
     * @param someoneUsername The username of the player that quit
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneQuit(String someoneUsername) throws RemoteException;

    /**
     * Notifies that the match has just started.
     *
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void matchFinished(List<LeaderboardEntry> ranking) throws RemoteException;

    /**
     * Notifies that a new message in the global chat has been received.
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneSentBroadcastText(String someoneUsername, String text) throws RemoteException;

    /**
     * Notifies that a new private message has been sent to the current user.
     *
     * @param someoneUsername Username of the user that sent the message
     * @param text            Content of the message
     * @throws RemoteException If the remote object is considered not to be reachable any more and cannot return as usual
     */
    void someoneSentPrivateText(String someoneUsername, String text) throws RemoteException;
}
```
Each method must be implemented keeping in mind that it represent the action to be triggered when the corresponding event occurs.
