package it.polimi.ingsw.gamemodel;
import java.util.EnumSet;

/**
* Contains all the possible symbols a corner can store or a multiplier can have.
* Besides the basic 4 factions and 3 symbols, there is also EMPTY_CORNER, which represents a corner without the
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

    /**
    * Generates subset containing only the four basic reigns
    * @return the subset containing only the four basic reigns
    */
    static public EnumSet<Symbol> getReigns() {
        return EnumSet.of(
            ANIMAL,
            PLANT,
            INSECT,
            FUNGUS
        );
    }

    /**
    * Generates subset containing only the basic resources (4 reigns and 3 "symbols")
    * @return the subset containing only the basic resources (4 reigns and 3 "symbols")
    */
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

    /**
    * Generates subset containing all the valid Symbols a corner can contain
    * @return the subset containing all the valid Symbols a corner can contain
    */
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

    /**
    * Generates subset containing all the symbols a {@link GoldCard} multiplier can be
    * @return the subset containing all the symbols a {@link GoldCard} multiplier can be
    */
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

