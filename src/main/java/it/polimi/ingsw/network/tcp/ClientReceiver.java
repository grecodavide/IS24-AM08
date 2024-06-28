package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.client.network.NetworkHandlerTCP;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.*;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.PlacedCardRecord;

/**
 * Receives messages from server to client
 */
public class ClientReceiver implements Runnable {
    private NetworkHandlerTCP networkHandler;
    private Socket socket;
    private IOHandler io;
    private Map<Integer, InitialCard> initialCards;
    private Map<Integer, ResourceCard> resourceCards;
    private Map<Integer, GoldCard> goldCards;
    private Map<Integer, Objective> objectives;


    /**
     * Class constructor.
     * 
     * @param networkHandler The network view that should call the {@link GraphicalView} methods
     * @param socket The socket opened
     * 
     * @throws IOException If there was an error with the socket's streams
     */
    public ClientReceiver(NetworkHandlerTCP networkHandler, Socket socket) throws IOException {
        this.networkHandler = networkHandler;
        this.socket = socket;
        this.io = new IOHandler(socket);
        this.io = networkHandler.getIO();

        CardsManager cardsManager = CardsManager.getInstance();
        this.initialCards = cardsManager.getInitialCards();
        this.resourceCards = cardsManager.getResourceCards();
        this.goldCards = cardsManager.getGoldCards();
        this.objectives = cardsManager.getObjectives();
    }


    /**
     * Get a playable card by its ID.
     * 
     * @param cardID The card ID
     * 
     * @return The playable card
     */
    private PlayableCard getPlayable(Integer cardID) {
        PlayableCard card = this.resourceCards.get(cardID);
        if (card == null) {
            return this.goldCards.get(cardID);
        }
        return card;
    }

    /**
     * Parses a map from coordinates to {@link PlacedCard} to a map from coordinates to
     * {@link PlacedCardRecord}.
     * 
     * @param board The map from coordinates to {@link PlacedCard}
     * 
     * @return The map from coordinates to {@link PlacedCardRecord}
     */
    private Map<Pair<Integer, Integer>, PlacedCard> getPlacedMap(
            Map<Integer, PlacedCardRecord> board) {
        Map<Pair<Integer, Integer>, PlacedCard> result = new HashMap<>();

        board.forEach((turn, placedCardRecord) -> {
            if (placedCardRecord.x().equals(0) && placedCardRecord.y().equals(0)) {
                result.put(new Pair<Integer, Integer>(placedCardRecord.x(), placedCardRecord.y()),
                        new PlacedCard(this.initialCards.get(placedCardRecord.cardID()),
                                placedCardRecord.side(), turn));
            } else {
                result.put(new Pair<Integer, Integer>(placedCardRecord.x(), placedCardRecord.y()),
                        new PlacedCard(this.getPlayable(placedCardRecord.cardID()),
                                placedCardRecord.side(), turn));
            }
        });

        return result;
    }


