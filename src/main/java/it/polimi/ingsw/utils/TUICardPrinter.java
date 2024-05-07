package it.polimi.ingsw.utils;

import it.polimi.ingsw.gamemodel.InitialCard;
import it.polimi.ingsw.gamemodel.Objective;

public class TUICardPrinter {

    // NEEDS TO BE MOVED: IT'S TUI-VIEW LOGIC
    /*
    Terminal term;

    Pair<Integer, Integer> center;

    public TUICardPrinter() throws IOException {
        // get the terminal
        term = org.jline.terminal.TerminalBuilder.terminal();
        int cols = term.getWidth(),
            rows = term.getHeight();

        // center of the terminal
        center = new Pair<>(
                (cols % 2 == 0) ? cols/2 : (cols-1)/2,
                (rows % 2 == 0) ? rows/2 : (rows-1)/2
        );
    }
    */

    public TUICardPrinter(){
    }

    /**
     * The method is called by the TUI View, in order to get what to print on the terminal.
     * @param cardID ID of the requested card
     * @param isFacingUp is the card facing up? If not, it's facing down.
     * @return the string containing the series characters representing the side on which the card was played.
     */

    public String getPrintableCardAsString(int cardID, Boolean isFacingUp){

        return "TBA";
    }

    /**
     * The method is called by the TUI View, in order to get what to print on the terminal.
     * @param initialCard initial card object
     * @param isFacingUp is the card facing up? If not, it's facing down.
     * @return the string containing the series characters representing the side on which the card was played.
     */

    public String getPrintableCardAsString(InitialCard initialCard, Boolean isFacingUp){

        return "TBA";
    }

    /**
     * The method is called by the TUI View, in order to get what to print on the terminal.
     * @param objectiveCard objective card object
     * @return the string containing the series characters representing the side on which the card was played.
     */

    public String getPrintableCardAsString(Objective objectiveCard){

        return "TBA";
    }

}
