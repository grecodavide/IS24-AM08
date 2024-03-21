package it.polimi.ingsw.gamemodel;
import java.util.EnumSet;

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
    CORNER_OBJ;

    static public EnumSet<Symbol> getReigns() {
        return EnumSet.of(
            ANIMAL,
            PLANT,
            INSECT,
            FUNGUS
        );
    }

    static public EnumSet<Symbol> getBasicResources() {
        return EnumSet.of(
            ANIMAL,
            PLANT,
            INSECT,
            FUNGUS,
            FEATHER,
            INKWELL,
            PARCHMENT
        );
    }

    static public EnumSet<Symbol> getValidCorner() {
        return EnumSet.of(
            ANIMAL,
            PLANT,
            INSECT,
            FUNGUS,
            FEATHER,
            INKWELL,
            PARCHMENT,
            EMPTY_CORNER,
            FULL_CORNER
        );
    }

    static public EnumSet<Symbol> getValidMultiplier() {
        return EnumSet.of(
            ANIMAL,
            PLANT,
            INSECT,
            FUNGUS,
            FEATHER,
            INKWELL,
            PARCHMENT,
            CORNER_OBJ
        );
    }

}

