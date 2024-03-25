package it.polimi.ingsw.gamemodel;

import it.polimi.ingsw.exceptions.*;
/*
* A condition must be met in order to play a golden card and to get points from the objectives. Those requirements are both represented by this class
*/
public abstract class Requirement {

    public Requirement()  throws InvalidResourceException {

    }

    /**
    * Will be implemented on the concrete classes, as they have different kind of conditions
    * @param board the board that will be used to check if the requirement is met
    * @return whether the requirement is met or not
    */
	public abstract int timesMet(Board board);
}
