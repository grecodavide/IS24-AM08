package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.InvalidResourceException;

import java.util.EnumSet;

/**
* The front side of these cards always gives points, but needs a certain requirement to be met in order to be played
* @see CardFace
*/
public class GoldCard extends PlayableCard{
    private Symbol multiplier;
    private QuantityRequirement req;

    /**
    * Class constructor. It needs to only take the front as an argument, since the back is handled by its superclass {@link PlayableCard}
    * @param front the front side of the card
    * @param reign the reign of the card
    * @param multiplier the symbol whose number of resources multiplies the points parameter
    * @param points the number every resource of the given type is worth
    * @param req the requirement that must be met in order to be able to play the card
    * @throws InvalidResourceException if the passed resource is not in {@link Symbol#getReigns()}
    */
    public GoldCard(CardFace front, Symbol reign, Symbol multiplier, int points, QuantityRequirement req) throws InvalidResourceException {
        super(reign);
        this.front = front;
        this.points = points;
        this.req = req; // integrity check already provided in the constructor of QuantityRequirement

        // integrity check for allowed multipliers
        EnumSet<Symbol> validMultiplier = Symbol.getValidMultiplier();
        if(!validMultiplier.contains(multiplier)){
            throw new InvalidResourceException("Resource " + multiplier.toString() + " is not valid for a " + this.getClass().toString());
        }
        this.multiplier = multiplier;
    }

    /**
    * Getter for the GoldCard class
    * @return the multiplier.
    */
    public Symbol getMultiplier(){
        return this.multiplier;
    }

    /**
    * Getter for the GoldCard class
    * @return the quantity requirement for the gold card to be played.
    */
    public QuantityRequirement getRequirement(){
        return this.req;
    }

    /**
    * Will compute the total points this card gives based on the board it's played on
    * @param board The board on which we want to compute the points this card will give
    */
    public int calculatePoints(Board board) {
        return this.points; // will need to compute tot resources of board and get the tot resource
    }
}
