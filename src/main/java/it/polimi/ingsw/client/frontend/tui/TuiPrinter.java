package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.Map;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.TUICardParser;

/**
 * Class to show Cards on the TUI. It handles coordinates conversion from relative to absolute, and prints to terminal in the right position
 */
public class TuiPrinter {
    private final Terminal terminal;
    private final TUICardParser parser;
    private final Integer infoLineOffset;

    public TuiPrinter() throws IOException {
        this.terminal = org.jline.terminal.TerminalBuilder.terminal();
        this.parser = new TUICardParser();
        this.infoLineOffset = 2;
    }

    private Pair<Integer, Integer> add(Pair<Integer, Integer> op1, Pair<Integer, Integer> op2) {
        return new Pair<>(op1.first() + op2.first(), op1.second() + op2.second());
    }

    // note that terminal will be inside the class
    private Pair<Integer, Integer> getCardCoords(Pair<Integer, Integer> coords) {
        int cardRows = 6, cardCols = 18;
        int cornerRows = 3, cornerCols = 5;

        int termRows = this.terminal.getHeight(), termCols = this.terminal.getWidth();

        Pair<Integer, Integer> coordOffset = new Pair<Integer, Integer>((termCols - cardCols) / 2, (termRows - cardRows) / 2);
        Pair<Integer, Integer> coordUpdated = new Pair<Integer, Integer>(coords.first() * (cardCols - cornerCols),
                -coords.second() * (cardRows - cornerRows));

        return this.add(coordOffset, coordUpdated);
    }

    
    /**
     * Clears the terminal
     */
    public void clearTerminal() {
        System.out.println("\033[2J");
    }
    
    /**
     * Outputs to terminal a {@link ShownCard}
     * 
     * @param card The card to show
     * 
     * @throws CardException If the card is not found
     */
    public void printCard(ShownCard card) throws CardException {
        if (card.coords().equals(new Pair<>(0, 0)))
            System.out.println(parser.getInitial(card.card().getId(), getCardCoords(card.coords()), card.side() == Side.FRONT));
        else
            System.out.println(parser.getPlayable(card.card().getId(), getCardCoords(card.coords()), card.side() == Side.FRONT));
        System.out.println("\033[0m");
    }

    public void printPoints(Integer points) {
        int termRows = this.terminal.getHeight(), termCols = this.terminal.getWidth();
        String out = "Current points: " + points ;
        System.out.println("\033[" + (termRows-infoLineOffset) + ";" + ((termCols-out.length())/4)+"H" + out);
    }


    // if we want the same position always, then we need to hardcode them. Ugly
    public void printResources(Map<Symbol, Integer> availableResources) {
        int termRows = this.terminal.getHeight(), termCols = this.terminal.getWidth();
        String out = "";
        String spaces = "    ";
        Integer len = availableResources.keySet().size()*(5+spaces.length()); // icon, space, :, space, number
        for (Symbol resource : availableResources.keySet()) {
            out += parser.getRightColor(resource) + parser.getRightIcon(resource) + ": " + availableResources.get(resource) + spaces;
        }

        System.out.println("\033[" + (termRows-infoLineOffset) + ";" + ((termCols - len)/2)+"H" + out + "\033[0m");
    }

    public void printPrompt() {
        int termRows = this.terminal.getHeight();
        System.out.print("\033[" + (termRows-infoLineOffset+1) + ";" + "1H" + "Command: ");
        System.out.flush();
    }

    public void printMessage(String string) {
        int termRows = this.terminal.getHeight();
        System.out.println("\033[" + (termRows-infoLineOffset) + ";" + "1H" + string);
    }

}