    /**
     * Parses the message and resumes the match.
     * 
     * @param msg The message containing the match status
     * 
     * @throws IOException if there was a problem with the socket stream
     */
    private void resumeMatch(MatchResumedMessage msg) throws IOException {
        Map<String, Color> playersUsernamesAndPawns = new HashMap<>();
        Map<String, List<PlayableCard>> playersHands = new HashMap<>();
        Pair<Objective, Objective> visibleObjectives;
        Map<DrawSource, PlayableCard> visiblePlayableCards = new HashMap<>();
        Pair<Symbol, Symbol> decksTopReigns;
        Objective secretObjective;
        Map<String, Map<Symbol, Integer>> availableResources = new HashMap<>();
        Map<String, Map<Pair<Integer, Integer>, PlacedCard>> placedCards = new HashMap<>();
        Map<String, Integer> playerPoints = new HashMap<>();
        String currentPlayer;
        boolean drawPhase;

        playersUsernamesAndPawns = msg.getPlayersUsernamesAndPawns();
        playersHands = msg.getPlayersHands().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().stream()
                        .map(cardID -> this.getPlayable(cardID)).collect(Collectors.toList())));
        Pair<Integer, Integer> visibleObjectivesID = msg.getVisibleObjectives();
        visibleObjectives =
                new Pair<Objective, Objective>(this.objectives.get(visibleObjectivesID.first()),
                        this.objectives.get(visibleObjectivesID.second()));
        visiblePlayableCards = msg.getVisiblePlayableCards().entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> this.getPlayable(entry.getValue())));
        decksTopReigns = msg.getDecksTopReigns();
        secretObjective = this.objectives.get(msg.getSecretObjective());
        availableResources = msg.getAvailableResources();
        msg.getPlacedCards()
                .forEach((player, board) -> placedCards.put(player, this.getPlacedMap(board)));
        playerPoints = msg.getPlayerPoints();
        currentPlayer = msg.getCurrentPlayer();
        drawPhase = msg.isDrawPhase();

        this.networkHandler.matchResumed(playersUsernamesAndPawns, playersHands, visibleObjectives,
                visiblePlayableCards, decksTopReigns, secretObjective, availableResources,
                placedCards, playerPoints, currentPlayer, drawPhase);
    }

    /**
     * Parses a message and calls the corresponding
     * {@link it.polimi.ingsw.client.network.NetworkHandler}'s view.
     * 
     * @param message The message to be parsed
     */
    private void parseMessage(String message) {
        try {
            if (message.equals("pong")) {
                this.networkHandler.pong();
            }
            ResponseMessage response = (ResponseMessage) io.stringToMsg(message);
            String username = response.getUsername();
            switch (response) {
                case AvailableMatchesMessage msg:
                    this.networkHandler.receiveAvailableMatches(msg.getMatches());
                    break;
                case MatchResumedMessage msg:
                    this.resumeMatch(msg);
                    break;
                case MatchStartedMessage msg:
                    Map<String, List<PlayableCard>> hands = new HashMap<>();
                    msg.getPlayerHands()
                            .forEach((player, hand) -> hands.put(player,
                                    List.of(hand).stream().map(card -> this.getPlayable(card))
                                            .collect(Collectors.toList())));

                    Pair<Objective, Objective> objectives = new Pair<Objective, Objective>(
                            this.objectives.get(msg.getVisibleObjectives()[0]),
                            this.objectives.get(msg.getVisibleObjectives()[1]));

                    Map<DrawSource, PlayableCard> visibles = new HashMap<>();
                    msg.getVisibleCards().forEach(
                            (source, card) -> visibles.put(source, this.getPlayable(card)));

                    Pair<Symbol, Symbol> decksTop = new Pair<Symbol, Symbol>(
                            msg.getVisibleDeckReigns()[0], msg.getVisibleDeckReigns()[1]);

                    this.networkHandler.matchStarted(msg.getPlayerPawnColors(), hands, objectives,
                            visibles, decksTop);
                    break;
                case SomeoneDrewInitialCardMessage msg:
                    if (username.equals(this.networkHandler.getUsername())) {
                        this.networkHandler
                                .giveInitialCard(this.initialCards.get(msg.getInitialCardID()));
                    } else {
                        this.networkHandler.someoneDrewInitialCard(username,
                                this.initialCards.get(msg.getInitialCardID()));
                    }
                    break;
                case SomeoneDrewSecretObjectivesMessage msg:
                    if (username.equals(this.networkHandler.getUsername())) {
                        Pair<Objective, Objective> objs =
                                new Pair<>(this.objectives.get(msg.getFirstID()),
                                        this.objectives.get(msg.getSecondID()));
                        this.networkHandler.giveSecretObjectives(objs);
                    } else {
                        this.networkHandler.someoneDrewSecretObjective(username);
                    }
                    break;
                case SomeoneSetInitialSideMessage msg:
                    this.networkHandler.someoneSetInitialSide(username, msg.getSide(),
                            msg.getAvailableResources());
                    break;
                case SomeoneChoseSecretObjectiveMessage msg:
                    this.networkHandler.someoneChoseSecretObjective(username);
                    break;
                case SomeonePlayedCardMessage msg:
                    Pair<Integer, Integer> coords =
                            new Pair<Integer, Integer>(msg.getX(), msg.getY());
                    this.networkHandler.someonePlayedCard(username, coords,
                            this.getPlayable(msg.getCardID()), msg.getSide(), msg.getPoints(),
                            msg.getAvailableResources());
                    break;
                case SomeoneDrewCardMessage msg:
                    this.networkHandler.someoneDrewCard(username, msg.getDrawSource(),
                            this.getPlayable(msg.getCardID()),
                            this.getPlayable(msg.getReplacementCardID()), msg.getDeckTopReigns());
                    break;
                case SomeoneJoinedMessage msg:
                    this.networkHandler.someoneJoined(username, msg.getJoinedPlayers());
                    break;
                case SomeoneQuitMessage msg:
                    this.networkHandler.someoneQuit(username);
                    break;
                case MatchFinishedMessage msg:
                    this.networkHandler.matchFinished(msg.getRanking());
                    break;
                case SomeoneSentBroadcastTextMessage msg:
                    this.networkHandler.someoneSentBroadcastText(username, msg.getText());
                    break;
                case SomeoneSentPrivateTextMessage msg:
                    this.networkHandler.someoneSentPrivateText(username, msg.getText());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            this.sendError(message);
        }

    }


    /**
     * Sends an error to the server.
     * 
     * @param message The error message
     */
    private void sendError(String message) {
        try {
            ErrorMessage msg = (ErrorMessage) this.io.stringToMsg(message);
            Exception exception = new Exception(msg.getMessage());
            this.networkHandler.notifyError(exception);
        } catch (Exception e) {
            // Nothing to do, received an invalid object
        }
    }

    
    /**
     * Receives answers from the server and tries to parse it (in a new thread)
     */
    @Override
    public void run() {
        String message;
        while (!this.socket.isClosed() && this.socket.isConnected()) {
            try {
                message = this.io.readMsg();
                final String finalMessage = message;
                new Thread(() -> {
                    this.parseMessage(finalMessage);
                }).start();
            } catch (IOException | ClassNotFoundException e) {
                
            }
        }
    }
}
