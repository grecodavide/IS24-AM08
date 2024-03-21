package it.polimi.ingsw.gamemodel;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.exceptions.*;

/**
* Player represents each in-game user. The class also manages the board's logic
*/
public class Player {
    private String nickname;
    private Match match;
    private int points;
    private Board board;
    private Color pawnColor;
    private Objective secretObjective;

    public Player(String nickname, Match match) {
        this.nickname = nickname;
        this.match = match;
         
        //Initialize values
        board = new Board();
        points = 0;

    }

    /**
    * Places on the board the desired card (on the specified side) in the given position, if it is a valid one
    * @param coord x and y position in which the card is played (where 0, 0 is the initial card)
    * @param card the card to be placed
    * @param side whether the card should be placed on the front or on the back
    */
    public void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side) throws WrongTurnException, WrongStateException {
        if (match.getCurrentPlayer().equals(this)) {
            match.makeMove(coords, card, side);
        } else {
            throw new WrongTurnException("Only the current player can play cards");
        }
    }

    /**
     * Adds a card to the player's hand, popping it from the required source
     * @param source represents the source of the draw, which can be either one of the two decks or one of the four cards on the table
     */
    public void drawcard(DrawSource source) throws WrongTurnException {
        if (match.getCurrentPlayer().equals(this)) {
            PlayableCard card = match.drawCard(source);
            board.addHandCard(card);
        } else {
            throw new WrongTurnException("Only the current player can draw cards");
        }
    }

    /**
    * Sets the player private objective (only at the start of the game)
    * @param objective the chosen objective between the two proposed
    */
    public void chooseSecretObjective(Objective objective) throws WrongTurnException {
        if (match.getCurrentPlayer().equals(this)) {
            match.chooseSecretObjective(objective);
            secretObjective = objective;
        } else {
            throw new WrongTurnException("Only the current player can choose an objective");
        }
    }
    
    /**
    * Getter for the player's board
    */
    public Board getBoard() {
        return board;
    }

    /**
    * Getter for the player's points
    */
    public int getPoints() {
        return points;
    }
    /**
     * Getter for the player's pawn color
     */
    public Color getPawnColor() {
        return pawnColor;
    }
    
    /**
     * Setter for the player's color
     */
    protected void setColor(Color color) {
        this.pawnColor = color;
    }

   /**
    * Getter for the player's objective
    * @see #chooseSecretObjective(Objective)
    */
    protected Objective getSecretObjective() {
        return secretObjective;
    }

    /**
     * Adds points to the player
     * @param points number of points to add to the player
     */
    protected void addPoints(int points) {
        this.points += points;
    }

    /**
     * Getter for the player's nickname
     */
    public String getNickname() {
        return nickname;
    }
}
