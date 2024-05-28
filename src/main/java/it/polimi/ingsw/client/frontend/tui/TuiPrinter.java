package it.polimi.ingsw.client.frontend.tui;

import java.io.IOException;
import java.util.*;
import org.jline.terminal.Terminal;

import it.polimi.ingsw.utils.*;
import it.polimi.ingsw.gamemodel.*;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.client.frontend.*;

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

        // TODO remove them?
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

    //! PRIVATE METHODS //

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

    public void printCard(ShownCard card) {
        try {
            if (card.coords().equals(new Pair<>(0, 0)))
                System.out.println(parser.parseCard(card.card(), getAbsoluteCoords(card.coords()), null, card.side() == Side.FRONT));
            else
                System.out.println(parser.parseCard(card.card(), getAbsoluteCoords(card.coords()), card.coords(), card.side() == Side.FRONT));
            System.out.println("\033[0m");
        } catch (CardException e) { }
    }

    private void printPoints(String username, Color color, Integer points) {
        // int termRows = this.getHeight();
        // int oldOffset = termRows - infoLineOffset;
        int termCols = this.getWidth();
        int newOffset = 1;
        String out = this.parseUsername(username, color) + "'s points: " + points;
        System.out.println(this.setPosition((termCols - out.length()) / 4, newOffset) + out);
    }
    
    private int getDimStart(int max, int dim) {
        if (dim >= max)
            return 1;

        int left = max - dim; // available space
        if (left % 2 == 1)
            left--;
        return left / 2; // starting coord
    }

    private void printWelcome(int x, int y) {
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

/* 
    private void printTitleDeprecated(int x, int y) {
        String white = "\033[0m", yellow = parser.getRightColor(Symbol.INKWELL);
        List<String> titleString = new ArrayList<>();
        String prefix = setPosition(x, y);

        titleString.add(prefix
                + "   _____                                                                                                                                                                                       _____   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "  ( ___ )-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------( ___ )  " );
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |             ,gggg,                                                         ,ggg, ,ggggggg,                                                                                            |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |           ,88'''Y8b,                     8I                               dP''Y8,8P'''''Y8b                I8                                       ,dPYb,                            |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |          d8'     'Y8                     8I                               Yb, '8dP'     '88                I8                                       IP''Yb                            |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |         d8'   8b  d8                     8I                                ''  88'       88             88888888                                    I8  8I  gg                        |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |        ,8I    'Y88P'                     8I                                    88        88                I8                                       I8  8'  ''                        |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |        I8'             ,ggggg,     ,gggg,8I   ,ggg,      ,gg,   ,gg            88        88    ,gggg,gg    I8    gg      gg   ,gggggg,    ,gggg,gg  I8 dP   gg     ,g,                |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |        d8             dP'  'Y8ggg dP'  'Y8I  i8' '8i    d8''8b,dP'             88        88   dP'  'Y8I    I8    I8      8I     iP'''8I   dP'  'Y8I  I8dP    88    ,8'8,              |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |        Y8,           i8'    ,8I  i8'    ,8I  I8, ,8I   dP   ,88'               88        88  i8'    ,8I   ,I8,   I8,    ,8I   ,8'   8I  i8'    ,8I  I8P     88   ,8'  Yb              |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |        'Yba,,_____, ,d8,   ,d8' ,d8,   ,d8b, 'YbadP' ,dP  ,dP'Y8,              88        Y8,,d8,   ,d8b, ,d88b, ,d8b,  ,d8b,,dP     Y8,,d8,   ,d8b,,d8b,_ _,88,_,8'    8)             |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |          ''Y8888888 P'Y8888P'  PP'Y8888P''Y8888P'Y8888'  dP'   'Y888          888        'Y8P'Y8888P''Y888P''Y888P''Y88P''Y88P      'Y8P'Y8888P''Y88P''Y888P''Y8P'  'Y8P8PP           |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |   |                                                                                                                                                                                       |   |   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "   |___|                                                                                                                                                                                       |___|   ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix
                + "  (_____)-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------(_____)  ");

        System.out.println(yellow);
        for (int i = 0; i < titleString.size(); i++)
            System.out.println(titleString.get(i));
        System.out.println(white);
    }
*/

    private void printTitle(int x, int y, boolean isLittle){
        if (isLittle)
            printLittleTitle(x, y);
        else
            printBigTitle(x, y);
    }

    private void printBigTitle(int x, int y) {
        String white = "\033[0m", yellow = parser.getRightColor(Symbol.INKWELL), bold = "\033[1m";
        List<String> titleString = new ArrayList<>();

        String prefix = setPosition(x, y);
        titleString.add(prefix + "  _____                                                                                                                                                                                                     _____  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + " ( ___ )---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------( ___ ) ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |                                                                                                                                                                                                     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |                                                                                                                                                                                                     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |                                                                                                                                                                                                     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |           ...                         ..                                             ...     ...                      s                                                  ..    .       .x+=:.       |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |        xH88''~ .x8X                 dF                                            .=*8888n..'%888:                   :8                                            x .d88'    @88>    z'    ^%      |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |      :8888   .f'8888Hf        u.   '88bu.                    uL   ..             X    |8888f '8888                  .88       x.    .        .u    .                5888R     %8P        .   <k     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |     :8888>  X8L  ^'''   ...ue888b  '*88888bu        .u     .@88b  @88R           88x. '8888X  8888>        u       :888ooo  .@88k  z88u    .d88B :@8c        u      '888R      .       .@8Ned8'     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |     X8888  X888h        888R Y888r   ^'*8888N    ud8888.  ''Y888k/'*P           '8888k 8888X  ''*8h.    us888u.  -*8888888 ~'8888 ^8888   ='8888f8888r    us888u.    888R    .@88u   .@^%8888'      |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |     88888  |88888.      888R I888>  beWE '888L :888'8888.    Y888L               '8888 X888X .xH8    .@88 '8888'   8888      8888  888R     4888>'88'  .@88 '8888'   888R   ''888E' x88:  ')8b.     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |     88888   %88888      888R I888>  888E  888E d888 '88%'     8888                 '8' X888|:888X    9888  9888    8888      8888  888R     4888> '    9888  9888    888R     888E  8888N=*8888     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |     88888 '> '8888>     888R I888>  888E  888E 8888.+'        '888N               =~'  X888 X888X    9888  9888    8888      8888  888R     4888>      9888  9888    888R     888E   %8'    R88     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |     '8888L %  |888   | u8888cJ888   888E  888F 8888L       .u./'888&               :h. X8*' |888X    9888  9888   .8888Lu=   8888 ,888B .  .d888L .+   9888  9888    888R     888E    @8Wou 9%      |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |      '8888  '-*''   /   '*888*P'   .888N..888  '8888c. .+ d888' Y888*'            X888xX'   '8888..: 9888  9888   ^%888*    '8888Y 8888'   ^'8888*'    9888  9888   .888B .   888&  .888888P'       |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |        '888.      :'      'Y'       ''888*''    '88888%   ' 'Y   Y'             :~'888f     '*888*'  '888*''888'    'Y'      'Y'   'YP        'Y'      '888*''888'  ^*888%    R888' '   ^'F         |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |          '''***~''                     ''         'YP'                              ''        '''     ^Y'   ^Y'                                         ^Y'   ^Y'     '%       ''                   |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |                                                                                                                                                                                                     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |   |                                                                                                                                                                                                     |   |  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  |___|                                                                                                                                                                                                     |___|  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + " (_____)---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------(_____) ");

        System.out.println(bold + yellow);
        for (int i = 0; i < titleString.size(); i++)
            System.out.println(titleString.get(i));
        System.out.println(white);
    }

    private void printLittleTitle(int x, int y){
        String white = "\033[0m", yellow = parser.getRightColor(Symbol.INKWELL), bold = "\033[1m";
        List<String> titleString = new ArrayList<>();
        
        String prefix = setPosition(x, y);
        titleString.add(prefix + "    ______                __                         _   __            __                               __   _         ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "   / ____/  ____     ____/ /  ___     _  __         / | / /  ____ _   / /_   __  __   _____   ____ _   / /  (_)  _____ ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + "  / /      / __ \\   / __  /  / _ \\   | |/_/        /  |/ /  / __  /  / __/  / / / /  / ___/  / __  /  / /  / /  / ___/ ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + " / /___   / /_/ /  / /_/ /  /  __/  _>  <         / /|  /  / /_/ /  / /_   / /_/ /  / /     / /_/ /  / /  / /  (__  )  ");
        prefix = setPosition(x, ++y);
        titleString.add(prefix + " \\____/   \\____/   \\__,_/   \\___/  /_/|_|        /_/ |_/   \\__,_/   \\__/   \\__,_/  /_/      \\__,_/  /_/  /_/  /____/   ");

        System.out.println(bold + yellow);
        for (int i = 0; i < titleString.size(); i++)
            System.out.println(titleString.get(i));
        System.out.println(white);
    }

    private void printYouWinScreen(int x, int y){
        List<String> winString = new ArrayList<>();

        String prefix = setPosition(x, y);
        winString.add(prefix + " '7MMF'   '7MF''7MMF' .g8'''bgd MMP''MM''YMM   .g8''8q. '7MM'''Mq.'YMM'   'MM' ");
        prefix = setPosition(x, ++y);
        winString.add(prefix + "   'MA     ,V    MM .dP'     'M P'   MM   '7 .dP'    'YM. MM   'MM. VMA   ,V   ");
        prefix = setPosition(x, ++y);
        winString.add(prefix + "    VM:   ,V     MM dM'       '      MM      dM'      'MM MM   ,M9   VMA ,V    ");
        prefix = setPosition(x, ++y);
        winString.add(prefix + "     MM.  M'     MM MM               MM      MM        MM MMmmdM9     VMMP     ");
        prefix = setPosition(x, ++y);
        winString.add(prefix + "     'MM A'      MM MM.              MM      MM.      ,MP MM  YM.      MM      ");
        prefix = setPosition(x, ++y);
        winString.add(prefix + "      :MM;       MM 'Mb.     ,'      MM      'Mb.    ,dP' MM   'Mb.    MM      ");
        prefix = setPosition(x, ++y);
        winString.add(prefix + "       VF      .JMML. ''bmmmd'     .JMML.      ''bmmd'' .JMML. .JMM. .JMML.    ");

        for (int i = 0; i < winString.size(); i++)
            System.out.println(winString.get(i));
    }

    private void printYouLoseScreen(int x, int y){
        List<String> loseString = new ArrayList<>();

        String prefix = setPosition(x, y);
        loseString.add(prefix + " @@@ @@@   @@@@@@   @@@  @@@     @@@        @@@@@@    @@@@@@   @@@@@@@ ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + " @@@ @@@  @@@@@@@@  @@@  @@@     @@@       @@@@@@@@  @@@@@@@   @@@@@@@ ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + " @@ì ì@@  @@ì  @@@  @@ì  @@@     @@ì       @@ì  @@@  ì@@       @@ì     ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + " ì@ì @ìì  ì@ì  @ì@  ì@ì  @ì@     ì@ì       ì@ì  @ì@  ì@ì       ì@ì     ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + "  ì@ì@ì   @ì@  ì@ì  @ì@  ì@ì     @ìì       @ì@  ì@ì  ìì@@ìì    @ììì:ì  ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + "   @ììì   ì@ì  ììì  ì@ì  ììì     ììì       ì@ì  ììì   ìì@ììì   ììììì:  ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + "   ìì:    ìì:  ììì  ìì:  ììì     ìì:       ìì:  ììì       ì:ì  ìì:     ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + "   :ì:    :ì:  ì:ì  :ì:  ì:ì      :ì:      :ì:  ì:ì      ì:ì   :ì:     ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + "    ::    ::::: ::  ::::: ::     :: ::::   ::::: ::  :::: ::   :: :::: ");
        prefix = setPosition(x, ++y);
        loseString.add(prefix + "    :      : :  :    : :  :      : :: : :   : :  :   :: : :    : :: :: ");

        for (int i = 0; i < loseString.size(); i++)
            System.out.println(loseString.get(i));
    }

    private void printDeck(Pair<Integer, Integer> coord, Symbol reign, DrawSource deckType){
        if (reign == null)
            return;
        String bianco = "\033[0m";
        int xx = 8, yy = 7+1;

        List<String> underCover = new ArrayList<>();
        int x = coord.first(), y = coord.second();

        String prefix = setPosition(x, y);
        underCover.add(prefix + "┌────────────────┐");
        prefix = setPosition(x, ++y);
        underCover.add(prefix + "│                │");
        prefix = setPosition(x, ++y);
        underCover.add(prefix + "│                │");
        prefix = setPosition(x, ++y);
        underCover.add(prefix + "│                │");
        prefix = setPosition(x, ++y);
        underCover.add(prefix + "│                │");
        prefix = setPosition(x, ++y);
        underCover.add(prefix + "└────────────────┘");

        System.out.println(bianco);
        for (String s : underCover)
            System.out.println(s);

        x = coord.first() + 2;
        y = coord.second() - 1;
        String topDeckCar = this.parser.getGenericBack(reign, new Pair<>(x, y));
        System.out.println(topDeckCar);

        if (deckType == DrawSource.GOLDS_DECK)
            System.out.println(setPosition(x+xx, y-1-1) + getCardIndex(deckType));
        if (deckType == DrawSource.RESOURCES_DECK)
            System.out.println(setPosition(x+xx, y+yy+1) + getCardIndex(deckType));

    }

    private void printDeckVisibleCard(Pair<Integer, Integer> coord, Map<DrawSource, PlayableCard> visiblePlayableCards){
        String bianco = "\033[0m";

        // obtain card obj
        PlayableCard firstG = visiblePlayableCards.get(DrawSource.FIRST_VISIBLE),
                secondG = visiblePlayableCards.get(DrawSource.SECOND_VISIBLE),
                thirdR = visiblePlayableCards.get(DrawSource.THIRD_VISIBLE),
                fourthR = visiblePlayableCards.get(DrawSource.FOURTH_VISIBLE);

        // obtain card coord
        int x = coord.first(), y = coord.second();
        int xOffset = 18 + 2, yOffset = 6 + 2;
        Pair<Integer, Integer> firstCoord = new Pair<>(x, y),
                secondCoord = new Pair<>(x + xOffset, y),
                thirdCoord = new Pair<>(x, y + yOffset),
                fourthCoord = new Pair<>(x + xOffset, y + yOffset);

        // obtain card as string (if it exists)
        try {
            String firstToPrint;
            String secondToPrint;
            String thirdToPrint;
            String fourthToPrint;
            int xx = 8, yy = 6;

            if (firstG != null){
                firstToPrint = this.parser.parseCard(firstG, firstCoord, null, true);
                System.out.println(firstToPrint);
                System.out.println(setPosition(firstCoord.first()+xx, firstCoord.second()-1-2) + getCardIndex(DrawSource.FIRST_VISIBLE));
                System.out.println(bianco);
            }
            if (secondG != null){
                secondToPrint = this.parser.parseCard(secondG, secondCoord, null, true);
                System.out.println(secondToPrint);
                System.out.println(setPosition(secondCoord.first()+xx, secondCoord.second()-1-2) + getCardIndex(DrawSource.SECOND_VISIBLE));
                System.out.println(bianco);
            }
            if (thirdR != null){
                thirdToPrint = this.parser.parseCard(thirdR, thirdCoord, null, true);
                System.out.println(thirdToPrint);
                System.out.println(setPosition(thirdCoord.first()+xx, thirdCoord.second()+yy+1+1) + getCardIndex(DrawSource.THIRD_VISIBLE));
                System.out.println(bianco);
            }
            if (fourthR != null){
                fourthToPrint = this.parser.parseCard(fourthR, fourthCoord, null, true);
                System.out.println(fourthToPrint);
                System.out.println(setPosition(fourthCoord.first()+xx, fourthCoord.second()+yy+1+1) + getCardIndex(DrawSource.FOURTH_VISIBLE));
                System.out.println(bianco);
            }

        } catch (CardException e) {
            throw new RuntimeException(e);
        }

    }

    private char getCardIndex(DrawSource drawSource){
        return switch (drawSource){
            case FIRST_VISIBLE -> '1';
            case SECOND_VISIBLE -> '2';
            case THIRD_VISIBLE -> '3';
            case FOURTH_VISIBLE -> '4';
            case GOLDS_DECK -> 'G';
            case RESOURCES_DECK -> 'R';
            default -> '0';
        };
    }

    private String repeatChar(char c, int times){

        StringBuilder s = new StringBuilder();
        for (int i = 0; i < times; i++)
            s.append(c);
        return s.toString();
    }

    private void boxBuilder(int totalWidth, StringBuffer upperBorder, StringBuffer middleBorder, StringBuffer lowerBorder){
        // int yCoord = (heightOffset > 0) ? heightOffset : 1;
        // int maxWidth = 43+2;
        int lineWidth = totalWidth - 2;
        // int textWidth = totalWidth - 4;
        // int xCoord = getDimStart(getWidth(), totalWidth); 
        // String prefix = setPosition(xCoord, yCoord);
            
        // define the border template
        upperBorder.append("╔");
        upperBorder.append(repeatChar('═', lineWidth));
        upperBorder.append("╗");
        
        middleBorder.append("╠");
        middleBorder.append(repeatChar('═', lineWidth));
        middleBorder.append("╣");
        
        lowerBorder.append("╚");
        lowerBorder.append(repeatChar('═', lineWidth));
        lowerBorder.append("╝");
    }

    private int getMaxElemLen(List<String> stringList) {
        int max = 0;
        for (String s : stringList){
            if (s.length() > max)
                max = s.length();
        }
        return max;
    }

    //! PUBLIC METHODS //

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
        System.out.print(this.setPosition(1, termRows - infoLineOffset + 1) + customMessage + " ");
        System.out.flush();
    }

    public void printMessage(List<String> message) {
        int termRows = this.getHeight();
        Integer offset = 0;
        for (String line : message) {
            System.out.println(this.setPosition(1, termRows - infoLineOffset - offset) + line);
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
     * Prints all the players' available resources (from the board)
     * @param availableResources map from the type of resource (Symbol) to its quantity (Integer)
     * @param verticalOffset offset lines from the top (default is 1)
     */
    public void printAvailableResources(Map<Symbol, Integer> availableResources, Integer verticalOffset) {
        // int termRows = this.getHeight(); 
        int vertCoord = (verticalOffset != 1) ? 1 : verticalOffset;
        int termCols = this.getWidth();
        String out = "";
        String spaces = "    ";
        Integer len = availableResources.keySet().size() * (5 + spaces.length()); // icon, space, :, space, number
        List<Symbol> toPrint =
                List.of(Symbol.PLANT, Symbol.INSECT, Symbol.FUNGUS, Symbol.ANIMAL, Symbol.PARCHMENT, Symbol.FEATHER, Symbol.INKWELL);

        for (Symbol resource : toPrint) {
            out += parser.getRightColor(resource) + parser.getRightIcon(resource) + ": " + availableResources.get(resource) + spaces;
        }

        System.out.println(this.setPosition((termCols - len) / 2, vertCoord) + out + "\033[0m");
    }

    /**
     * Prints the whole board, including username, points and resources
     *
     * @param username The username to print
     * @param board the board to be printed
     */
    public void printPlayerBoard(String username, ClientBoard board) {
        // this.clearTerminal();
        if (board == null) {
            this.printMessage("No such player exists!");
            return;
        }
        Map<Integer, ShownCard> placed = board.getPlaced();
        for (Integer turn : placed.keySet()) {
            this.printCard(placed.get(turn));
        }
        this.printAvailableResources(board.getAvailableResources(), this.getHeight() - infoLineOffset);
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

        Integer last = (termCols - (handSize) * (cardCols)) / 2 - spaces * (handSize - 1) / 2;
        for (PlayableCard card : hand) {
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
        // Integer visiblesSize = 2;
        // Integer spaces = 4;
        Integer strlen;

        strlen = ("Your secret objective").length();
        username = this.parseUsername("Your", color) + " secret objective:";
        Integer last = (termCols - strlen) / 2;
        System.out.println(this.setPosition(last, 1) + username);

        last = (termCols - cardCols) / 2;
        System.out.println(parser.parseObjective(secret, new Pair<Integer, Integer>(last, 2)) + "\033[0m");

        int verticalSpaceAlreadyUsedForSecretObjective = (7) + 1 + 1;
        printObjectivePair("Common objectives:", visibles, verticalSpaceAlreadyUsedForSecretObjective);

    }

    /**
     * Prints a pair of objectives, with a brief description above them
     * @param pairObjectives pair of objectives
     * @param heightOffset offset lines from the top (default is 1)
     */
    public void printObjectivePair(String message, Pair<Objective, Objective> pairObjectives, int heightOffset){
        int yOffset = (heightOffset <= 0) ? 1 : heightOffset;

        // common objectives STRING
        int xCoord = getDimStart(this.terminal.getWidth(), message.length());
        message = setPosition(xCoord, yOffset++) + message;
        System.out.println(message);

        // common objectives CARDS
        int cardWidth = 18, spaceBetweenSides = 4;
        xCoord = getDimStart(this.getWidth(), (2 * cardWidth) + spaceBetweenSides);

        Pair<Integer, Integer> obj1Coord = new Pair<>(xCoord, yOffset);
        Pair<Integer, Integer> obj2Coord = new Pair<>(xCoord + cardWidth + spaceBetweenSides, yOffset);
        String obj1 = this.parser.parseObjective(pairObjectives.first(), obj1Coord);
        String obj2 = this.parser.parseObjective(pairObjectives.second(), obj2Coord);

        System.out.println(obj1 + obj2);
    }

    /**
     * Prints the message history of the most recent messages
     *
     * @param chat chat object, as a list of strings
     */
    public void printChat(List<String> chat) {
        
        printSimpleList(chat, false, false);
        // int rows = this.getHeight() - infoLineOffset + 1;
        // int start = chat.size() - rows;
        // if (start < 0) {
        //     start = 0;
        // }
        // for (int i = start; i < chat.size(); i++) {
        //     System.out.println(this.setPosition(1, i - start) + chat.get(i));            
        // }
    }

    /**
     * Prints a list of available commands
     */
    public void printHelp() {
        // String prefix = "Command used to";
        String prefix = "";
        int maxLen = this.getHeight() - infoLineOffset + 1;
        int y = maxLen - this.commandList.size();

        for (Pair<String, String> command : this.commandList.keySet()) {
            System.out.printf("%s%-15s %2s: %s %s", this.setPosition(1, y), command.first(), command.second(), prefix, this.commandList.get(command));
            y++;

        }
    }

    /**
     * Prints the welcome screen in the middle of the tui view
     */
    public void printWelcomeScreen() {
        
        // get title and welcome sizes 
        int welcomeHeight = 5, welcomeWidth = 88+2; // width must be even (pari)
        int spaceBetween = 3;
        int titleHeight = 21, titleWidth = 210+2; // width must be even (pari)
        int verticalOffset = -10;
        
        boolean isLittle = (this.terminal.getWidth() < titleWidth || this.terminal.getHeight() < welcomeHeight - verticalOffset + titleHeight) ? true : false;
        if (isLittle){
            titleHeight = 5;
            titleWidth = 118 + 2;
            verticalOffset = -1;
        }
        
        // get coordinates 
        int welcomeStartY = getDimStart(this.getHeight(), welcomeHeight + spaceBetween + titleHeight);
        int titleStartY = welcomeStartY + welcomeHeight + spaceBetween;

        int welcomeStartX = getDimStart(this.getWidth(), welcomeWidth);
        int titleStartX = getDimStart(this.getWidth(), titleWidth);
        
        // print welcome and title 
        printWelcome(welcomeStartX, welcomeStartY + verticalOffset);
        printTitle(titleStartX, titleStartY + verticalOffset, isLittle);
    }

    /**
     * Prints the end screen (win/lose) in the middle of the tui view
     * @param isVictorious whether the player won or not
     */
    public void printEndScreen(Boolean isVictorious){
        int maxHeight = this.getHeight() - this.infoLineOffset;
        int maxWidth = this.getWidth() - 2;

        int msgHeight, msgWidth;
        int x, y;
        int verticalOffset = -10;
        if (isVictorious){
            msgHeight = 7;
            msgWidth = 78+2; // width must be even (pari)
            x = getDimStart(maxWidth, msgWidth);
            y = getDimStart(maxHeight, msgHeight);
            printYouWinScreen(x, y + verticalOffset);

        } else {
            msgHeight = 10;
            msgWidth = 70+2; // width must be even (pari)
            x = getDimStart(maxWidth, msgWidth);
            y = getDimStart(maxHeight, msgHeight);
            printYouLoseScreen(x, y + verticalOffset);

        }
    }

    /**
     * Prints the specified initial card front and back in the middle of the screen
     * 
     * @param initialCard initial card to print
     * @param heightOffset offset lines from the top (default is 1)
     */
    public void printInitialSideBySide(InitialCard initialCard, int heightOffset) {

        int offset = (heightOffset <= 0) ? 1 : heightOffset;
        String faceup, facedown;
        int cardWidth = 18, spaceBetweenSides = 4;
        int width = getDimStart(this.getWidth(), (2 * cardWidth) + spaceBetweenSides);

        Pair<Integer, Integer> faceupCoord = new Pair<>(width, offset);
        Pair<Integer, Integer> facedownCoord = new Pair<>(width + cardWidth + spaceBetweenSides, offset);

        try {
            faceup = this.parser.parseCard(initialCard, faceupCoord, null, true);
            facedown = this.parser.parseCard(initialCard, facedownCoord, null, false);
            System.out.println(faceup + facedown);
        } catch (CardException e) {
            // TODO: handle exception ?
        }
    }

    /**
     * Prints the specified initial card front and back in the middle of the screen
     * 
     * @param playableCard gold/resource card to print
     * @param heightOffset offset lines from the top. For default (y-centered) must be 0
     */
    public void printPlayableFrontAndBack(PlayableCard playableCard, int heightOffset){
        int yCoord = (heightOffset > 0) ? heightOffset : getDimStart(getHeight()-1, 6);
        int cardWidth = 18, spaceBetweenSides = 4;
        int xCoord = getDimStart(getWidth(), cardWidth + spaceBetweenSides + cardWidth);

        String faceUp, faceDown, bianco = "\033[0m";
        Pair<Integer, Integer> faceupCoord = new Pair<>(xCoord, yCoord);
        Pair<Integer, Integer> facedownCoord = new Pair<>(xCoord + cardWidth + spaceBetweenSides, yCoord);

        try {
           faceUp = this.parser.parseCard(playableCard, faceupCoord, null, true);
           faceDown = this.parser.parseCard(playableCard, facedownCoord, null, false);
           System.out.println(faceUp + faceDown + bianco);
        } catch (Exception e) {
            // TODO: handle exception ?
        }
    }

    /**
     * Prints a one-line message in the center of the screen
     * @param message message to display
     * @param heightOffset offset lines from the top. For default (y-centered) must be 0
     */
    public void printCenteredMessage(String message, int heightOffset){
        int maxWidth = message.length();
        if (maxWidth > getWidth() - 4)
            return;

        String resetStyle = "\033[0m", style = "\033[1m";

        // int yCoord = getDimStart(this.terminal.getHeight(), 3);
        int yCoord = (heightOffset > 0) ? heightOffset : getDimStart(this.terminal.getHeight(), 3);
        int xCoord = getDimStart(this.terminal.getWidth(), maxWidth + 4);
        String prefix = setPosition(xCoord, yCoord);

        // define the border template
        StringBuffer upperBorder    = new StringBuffer("╔");
        upperBorder.append(repeatChar('═', maxWidth+2));
        upperBorder.append("╗");
        StringBuffer center  = new StringBuffer("║ ");
        center.append(style + message + resetStyle);
        center.append(" ║");
        StringBuffer lowerBorder    = new StringBuffer("╚");
        lowerBorder.append(repeatChar('═', maxWidth+2));
        lowerBorder.append("╝");

        // print 
        System.out.println(prefix + upperBorder.toString());
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + center.toString());
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + lowerBorder.toString());

    }

    /**
     * Prints the drawing screen, containing the 2 decks and the 4 visible cards. Unavailable resources are not displayed.
     * @param decksTopReign pair of the 2 top-deck cards
     * @param visiblePlayableCards map of visible cards
     */
    public void printDrawingScreen(Pair<Symbol, Symbol> decksTopReign, Map<DrawSource, PlayableCard> visiblePlayableCards) {
        // int maxHeight = this.getHeight() - this.infoLineOffset;
        int deckHeight = 6 + 2, deckWidth = 18 + 1; // width must be even (pari)
        int xSpaceBetween = 12, ySpaceBetween = 0;
        int cardsHeight = 6 + 2, cardsWidth = 18 + 2 + 18; // width must be even (pari)

        int deckStartX = getDimStart(this.getWidth(), deckWidth + xSpaceBetween + cardsWidth);
        int deckStartY = getDimStart(this.getHeight(), deckHeight + ySpaceBetween + cardsHeight);
        int cardsStartX = deckStartX + deckWidth + xSpaceBetween;
        int cardsStartY = deckStartY;

        printDeck(new Pair<>(deckStartX, deckStartY), decksTopReign.first(), DrawSource.GOLDS_DECK);
        printDeck(new Pair<>(deckStartX, deckStartY + deckHeight + ySpaceBetween), decksTopReign.second(), DrawSource.RESOURCES_DECK);
        printDeckVisibleCard(new Pair<>(cardsStartX, cardsStartY), visiblePlayableCards);
    }

    /**
     * Prints the list of matches (joinable or not) in the center of the screen. It can print a maximum of 99 matches.
     * @param availableMatches list of available matches
     * @param heightOffset offset lines from the top (default is 1)
     */
    public void printMatchesLobby(List<AvailableMatch> joinableMatches, List<AvailableMatch> unavailableMatches, int heightOffset){
        int yCoord = (heightOffset > 0) ? heightOffset : 1;
        int maxWidth = 45;
        int xCoord = getDimStart(getWidth(), maxWidth); 
        String prefix = setPosition(xCoord, yCoord);
            
        // define the border template
        StringBuffer upperBorder = new StringBuffer();
        StringBuffer middleBorder = new StringBuffer();
        StringBuffer lowerBorder = new StringBuffer();
        boxBuilder(maxWidth, upperBorder, middleBorder, lowerBorder);

        // print upper and middle border
        System.out.println(prefix + upperBorder.toString());
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + "║      \033[1mMatches                        Slots\033[0m ║");            // manually adjust according to maxWidth
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + middleBorder.toString());
        prefix = setPosition(xCoord, ++yCoord);
        

        // print list of joinable matches
        String white = "\033[0m", green = "\033[32m";
        int matchIndex = 1;
        for (AvailableMatch m1 : joinableMatches){

            // color = (match.maxPlayers() == match.currentPlayers()) ? "\033[31m" : "\033[35m";
            System.out.printf("%s║ [%02d] %s%-31s %s/%s%s  ║", prefix, matchIndex, green, m1.name().toString(), m1.currentPlayers().toString(), m1.maxPlayers().toString(), white);     // manually adjust according to maxWidth
            prefix = setPosition(xCoord, ++yCoord);
            matchIndex++;
        }

        // print list of unavailable matches
        String red = "\033[31m";
        for (AvailableMatch m2 : unavailableMatches){

            System.out.printf("%s║ [--] %s%-31s %s/%s%s  ║", prefix, red, m2.name().toString(), m2.currentPlayers().toString(), m2.maxPlayers().toString(), white);     // manually adjust according to maxWidth
            prefix = setPosition(xCoord, ++yCoord);
        }

        // print lower border
        System.out.print(prefix + lowerBorder.toString());
    }

    /**
     * Prints the scoreboard at the end of the match
     * @param playerToPoints map from player name as string, to total points as integer
     * @param heightOffset offset lines from the top (default is 1)
     */
    public void printScoreboard(Map<String, Integer> playerToPoints, int heightOffset){
        int yCoord = (heightOffset > 0) ? heightOffset : 1;
        int maxWidth = 38+2;
        int xCoord = getDimStart(getWidth(), maxWidth); 
        String prefix = setPosition(xCoord, yCoord);
        
        // ? order the players by points
        // ? bold + color for the current player?
        
        // define the border template
        StringBuffer upperBorder    = new StringBuffer("╔");
        upperBorder.append(repeatChar('═', maxWidth-2));
        upperBorder.append("╗");
        StringBuffer middleBorder  = new StringBuffer("╠");
        middleBorder.append(repeatChar('═', maxWidth-2));
        middleBorder.append("╣");
        StringBuffer lowerBorder    = new StringBuffer("╚");
        lowerBorder.append(repeatChar('═', maxWidth-2));
        lowerBorder.append("╝");

        // print upper and middle border
        System.out.println(prefix + upperBorder.toString());
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + "║ \033[1mPlayer                         Score\033[0m ║");            // manually adjust according to maxWidth
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + middleBorder.toString());
        prefix = setPosition(xCoord, ++yCoord);
        
        // print list of players
        for (String s : playerToPoints.keySet()){
            System.out.printf("%s║ %-31s%4s  ║", prefix, s, playerToPoints.get(s).toString());     // manually adjust according to maxWidth
            prefix = setPosition(xCoord, ++yCoord);    
        }

        // print lower border
        System.out.print(prefix + lowerBorder.toString());
    }

    /**
     * Prints the hand of the player at the bottom of the screen
     * @param hand list of the 3 playable cards (hand)
     */
    public void printHandAtBottom(List<PlayableCard> hand) {
        int termCols = this.getWidth();
        Integer handSize = hand.size();
        Integer spaces = 4;

        Integer last = (termCols - (handSize) * (cardCols)) / 2 - spaces * (handSize - 1) / 2;
        Integer row = this.getHeight()-TuiPrinter.cardRows-3;

        for (PlayableCard card : hand) {
            try {
                System.out.println(parser.parseCard(card, new Pair<Integer, Integer>(last, row), null, true) + "\033[0m");
                last += cardCols + spaces;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Prints a list of strings
     * @param stringList list of strings
     * @param msg message to display
     * @param textColor color of the players' name (red or green)
     * @param isCentered if the list has to be centered in the tui or not
     */
    public void printListOfString(List<String> stringList, String msg, Color textColor, Boolean isCentered) {
        int maxWidth = 40;  //leave it even
        int xCoord = getDimStart(getWidth(), maxWidth); 
        int yCoord = (isCentered) ? getDimStart(terminal.getHeight(), stringList.size()+4) : 1;
        String bold = "\033[1m", reset = "\033[0m", color;
        switch (textColor) {
            case RED:
                color = "\033[31m";
                break;
            case GREEN:
                color = "\033[32m";
                break;
            default:
                color = reset; 
                break;
        }

        // define the border template
        StringBuffer upperBorder = new StringBuffer();
        StringBuffer middleBorder = new StringBuffer();
        StringBuffer lowerBorder = new StringBuffer();
        boxBuilder(maxWidth, upperBorder, middleBorder, lowerBorder);

        // define title of the table
        // String msg = "Connected Players";
        StringBuffer title = new StringBuffer();
        title.append("║").append(bold);
        if (msg.length() % 2 != 0){
            title.append(repeatChar(' ', (maxWidth - msg.length() - 2) / 2));
            title.append(msg);
            title.append(repeatChar(' ', ((maxWidth - msg.length() - 2) / 2) + 1));
    
        } else {
            title.append(repeatChar(' ', (maxWidth - msg.length() - 2) / 2));
            title.append(msg);
            title.append(repeatChar(' ', (maxWidth - msg.length() - 2) / 2));    
        }
        title.append("║").append(reset);
        
        // define players' lines
        List<String> playerList = new ArrayList<>();
        for (String s : stringList) {

            StringBuffer player = new StringBuffer();
            player.append("║").append(color);
            if (s.length() % 2 != 0){
                player.append(repeatChar(' ', (maxWidth - s.length() - 2) / 2));
                player.append(s);
                player.append(repeatChar(' ', ((maxWidth - s.length() - 2) / 2) + 1));

            } else {
                player.append(repeatChar(' ', (maxWidth - s.length() - 2) / 2));
                player.append(s);
                player.append(repeatChar(' ', (maxWidth - s.length() - 2) / 2));
            }
            player.append(reset).append("║");

            playerList.add(player.toString());
        }

        // print it all
        String prefix = setPosition(xCoord, yCoord);
        System.out.println(prefix + upperBorder.toString());
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + title.toString());
        prefix = setPosition(xCoord, ++yCoord);
        System.out.println(prefix + middleBorder.toString());
        prefix = setPosition(xCoord, ++yCoord);
        for (String s : playerList) {
            System.out.println(prefix + s);
            prefix = setPosition(xCoord, ++yCoord);
        }
        System.out.println(prefix + lowerBorder);
        
    }
    
    /**
     * Prints a list, simple or numbered, either in the center or in the bottom left of the tui
     * @param stringList list of strings
     * @param isCentered if you want it centered or not
     * @param isNumbered if you want it numbered or not
     */
    public void printSimpleList(List<String> stringList, Boolean isCentered, Boolean isNumbered){
        // coords with case
        int xCoord = (isCentered) ? getDimStart(getWidth(), getMaxElemLen(stringList)) : 1; 
        int yCoord = (isCentered) ? (terminal.getHeight() - stringList.size()) : (this.getHeight() - infoLineOffset + 1 - stringList.size());
        int index = 0;
        if (yCoord <= 0){
            index = -yCoord+2;
            yCoord = 1;
        }
    
        // printing phase
        int i = 1;
        String prefix = setPosition(xCoord, yCoord);
        for (; index < stringList.size(); index++){
            if (isNumbered)
                System.out.println(prefix + String.valueOf(i++) + ") " + stringList.get(index));
            else
                System.out.println(prefix + stringList.get(index));
            
            prefix = setPosition(xCoord, ++yCoord);
        }
    }

}