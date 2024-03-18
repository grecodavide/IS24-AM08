package it.polimi.ingsw.gamemodel;

/*
* A condition must be met in order to play a golden card and to get points from the objectives. Those requirements are both represented by this class
*/
public abstract class Requirement {

	public abstract boolean isSatisfied(Board board);

}
