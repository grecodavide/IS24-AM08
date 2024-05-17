package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jline.terminal.Terminal;
import it.polimi.ingsw.client.frontend.ClientBoard;
import it.polimi.ingsw.client.frontend.ShownCard;
import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.gamemodel.*;
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
    private final Map<Pair<String, String>, String> commandList;

    public TuiPrinter() throws IOException {
        this.terminal = org.jline.terminal.TerminalBuilder.terminal();
        this.parser = new TUICardParser();
        this.infoLineOffset = 2;
        this.commandList = new HashMap<>();

        // list of available commands
        commandList.put(new Pair<>("quit", "q"), "exit the match");
        commandList.put(new Pair<>("place", "p"), "place a card in the chosen coordinates"); // review
        commandList.put(new Pair<>("list", "l"), "print player list");
        commandList.put(new Pair<>("chat view", "cv"), "view the latest messages of the chat");
        commandList.put(new Pair<>("chat send", "cs"), "send a message in the chat");
        commandList.put(new Pair<>("show", "s"), "show the board of the chosen player");
        commandList.put(new Pair<>("hand", "h"), "show the hand of the chosen player");
        commandList.put(new Pair<>("help", "-h"), "show the list of all available commands");
        commandList.put(new Pair<>("objective", "o"), "show the objectives of the current player");
    }

    // --------------- //
    // PRIVATE METHODS //
    // --------------- //

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
            System.out.println(parser.parseCard(card.card(), getAbsoluteCoords(card.coords()), null, card.side() == Side.FRONT));
        else
            System.out
                    .println(parser.parseCard(card.card(), getAbsoluteCoords(card.coords()), card.coords(), card.side() == Side.FRONT));
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

    private void printWelcome(int x, int y){
        List<String> welcomeString = new ArrayList<>();

        String prefix = setPosition(x, y);
        welcomeString.add(prefix + "  _       __           __                                               __          ");
        prefix = setPosition(x, ++y);
        welcomeString.add(prefix + " | |     / /  ___     / /  _____   ____     ____ ___     ___           / /_   ____  ");
        prefix = setPosition(x, ++y);
        welcomeString.add(prefix + " | | /| / /  / _ \\   / /  / ___/  / __ \\   / __  __ \\   / _ \\         / __/  / __ \\ ");
        prefix = setPosition(x, ++y);
        welcomeString.add(prefix + " | |/ |/ /  /  __/  / /  / /__   / /_/ /  / / / / / /  /  __/        / /_   / /_/ / ");
        prefix = setPosition(x, ++y);
        welcomeString.add(prefix + " |__/|__/   \\___/  /_/   \\___/   \\____/  /_/ /_/ /_/   \\___/         \\__/   \\____/  ");

        for (int i = 0; i < welcomeString.size(); i++)
            System.out.println(welcomeString.get(i));
    }

    private void printTitle(int x, int y){
        List<String> titleString = new ArrayList<>();
        String prefix = setPosition(x, y);

        titleString.add(prefix + "   _____                                                                                                                                                                                       _____   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  ( ___ )-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------( ___ )  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |             ,gggg,                                                         ,ggg, ,ggggggg,                                                                                            |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |           ,88'''Y8b,                     8I                               dP''Y8,8P'''''Y8b                I8                                       ,dPYb,                            |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |          d8'     'Y8                     8I                               Yb, '8dP'     '88                I8                                       IP''Yb                            |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |         d8'   8b  d8                     8I                                ''  88'       88             88888888                                    I8  8I  gg                        |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |        ,8I    'Y88P'                     8I                                    88        88                I8                                       I8  8'  ''                        |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |        I8'             ,ggggg,     ,gggg,8I   ,ggg,      ,gg,   ,gg            88        88    ,gggg,gg    I8    gg      gg   ,gggggg,    ,gggg,gg  I8 dP   gg     ,g,                |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |        d8             dP'  'Y8ggg dP'  'Y8I  i8' '8i    d8''8b,dP'             88        88   dP'  'Y8I    I8    I8      8I     iP'''8I   dP'  'Y8I  I8dP    88    ,8'8,              |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |        Y8,           i8'    ,8I  i8'    ,8I  I8, ,8I   dP   ,88'               88        88  i8'    ,8I   ,I8,   I8,    ,8I   ,8'   8I  i8'    ,8I  I8P     88   ,8'  Yb              |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |        'Yba,,_____, ,d8,   ,d8' ,d8,   ,d8b, 'YbadP' ,dP  ,dP'Y8,              88        Y8,,d8,   ,d8b, ,d88b, ,d8b,  ,d8b,,dP     Y8,,d8,   ,d8b,,d8b,_ _,88,_,8'    8)             |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |          ''Y8888888 P'Y8888P'  PP'Y8888P''Y8888P'Y8888'  dP'   'Y888          888        'Y8P'Y8888P''Y888P''Y888P''Y88P''Y88P      'Y8P'Y8888P''Y88P''Y888P''Y8P'  'Y8P8PP           |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   |___|                                                                                                                                                                                       |___|   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  (_____)-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------(_____)  ");

        for (int i = 0; i < titleString.size(); i++)
            System.out.println(titleString.get(i));
    }

    private int getDimStart(int max, int dim){
        int left = max - dim;
        if (left % 2 == 1)
            left--;
        return left / 2;
    }

    // -------------- //
    // PUBLIC METHODS //
    // -------------- //

    /**
     * Clears the terminal
     */
    public void clearTerminal() {
        System.out.println("\033[2J");
    }

    /**
     * Prints the command prompt
     */
    public void printPrompt(String customMessage) {
        int termRows = this.getHeight();
        System.out.print(this.setPosition(1, termRows - infoLineOffset + 1) + customMessage + ": ");
        System.out.flush();
    }

    public void printMessage(List<String> message) {
        int termRows = this.getHeight();
        Integer offset = 0;
        for (String line : message) {
            System.out.println(this.setPosition(1, termRows - infoLineOffset - offset) + (offset + 1) + ". "+ line);
            offset++;
        }
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
        this.printAvailableResources(board.getAvailableResources());
        this.printPoints(username, board.getColor(), board.getPoints());
    }

    /**
     * Prints the hand of the player, which includes the 3 available-to-play cards
     *
     * @param username username of the player
     * @param color color of the player's token
     * @param hand list of cards (as IDs)
     */
    public void printHand(String username, Color color, List<PlayableCard> hand) {
        int termCols = this.getWidth();
        Integer handSize = hand.size();
        Integer spaces = 4;
        Integer strlen = (username + "'s Hand").length();

        username = this.parseUsername(username, color) + "'s Hand:";

        Integer last = (termCols - (handSize) * (cardCols)) / 2 - spaces * (handSize - 1) / 2;

        System.out.println(this.setPosition((termCols - strlen) / 2, 1) + username);
        for (PlayableCard card: hand) {
            try {
                System.out.println(parser.parseCard(card, new Pair<Integer, Integer>(last, 2), null, true) + "\033[0m");
                last += cardCols + spaces;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints the objectives, both common and secret, of a given player
     *
     * @param username username of the player
     * @param color color of the player's token
     * @param secret secret objective (as ID)
     * @param visibles array of common objectives (as IDs)
     */
    public void printObjectives(String username, Color color, Objective secret, Pair<Objective, Objective> visibles) {
        int termCols = this.getWidth();
        Integer visiblesSize = 2;
        Integer spaces = 4;
        Integer strlen;

        strlen = ("Your secret objective").length();
        username = this.parseUsername("Your", color) + " secret objective:";
        Integer last = (termCols - strlen) / 2;
        System.out.println(this.setPosition(last, 1) + username);

        last = (termCols - cardCols) / 2;
        System.out.println(parser.parseObjective(secret, new Pair<Integer, Integer>(last, 2)) + "\033[0m");

        username = "Common objectives:";
        System.out.println(this.setPosition((termCols - username.length()) / 2, 3+cardRows) + username);

        last = (termCols - (visiblesSize) * (cardCols)) / 2 - spaces * (visiblesSize - 1) / 2;

        System.out.println(parser.parseObjective(visibles.first(), new Pair<Integer, Integer>(last, 4+cardRows)) + "\033[0m");
        System.out.println(parser.parseObjective(visibles.second(), new Pair<Integer, Integer>(last+spaces, 4+cardRows)) + "\033[0m");
    }

    /**
     * Prints the message history of the most recent messages
     *
     * @param chat chat object, as a list of strings
     */
    public void printChat(List<String> chat) {
        int rows = this.getHeight() - infoLineOffset + 1;
        int start = chat.size() - rows;
        if (start < 0) {
            start = 0;
        }

        for (int i = start; i < chat.size(); i++) {
            System.out.println(this.setPosition(1, i-start) + chat.get(i));
        }

    }

    /**
     * Prints a list of available commands
     */
    public void printHelp(){
        String prefix = "Command used to";
        int maxLen = this.getHeight() - infoLineOffset + 1;
        int y = maxLen - this.commandList.size();

        for (Pair<String, String> command : this.commandList.keySet()){
            System.out.printf("%s%-15s %2s: %s %s", this.setPosition(1, y), command.first()+",", command.second(), prefix, this.commandList.get(command));
            y++;

        }
    }

    /**
     * Prints the welcome screen in the middle of the tui view
     */
    public void printWelcomeScreen(){
        int maxHeight = this.getHeight() - this.infoLineOffset;
        int welcomeHeight = 5, welcomeWidth = 90; // width must be even (pari)
        int spaceBetween = 3;
        int titleHeight = 18, titleWidth = 198; // width must be even (pari)

        int welcomeStartY = getDimStart(this.getHeight(), welcomeHeight + spaceBetween + titleHeight);
        int titleStartY = welcomeStartY + welcomeHeight + spaceBetween;
        int welcomeStartX = getDimStart(this.getWidth(), welcomeWidth);
        int titleStartX = getDimStart(this.getWidth(), titleWidth);

        printWelcome(welcomeStartX, welcomeStartY);
        printTitle(titleStartX, titleStartY);
    }

    // TO BE DELETED -- HERE just for easy TESTING
    public static void main(String[] args) throws IOException {
        TuiPrinter pippo = new TuiPrinter();
        pippo.printPrompt("AAAAAAAAAAAAAAAAAAAAAAAA");
        pippo.printWelcomeScreen();
    }

}
