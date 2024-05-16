package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.gamemodel.Color;
import it.polimi.ingsw.gamemodel.Side;
import it.polimi.ingsw.gamemodel.Symbol;
import it.polimi.ingsw.utils.Pair;
import it.polimi.ingsw.utils.TUICardParser;

/**
 * Class that handles the actual printing to the terminal
 */
public class TuiPrinter {
    private final Terminal terminal;
    private final TUICardParser parser;
    private final Integer infoLineOffset;
    private static final Integer cardRows = 6, cardCols = 18, cornerRows = 3, cornerCols = 5;

    public TuiPrinter() throws IOException {
        this.terminal = org.jline.terminal.TerminalBuilder.terminal();
        this.parser = new TUICardParser();
        this.infoLineOffset = 2;
    }

    private Pair<Integer, Integer> sumCoords(Pair<Integer, Integer> op1, Pair<Integer, Integer> op2) {
        return new Pair<>(op1.first() + op2.first(), op1.second() + op2.second());
    }

    private Integer getHeight() {
        return this.terminal.getHeight();
    }

    private Integer getWidth() {
        return this.terminal.getWidth();
    }

    private String setPosition(Integer x, Integer y) {
        return "\033[" + y + ";" + x + "H";
    }


    private Pair<Integer, Integer> getAbsoluteCoords(Pair<Integer, Integer> coords) {
        int termRows = this.getHeight(), termCols = this.getWidth();

        Pair<Integer, Integer> coordOffset = new Pair<Integer, Integer>((termCols - cardCols) / 2, (termRows - cardRows) / 2);
        Pair<Integer, Integer> coordUpdated =
                new Pair<Integer, Integer>(coords.first() * (cardCols - cornerCols), -coords.second() * (cardRows - cornerRows));

        return this.sumCoords(coordOffset, coordUpdated);
    }

    private String parseUsername(String username, Color color) {
        String c = switch (color) {
            case Color.RED -> "\033[031m";
            case Color.YELLOW -> "\033[032m";
            case Color.GREEN -> "\033[033m";
            case Color.BLUE -> "\033[034m";
            default -> "";
        };

        return c + username + "\033[0m";
    }

    private void printCard(ShownCard card) throws CardException {
        if (card.coords().equals(new Pair<>(0, 0)))
            System.out.println(parser.getInitial(card.card().getId(), getAbsoluteCoords(card.coords()), card.side() == Side.FRONT));
        else
            System.out
                    .println(parser.getPlayable(card.card().getId(), getAbsoluteCoords(card.coords()), card.coords(), card.side() == Side.FRONT));
        System.out.println("\033[0m");
    }

    private void printPoints(String username, Color color, Integer points) {
        int termRows = this.getHeight(), termCols = this.getWidth();
        String out = this.parseUsername(username, color) + "'s Points: " + points;
        System.out.println(this.setPosition((termCols - out.length()) / 4, termRows - infoLineOffset) + out);
    }

    private void printAvailableResources(Map<Symbol, Integer> availableResources) {
        int termRows = this.getHeight(), termCols = this.getWidth();
        String out = "";
        String spaces = "    ";
        Integer len = availableResources.keySet().size() * (5 + spaces.length()); // icon, space, :, space, number
        List<Symbol> toPrint =
                List.of(Symbol.PLANT, Symbol.INSECT, Symbol.FUNGUS, Symbol.ANIMAL, Symbol.PARCHMENT, Symbol.FEATHER, Symbol.INKWELL);

        for (Symbol resource : toPrint) {
            out += parser.getRightColor(resource) + parser.getRightIcon(resource) + ": " + availableResources.get(resource) + spaces;
        }

        System.out.println(this.setPosition((termCols - len) / 2, termRows - infoLineOffset) + out + "\033[0m");
    }

    /**
     * Clears the terminal
     */
    public void clearTerminal() {
        System.out.println("\033[2J");
    }

