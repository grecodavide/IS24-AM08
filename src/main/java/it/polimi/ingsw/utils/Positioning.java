package it.polimi.ingsw.utils;

import it.polimi.ingsw.gamemodel.PositionRequirement;

/**
 * Enum used to realize the TUI Objective parser. Note that the order of DIAG and VERT is used to
 * indicate the specific positioning of the {@link PositionRequirement}
 */
enum Positioning {
    DIAGONAL_LF, DIAGONAL_RG,

    DIAG_VERT_LF, DIAG_VERT_RG,

    VERT_DIAG_LF, VERT_DIAG_RG;

}
