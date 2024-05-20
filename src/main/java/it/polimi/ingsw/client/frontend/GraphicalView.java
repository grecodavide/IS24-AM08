package it.polimi.ingsw.client.frontend;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.client.network.NetworkView;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.LeaderboardEntry;
import it.polimi.ingsw.utils.Pair;

public abstract class GraphicalView {
    protected NetworkView networkView;
    protected int chosenPort;
    protected Map<String, ClientBoard> clientBoards;
    protected List<String> players; // ordered by turn
    protected String currentPlayer;
    protected Pair<Objective, Objective> visibleObjectives;
    protected Map<DrawSource, PlayableCard> visiblePlayableCards;
    protected Pair<Symbol, Symbol> decksTopReign;
    private boolean lastTurn = false;
    protected List<AvailableMatch> availableMatches;
    protected String username;

    protected void setUsername(String username) {
        this.username = username;
        this.networkView.setUsername(username);
    }

    public boolean isLastTurn() {
        return this.lastTurn;
    }

    /**
     * Displays the user an error, when received
     * 
     * @param cause What went wrong
     */
    public abstract void showError(String cause, Exception exception);

    /**
     * Sets the network interface to communicate
     *
     * @param networkView the interface to communicate
     */
    public void setNetworkInterface(NetworkView networkView) {
        this.networkView = networkView;
    }

    /**
     * Tries to create a match
     *
     * @param matchName The match's name
     */
    public void createMatch(String matchName, Integer maxPlayers) throws ChosenMatchException, RemoteException {
        this.networkView.createMatch(matchName, maxPlayers);
    }

    /**
     * Tries to join a match
     *
     * @param matchName the match's name
     */
    public void joinMatch(String matchName) throws ChosenMatchException, WrongStateException, AlreadyUsedUsernameException, RemoteException {
        this.networkView.joinMatch(matchName);
    }

    /**
     * Draws an initial card for the player.
     */
    public void drawInitialCard() throws WrongStateException, WrongTurnException, RemoteException {
        this.networkView.drawInitialCard();
    }

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     */
    public void chooseInitialCardSide(Side side) throws WrongStateException, WrongTurnException, RemoteException {
        this.networkView.chooseInitialCardSide(side);
    }

    /**
     * Draws two secret objectives.
     */
    public void drawSecretObjectives() throws WrongStateException, WrongTurnException, RemoteException {
        this.networkView.drawSecretObjectives();
    }

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     */
    public void chooseSecretObjective(Objective objective) throws WrongStateException, WrongTurnException, RemoteException, WrongChoiceException {
        this.networkView.chooseSecretObjective(objective);
    }

    /**
     * Plays a card.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     */
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongStateException, WrongTurnException, RemoteException, WrongChoiceException {
        this.networkView.playCard(coords, card, side);
    }

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     */
    public void drawCard(DrawSource source) throws HandException, WrongStateException, WrongTurnException, RemoteException, WrongChoiceException {
        this.networkView.drawCard(source);
    }

    public abstract void changePlayer();

    private void nextPlayer() throws RemoteException, WrongTurnException, WrongStateException {
        this.currentPlayer = this.players.get((this.players.indexOf(currentPlayer) + 1) % this.players.size());
        if (this.currentPlayer == null)
            this.currentPlayer = this.players.get(0);
        else
            this.currentPlayer = this.players.get((this.players.indexOf(currentPlayer) + 1) % this.players.size());

        this.changePlayer();

        if (this.currentPlayer.equals(this.username)) {
            if (this.clientBoards.get(this.username).getPlaced().isEmpty()) {
                this.networkView.drawInitialCard();
            } else if (this.clientBoards.get(this.username).getObjective() == null) {
                this.networkView.drawSecretObjectives();
            } else {
                this.makeMove();
            }
        }
    }

    public abstract void makeMove();


    public abstract void giveLobbyInfo(List<String> playersUsernames);

