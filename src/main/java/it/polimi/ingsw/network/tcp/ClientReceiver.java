package it.polimi.ingsw.network.tcp;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import it.polimi.ingsw.client.network.NetworkViewTCP;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.network.messages.errors.ErrorMessage;
import it.polimi.ingsw.network.messages.responses.*;
import it.polimi.ingsw.utils.CardsManager;
import it.polimi.ingsw.utils.Pair;

/**
 * ClientSender
 */

public class ClientReceiver implements Runnable {
    private NetworkViewTCP networkView;
    private Socket socket;
    private IOHandler io;
    private Map<Integer, InitialCard> initialCards;
    private Map<Integer, ResourceCard> resourceCards;
    private Map<Integer, GoldCard> goldCards;
    private Map<Integer, Objective> objectives;

    public ClientReceiver(NetworkViewTCP networkView, Socket socket) throws IOException {
        this.networkView = networkView;
        this.socket = socket;
        this.io = new IOHandler(socket);
        this.io = networkView.getIO();

        CardsManager cardsManager = CardsManager.getInstance();
        this.initialCards = cardsManager.getInitialCards();
        this.resourceCards = cardsManager.getResourceCards();
        this.goldCards = cardsManager.getGoldCards();
        this.objectives = cardsManager.getObjectives();
    }

    private PlayableCard getPlayable(Integer cardID) {
        PlayableCard card = this.resourceCards.get(cardID);
        if (card == null) {
            return this.goldCards.get(cardID);
        }
        return card;
    }

    private void parseMessage(String message) {
        try {
            ResponseMessage response = (ResponseMessage) io.stringToMsg(message);
            String username = response.getUsername();
            switch (response) {
                case AvailableMatchesMessage msg:
                    this.networkView.receiveAvailableMatches(msg.getMatches());
                    break;
                case MatchStartedMessage msg:
                    Map<String, List<PlayableCard>> hands = new HashMap<>();
                    msg.getPlayerHands().forEach((player, hand) -> hands.put(player,
                            List.of(hand).stream().map(card -> this.getPlayable(card)).collect(Collectors.toList())));

                    Pair<Objective, Objective> objectives = new Pair<Objective, Objective>(
                            this.objectives.get(msg.getVisibleObjectives()[0]), this.objectives.get(msg.getVisibleObjectives()[1]));

                    Map<DrawSource, PlayableCard> visibles = new HashMap<>();
                    msg.getVisibleCards().forEach((source, card) -> visibles.put(source, this.getPlayable(card)));

                    Pair<Symbol, Symbol> decksTop = new Pair<Symbol, Symbol>(msg.getVisibleDeckReigns()[0], msg.getVisibleDeckReigns()[1]);

                    this.networkView.matchStarted(msg.getPlayerPawnColors(), hands, objectives, visibles, decksTop);
                    break;

                case SomeoneDrewInitialCardMessage msg:
                    if (username.equals(this.networkView.getUsername())) {
                        this.networkView.giveInitialCard(this.initialCards.get(msg.getInitialCardID()));
                    } else {
                        this.networkView.someoneDrewInitialCard(username, this.initialCards.get(msg.getInitialCardID()));
                    }
                    break;
                case SomeoneDrewSecretObjectivesMessage msg:
                    if (username.equals(this.networkView.getUsername())) {
                        Pair<Objective, Objective> objs =
                                new Pair<>(this.objectives.get(msg.getFirstID()), this.objectives.get(msg.getSecondID()));
                        this.networkView.giveSecretObjectives(objs);
                    } else {
                        this.networkView.someoneDrewSecretObjective(username);
                    }
                    break;
                case SomeoneSetInitialSideMessage msg:
                    this.networkView.someoneSetInitialSide(username, msg.getSide());
                    break;
                case SomeoneChoseSecretObjectiveMessage msg:
                    this.networkView.someoneChoseSecretObjective(username);
                    break;
                case SomeonePlayedCardMessage msg:
                    Pair<Integer, Integer> coords = new Pair<Integer, Integer>(msg.getX(), msg.getY());
                    this.networkView.someonePlayedCard(username, coords, this.getPlayable(msg.getCardID()), msg.getSide(), msg.getPoints(),
                            msg.getAvailableResources());
                    break;
                case SomeoneDrewCardMessage msg:
                    this.networkView.someoneDrewCard(username, msg.getDrawSource(), this.getPlayable(msg.getCardID()),
                            this.getPlayable(msg.getReplacementCardID()), msg.getReplacementCardReign());
                    break;
                case SomeoneJoinedMessage msg:
                    this.networkView.someoneJoined(username);
                    break;
                case SomeoneQuitMessage msg:
                    this.networkView.someoneQuit(username);
                    break;
                case MatchFinishedMessage msg:
                    this.networkView.matchFinished(msg.getRanking());
                    break;
                case SomeoneSentBroadcastTextMessage msg:
                    this.networkView.someoneSentBroadcastText(username, msg.getText());
                    break;
                case SomeoneSentPrivateTextMessage msg:
                    this.networkView.someoneSentPrivateText(username, msg.getText());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            this.sendError(message);
        }

    }

    private void sendError(String message) {
        try {
            ErrorMessage msg = (ErrorMessage)this.io.stringToMsg(message);
            Exception exception = new Exception(msg.getMessage());
            this.networkView.notifyError(exception);
        } catch (Exception e) {
            // Nothing to do, received an invalid object
        }
    }

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
                // TODO: something bad happened
            }
        }

        System.out.println("cacca");
    }

}