    /**
     * Prints the command prompt
     */
    public void printPrompt() {
        int termRows = this.getHeight();
        System.out.print(this.setPosition(1, termRows - infoLineOffset + 1) + "Command: ");
        System.out.flush();
    }

    /**
     * Prints a message in the line above the prompt
     * 
     * @param string The message to print
     */
    public void printMessage(String string) {
        int termRows = this.getHeight();
        System.out.println(this.setPosition(1, termRows - infoLineOffset) + string);
    }


    /**
     * Prints the list of players with their associated number
     * 
     * @param players the list of players to print
     */
    public void printPlayerList(List<String> players) {
        int termRows = this.getHeight();
        Integer offset = 0;
        for (String username : players) {
            System.out.println(this.setPosition(1, termRows - infoLineOffset - offset) + (offset + 1) + ". " + username);
            offset++;
        }
    }

    /**
     * Prints the whole board, including username, points and resources
     * 
     * @param username The username to print
     * @param board the board to be printed
     */
    public void printPlayerBoard(String username, ClientBoard board) {
        this.clearTerminal();
        if (board == null) {
            this.printMessage("No such player exists!");
            return;
        }
        Map<Integer, ShownCard> placed = board.getPlaced();
        for (Integer turn : placed.keySet()) {
            try {
                this.printCard(placed.get(turn));
            } catch (CardException e) {
                e.printStackTrace();
            }
        }
        this.printAvailableResources(board.getResources());
        this.printPoints(username, board.getColor(), board.getPoints());
    }

    /**
     * Prints the hand of the player, which includes the 3 available-to-play cards.
     *
     * @param username username of the player
     * @param color color of the player's token
     * @param hand list of cards (as IDs)
     */
    public void printHand(String username, Color color, List<Integer> hand) {
        int termCols = this.getWidth();
        Integer handSize = hand.size();
        Integer spaces = 4;
        Integer strlen = (username + "'s Hand").length();

        username = this.parseUsername(username, color) + "'s Hand:";

        Integer last = (termCols - (handSize) * (cardCols)) / 2 - spaces * (handSize - 1) / 2;

        System.out.println(this.setPosition((termCols - strlen) / 2, 1) + username);
        for (Integer cardID : hand) {
            try {
                System.out.println(parser.getPlayable(cardID, new Pair<Integer, Integer>(last, 2), null, true) + "\033[0m");
                last += cardCols + spaces;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints the objectives, both common and secret, of a given player.
     *
     * @param username username of the player
     * @param color color of the player's token
     * @param secret secret objective (as ID)
     * @param visibles array of common objectives (as IDs)
     */
    public void printObjectives(String username, Color color, Integer secret, Integer[] visibles) {
        int termCols = this.getWidth();
        Integer visiblesSize = visibles.length;
        Integer spaces = 4;
        Integer strlen;

        strlen = ("Your secret objective").length();
        username = this.parseUsername("Your", color) + " secret objective:";
        Integer last = (termCols - strlen) / 2;
        System.out.println(this.setPosition(last, 1) + username);

        last = (termCols - cardCols) / 2;
        try {
            System.out.println(parser.getObjective(secret, new Pair<Integer, Integer>(last, 2)) + "\033[0m");
        } catch (CardException e) {
            e.printStackTrace();
        }

        username = "Common objectives:";
        System.out.println(this.setPosition((termCols - username.length()) / 2, 3+cardRows) + username);

        last = (termCols - (visiblesSize) * (cardCols)) / 2 - spaces * (visiblesSize - 1) / 2;
        for (Integer cardID : visibles) {
            try {
                System.out.println(parser.getObjective(cardID, new Pair<Integer, Integer>(last, 4+cardRows)) + "\033[0m");
                last += cardCols + spaces;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void printChat(List<String> chat) {
        int rows = this.getHeight() - infoLineOffset+1;
        int start = chat.size() - rows;
        if (start < 0) {
            start = 0;
        }

        for (int i = start; i < chat.size(); i++) {
            System.out.println(this.setPosition(1, i-start) + chat.get(i));
        }
        
    }

}
