package it.polimi.ingsw.utils;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import java.io.IOException;
import org.jline.terminal.*;

public class TuiCardParserTest {
    @Test
    public void testBoard() {

        try {
            Terminal terminal = org.jline.terminal.TerminalBuilder.terminal();
            TUICardParser parser = new TUICardParser();

            System.out.println("\033[2J");

            System.out.println(parser.getInitial(1,  this.getCardCoords(terminal, new Pair<Integer, Integer>(0, 0)), true));
            System.out.println(parser.getPlayable(1, this.getCardCoords(terminal, new Pair<Integer, Integer>(1, 1)), true));
            System.out.println(parser.getPlayable(2, this.getCardCoords(terminal, new Pair<Integer, Integer>(2, 2)), true));
            System.out.println(parser.getPlayable(3, this.getCardCoords(terminal, new Pair<Integer, Integer>(1, -1)), true));
            System.out.println(parser.getPlayable(4, this.getCardCoords(terminal, new Pair<Integer, Integer>(2, 0)), true));
            System.out.println(parser.getPlayable(5, this.getCardCoords(terminal, new Pair<Integer, Integer>(3, 1)), true));

        } catch (Exception e) {
            e.printStackTrace();
            assertFalse(true);
        }
    }


    // FIXME: THIS WILL BE IN ANOTHER CLASS LATER ON
    private Pair<Integer, Integer> add(Pair<Integer, Integer> op1, Pair<Integer, Integer> op2) {
        return new Pair<>(op1.first() + op2.first(), op1.second() + op2.second());
    }

    // note that terminal will be inside the class
    private Pair<Integer, Integer> getCardCoords(Terminal terminal, Pair<Integer, Integer> coords) throws IOException {
        int cardRows = 6, cardCols = 18;
        int cornerRows = 3, cornerCols = 5;

        int termRows = 54, termCols = 235; // NOTE: since tests cannot get right terminal size, have to hard code
        // int termRows = terminal.getHeight(), termCols = terminal.getWidth();

        Pair<Integer, Integer> coordOffset = new Pair<Integer, Integer>((termCols - cardCols) / 2, (termRows - cardRows) / 2);
        Pair<Integer, Integer> coordUpdated = new Pair<Integer, Integer>(coords.first()*(cardCols - cornerCols), -coords.second()*(cardRows - cornerRows));

        return this.add(coordOffset, coordUpdated);
    }
}
