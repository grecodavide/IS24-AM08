package it.polimi.ingsw.model;

/**
* Player represents each in-game user. The class also manages the board's logic
*/
public class Player {
    private String nickname;
    private Match match;
    private int points;
    private Board board;
    private Color color;
    private Objective objective;

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
    public void playCard(Pair<Integer, Integer> coord, PlayableCard card, Side side) {
        //TODO
    }

    /**
     * Adds a card to the player's hand, popping it from the required source
     * @param draw represents the source of the draw, which can be either one of the two decks or one of the four cards on the table
     */
    public void drawcard(DrawSource source) {
        // TODO
    }

    /**
    * Sets the player private objective (only at the start of the game)
    * @param objective the chosen objective between the two proposed
    */
    public void chooseSecretObjective(Objective objective) {
        // TODO
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
     * Getter for the player's color
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Setter for the player's color
     */ 
    
    protected void setColor(Color color) {
        this.color = color;
    }

   /**
    * Getter for the player's objective
    * @see #chooseSecretObjective(Objective)
    */
    protected Objective getSecretObjective() {
        return objective;
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
