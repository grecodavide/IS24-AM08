package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.HandException;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.exceptions.WrongTurnException;
import it.polimi.ingsw.utils.Pair;

import java.util.Objects;

/**
 * Represents each in-game user, so acts also as a gateway receiving input by the Controller.
 * It's also responsible for the board's logic, which is a slice of the game logic.
 */
public class Player {
    private final String username;
    private final Match match;
    private int points;
    private final Board board;
    private Color pawnColor;
    private Objective secretObjective;

    /**
     * Initializes the main player's attributes.
     *
     * @param username the player's username
     * @param match    the match the player belongs to
     */
    public Player(String username, Match match) {
        this.username = username;
        this.match = match;

        //Initialize values
        board = new Board();
        points = 0;
    }

    /**
     * Initializes the current instance from a copy reference
     *
     * @param player
     */
    public Player(Player player) {
        this.username = player.username;
        this.match = player.match;
        this.board = player.board;
        this.points = player.points;
        this.pawnColor = player.pawnColor;
        this.secretObjective = player.secretObjective;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;

        return username.equals(player.username) &&
                match.equals(player.match);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, match, points, board, pawnColor, secretObjective);
    }

    /**
     * Places a card on the player's board, on the give side and in the given position, assuming it's valid.
     *
     * @param coords x and y position in which the card is played (where 0, 0 is the initial card)
     * @param card   the card to be placed
     * @param side   whether the card should be placed on the front or on the back
     */
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongTurnException, WrongStateException, WrongChoiceException {
        synchronized (match) {
            if (match.getCurrentPlayer().equals(this))
                match.makeMove(coords, card, side);
            else
                throw new WrongTurnException("Only the current player can play cards");
        }
    }

    /**
     * Gets two objectives from the match objectives deck considered to be secret.
     *
     * @return a pair of objectives
     * @throws WrongStateException if called during the wrong match state
     * @throws WrongTurnException  if called by the player when it's not its turn
     */
    public Pair<Objective, Objective> drawSecretObjectives() throws WrongStateException, WrongTurnException {
        synchronized (match) {
            if (match.getCurrentPlayer().equals(this)) {
                return match.proposeSecretObjectives();
            } else {
                throw new WrongTurnException("Only the current player can draw secret objectives");
            }
        }
    }

    /**
     * Gets an initial card from the match.
     *
     * @return an initial card
     * @throws WrongTurnException  if called by the player when it's not its turn
     * @throws WrongStateException if called during the wrong match state
     */
    public InitialCard drawInitialCard() throws WrongTurnException, WrongStateException {
        synchronized (match) {
            if (match.getCurrentPlayer().equals(this))
                return match.drawInitialCard();
            else
                throw new WrongTurnException("Only the current player can draw the initial card");
        }
    }

    /**
     * Chooses the initial card side.
     *
     * @param side the side of the initial card
     * @throws WrongTurnException  if called by the player when it's not its turn
     * @throws WrongStateException if called during the wrong match state
     */
    public void chooseInitialCardSide(Side side) throws WrongTurnException, WrongStateException {
        synchronized (match) {
            if (match.getCurrentPlayer().equals(this))
                match.setInitialSide(side, board.getAvailableResources());
            else
                throw new WrongTurnException("Only the current player can choose the initial card side");
        }
    }

    /**
     * Adds a card to the player's hand, popping it from the required source
     *
     * @param source represents the source of the draw, which can be either one of the two decks or one of the four cards on the table
     * @throws WrongTurnException   if called by the player when it's not its turn
     * @throws WrongChoiceException if called on a drawing source which is empty (e.g. empty deck)
     * @throws WrongStateException  if called during the wrong match state
     */
    public void drawCard(DrawSource source) throws HandException, WrongStateException, WrongChoiceException, WrongTurnException {
        synchronized (match) {
            if (match.getCurrentPlayer().equals(this)) {
                PlayableCard card = match.drawCard(source);
                board.addHandCard(card);
            } else {
                throw new WrongTurnException("Only the current player can draw cards");
            }
        }
    }

    /**
     * Sets the player private objective (only at the start of the game).
     *
     * @param objective the chosen objective between the two proposed
     * @throws WrongTurnException   if called by the player when it's not its turn
     * @throws WrongStateException  if called during the wrong match state
     * @throws WrongChoiceException if called on an objective which is not one of the proposed ones
     */
    public void chooseSecretObjective(Objective objective) throws WrongTurnException, WrongStateException, WrongChoiceException {
        synchronized (match) {
            if (match.getCurrentPlayer().equals(this)) {
                match.setSecretObjective(objective);
                secretObjective = objective;
            } else {
                throw new WrongTurnException("Only the current player can choose an objective");
            }
        }
    }

    /**
     * Sends a message in public chat
     *
     * @param text content of the message
     */
    public void sendBroadcastText(String text) {
        this.match.sendBroadcastText(this, text);
    }

    /**
     * Sends a private message to the specified recipient
     *
     * @param recipient recipient of the message
     * @param text      content of the message
     */
    public void sendPrivateText(Player recipient, String text) {
        this.match.sendPrivateText(this, recipient, text);
    }

    /**
     * Getter for the player's board.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Getter for the player's points.
     */
    public int getPoints() {
        return points;
    }

    /**
     * Getter for the player's pawn color.
     */
    public Color getPawnColor() {
        return pawnColor;
    }

    /**
     * Setter for the player's color.
     */
    protected void setColor(Color color) {
        this.pawnColor = color;
    }

    /**
     * Getter for the player's secret objective.
     *
     * @see #chooseSecretObjective(Objective)
     */
    protected Objective getSecretObjective() {
        return secretObjective;
    }

    /**
     * Adds points to the player.
     *
     * @param points number of points to add to the player
     */
    protected void addPoints(int points) {
        this.points += points;
    }

    /**
     * Getter for the player's username.
     */
    public String getUsername() {
        return username;
    }
}
