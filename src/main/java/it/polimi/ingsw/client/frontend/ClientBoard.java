package it.polimi.ingsw.client.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.utils.Pair;

/**
 * This class contains just elements needed to show the player's board, points, resources, hand and objectives
 */
public class ClientBoard {
    private final Map<Integer, ShownCard> placed;
    private Integer placementNumber;
    private List<PlayableCard> hand;
    private Integer points;
    private Map<Symbol, Integer> availableResources;
    private final Color color;
    private Objective objective;
    private InitialCard initialCard;

    
    /**
     * Class constructor.
     * 
     * @param color The player pawn's color
     * @param hand The player's hand
     */
    public ClientBoard(Color color, List<PlayableCard> hand) {
        this.placementNumber = 0;
        this.placed = new HashMap<>();
        this.color = color;

        this.hand = new ArrayList<>();
        this.hand = hand;

        this.points = 0;

        this.availableResources = new HashMap<>();
        Symbol.getBasicResources().forEach((reign -> this.availableResources.put(reign, 0)));
    }
    
    /**
     * Sets the secret objective.
     * 
     * @param objective The chosen secret objective
     */
    public void setSecretObjective(Objective objective) {
        this.objective = objective;
    }
    
    /**
     * Adds a card to the player's board.
     * 
     * @param coords The card's coordinates
     * @param card The chosen card
     * @param side The chosen side
     * @param points The player's point (total)
     * @param resources The player's resources (total)
     */
    public void placeCard(Pair<Integer, Integer> coords, PlayableCard card, Side side, Integer points, Map<Symbol, Integer> resources) {
        this.hand.remove(card);
        this.placed.put(placementNumber, new ShownCard(card, side, coords));
        this.points = points;
        this.availableResources = resources;
        this.placementNumber++;
    }
    
    /**
     * Adds a card to the player's hand.
     * 
     * @param card The drawn card
     */
    public void drawCard(PlayableCard card) {
        this.hand.add(card);
    }

    
    /**
     * Sets the initial card. This still does not put it in the board, as the side is still not chosen.
     * 
     * @param card The chosen card
     *
     * @see ClientBoard#placeInitial(Side, Map)
     */
    public void setInitial(InitialCard card) {
        this.initialCard = card;
    }
    
    /**
     * Places initial card on the board. At this point, the initial card's side has been chosen
     * 
     * @param side The chosen side
     * @param availableResources The player's resources (total)
     */
    public void placeInitial(Side side, Map<Symbol, Integer> availableResources) {
        this.placed.put(placementNumber, new ShownCard(this.initialCard, side, new Pair<>(0, 0)));
        this.availableResources = availableResources;
        this.placementNumber++;
    }

    /**
     * @return The initial card.
     */
    public InitialCard getInitialCard() {return this.initialCard;}

    /**
     * @return The card's index (ie, the first card played would have a placementNumber of 1).
     */
    public Integer getPlacementNumber() {
        return placementNumber;
    }

    /**
     * @return The player's hand.
     */
    public List<PlayableCard> getHand() {
        return hand;
    }

    /**
     * @return The player's points.
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * @return The player's board.
     */
    public Map<Integer, ShownCard> getPlaced() {
        return placed;
    }

    /**
     * @return The player's resources.
     */
    public Map<Symbol, Integer> getAvailableResources() {
        return availableResources;
    }

    /**
     * @return The player pawn's color.
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return The player's secret objective.
     */
    public Objective getObjective() {
        return objective;
    }

}