    public void matchStarted(Map<String, Color> playersUsernamesAndPawns, Map<String, List<PlayableCard>> playersHands,
            Pair<Objective, Objective> visibleObjectives, Map<DrawSource, PlayableCard> visiblePlayableCards,
            Pair<Symbol, Symbol> decksTopReign) throws RemoteException, WrongStateException, WrongTurnException {
        this.players = new ArrayList<>();
        this.clientBoards = new HashMap<>();
        Color curr;
        playersUsernamesAndPawns.forEach((player, pawn) -> this.players.add(player));

        for (String username : playersUsernamesAndPawns.keySet()) {
            curr = playersUsernamesAndPawns.get(username);
            switch (curr) {
                case Color.RED:
                    this.players.set(0, username);
                    break;
                case Color.BLUE:
                    this.players.set(1, username);
                    break;
                case Color.GREEN:
                    this.players.set(2, username);
                    break;
                case Color.YELLOW:
                    this.players.set(3, username);
                    break;

                default:
                    break;
            }
        }

        this.currentPlayer = null;

        playersHands.forEach((username, hand) -> {
            this.clientBoards.put(username, new ClientBoard(playersUsernamesAndPawns.get(username), hand));
        });

        this.visiblePlayableCards = visiblePlayableCards;
        this.visibleObjectives = visibleObjectives;
        this.decksTopReign = decksTopReign;

        this.notifyMatchStarted();

        this.nextPlayer();
    }

    protected abstract void notifyMatchStarted();

    public void receiveAvailableMatches(List<AvailableMatch> availableMatches) {
        this.availableMatches = availableMatches;
    }

    public abstract void giveInitialCard(InitialCard initialCard);

    public abstract void giveSecretObjectives(Pair<Objective, Objective> secretObjectives);

    public abstract void someoneDrewInitialCard(String someoneUsername, InitialCard card);

    public void someoneSetInitialSide(String someoneUsername, Side side) throws WrongTurnException, WrongStateException, RemoteException {
        this.nextPlayer();
    }

    public abstract void someoneDrewSecretObjective(String someoneUsername);

    public void someoneChoseSecretObjective(String someoneUsername)  throws WrongTurnException, WrongStateException, RemoteException {
        this.nextPlayer();
    }

    public void someonePlayedCard(String someoneUsername, Pair<Integer, Integer> coords, PlayableCard card, Side side, int points,
            Map<Symbol, Integer> availableResources) {
        if (points >= 20 && !this.lastTurn) {
            this.lastTurn = true;
            this.notifyLastTurn();
        }
        this.clientBoards.get(someoneUsername).placeCard(coords, card, side, points, availableResources);
    }

    public void someoneDrewCard(String someoneUsername, DrawSource source, PlayableCard card, PlayableCard replacementCard,
            Symbol replacementCardReign) throws WrongTurnException, WrongStateException, RemoteException {
        if (source.equals(DrawSource.GOLDS_DECK)) {
            this.decksTopReign = new Pair<Symbol, Symbol>(replacementCardReign, this.decksTopReign.second());
        } else if (source.equals(DrawSource.RESOURCES_DECK)) {
            this.decksTopReign = new Pair<Symbol, Symbol>(this.decksTopReign.first(), replacementCardReign);
        } else {
            visiblePlayableCards.put(source, replacementCard);
        }

        if (decksTopReign.first() == null && decksTopReign.second() == null && !this.lastTurn) {
            this.lastTurn = true;
            this.notifyLastTurn();
        }

        this.nextPlayer();
    }

    public abstract void notifyLastTurn();

    public abstract void someoneJoined(String someoneUsername);

    public abstract void someoneQuit(String someoneUsername);

    public abstract void matchFinished(List<LeaderboardEntry> ranking);

    public abstract void someoneSentBroadcastText(String someoneUsername, String text);

    public abstract void someoneSentPrivateText(String someoneUsername, String text);
}
