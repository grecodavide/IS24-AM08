package it.polimi.ingsw.client.network;

import it.polimi.ingsw.client.frontend.GraphicalViewInterface;
import it.polimi.ingsw.gamemodel.DrawSource;
import it.polimi.ingsw.gamemodel.Objective;
import it.polimi.ingsw.gamemodel.PlayableCard;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.utils.Pair;

// we need graphicalInterface to RECEIVE information
public abstract class NetworkView implements RemoteViewInterface {
    GraphicalViewInterface graphicalInterface;
    /**
     * Register the graphical interface to notify changes
     * @param graphicalInterface graphicat interface to notify changes
     */
    protected void setGraphicalInterface(GraphicalViewInterface graphicalInterface) {
        this.graphicalInterface = graphicalInterface;
    }

    // Action Methods
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
