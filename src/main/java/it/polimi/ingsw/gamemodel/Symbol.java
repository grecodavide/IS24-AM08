package it.polimi.ingsw.gamemodel;

/**
* Contains all the possible symbols a corner can store or a multiplier can have.
* Besides the basic 4 factions and 3 symbols, there is also EMPTY_CORNER, which represents a corner withtout the
* possibility of placing another card on top of it (missing slot), FULL_CORNER which represents a valid corner without any symbol and
* CORNER_OBJ which represents a {@link QuantityRequirement} in which the multiplier is how many corners the card covered
*/
public enum Symbol {
    ANIMAL,
    PLANT,
    INSECT,
    FUNGUS,
    FEATHER,
    INKWELL,
    PARCHMENT,
    EMPTY_CORNER,
    FULL_CORNER,
    CORNER_OBJ
}
