package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.frontend.GraphicalView;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.AvailableMatch;
import it.polimi.ingsw.utils.Pair;

public abstract class NetworkView implements RemoteViewInterface {
    GraphicalView graphicalInterface;
    protected String username;

    public void setUsername(String username) {
        this.username = username;
    }

    public NetworkView(GraphicalView graphicalViewInterface) {
        this.graphicalInterface = graphicalViewInterface;
    }

    public abstract void showError(String cause);
    
    /**
     * Asks the server to send a list of {@link AvailableMatch}
     */
    public abstract void getAvailableMatches();

    // Action Methods
    /**
     * Tries to create a match
     * 
     * @param matchName The match's name
     */
    public abstract void createMatch(String matchName, Integer maxPlayers);
    
    /**
     * Tries to join a match
     * 
     * @param matchName the match's name
     */
    public abstract void joinMatch(String matchName);

    /**
     * Draws an initial card for the player.
     */
    public abstract void drawInitialCard();

    /**
     * Communicates the chosen initial card side.
     *
     * @param side The side on which play the initial card drawn using {@link #drawInitialCard()}
     */
    public abstract void chooseInitialCardSide(Side side);

    /**
     * Draws two secret objectives.
     *
     */
    public abstract void drawSecretObjectives();

    /**
     * Communicates the chosen secret objective.
     *
     * @param objective The chosen objective
     */
    public abstract void chooseSecretObjective(Objective objective);

    /**
     * Plays a card.
     *
     * @param coords The coordinates on which to place the card
     * @param card   The PlayableCard to play
     * @param side   The side on which to play the chosen card
     */
    public abstract void playCard(Pair<Integer, Integer> coords, PlayableCard card, Side side);

    /**
     * Draws a card.
     *
     * @param source The drawing source to draw the card from
     */
    public abstract void drawCard(DrawSource source);
}
