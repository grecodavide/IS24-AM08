package it.polimi.ingsw.utils;

import java.io.IOException;
import java.util.*;
import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.gamemodel.*;


public class TUICardPrinter {
    Map<Integer, InitialCard> initialCards;
    Map<Integer, Objective> objectives;
    Map<Integer, GoldCard> goldCards;
    Map<Integer, ResourceCard> resourceCards;

    public TUICardPrinter() {
        CardsManager cardsManager = CardsManager.getInstance();

        initialCards = cardsManager.getInitialCards();
        objectives = cardsManager.getObjectives();
        goldCards = cardsManager.getGoldCards();
        resourceCards = cardsManager.getResourceCards();

    }


    /**
     * Generates a {@link PlayableCard} as a string to be printed
     * 
     * @param id the card id
     * @param coord where to place the card (relative to the terminal, not {@link Board})
     * @param isFacingUp wheter the card is facing up or down
     * 
     * @returns the string representing the card
     * 
     * @throws CardException if the card type is not known
     */
    public String getCardString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp) throws CardException {
        if (resourceCards.get(id) != null)
            return parseCard(resourceCards.get(id), coord, isFacingUp);
        else if (goldCards.get(id) != null)
            return parseCard(goldCards.get(id), coord, isFacingUp);

        throw new CardException("Invalid card type: " + initialCards.get(id).getClass() + "or" + objectives.get(id).getClass() + "!");
    }

    /**
     * Generates a {@link InitialCard} as a string to be printed
     * 
     * @param id the card id
     * @param coord where to place the card (relative to the terminal, not {@link Board})
     * @param isFacingUp wheter the card is facing up or down
     * 
     * @returns the string representing the card
     * 
     * @throws CardException if the card type is not known
     */
    public String getInitialString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp) throws CardException {
        if (initialCards.get(id) == null)
            throw new CardException("Invalid card type!");

        return parseCard(initialCards.get(id), coord, isFacingUp);
    }

    /**
     * Generates a {@link Objective} as a string to be printed
     * 
     * @param id the card id
     * @param coord where to place the card (relative to the terminal, not {@link Board})
     * 
     * @returns the string representing the card
     * 
     * @throws CardException if the card type is not known
     */
    public String getObjectiveString(int id, Pair<Integer, Integer> coord) throws CardException {
        if (objectives.get(id) == null)
            throw new CardException("Invalid card type!");

        return parseObjective(objectives.get(id), coord);
    }

    // NO JAVADOC

    // PARSERS
    private String parseCard(Card card, Pair<Integer, Integer> coord, Boolean isFacingUp) throws CardException {

        // acquire information
        Map<Corner, Symbol> cornersToProcess = new HashMap<>();
        acquireCornerSymbols(cornersToProcess, card, isFacingUp);

        Set<Symbol> centerToProcess = new HashSet<>();
        acquireCenterSymbols(centerToProcess, card, isFacingUp);

        String cardColor;
        switch (card) {
            case InitialCard initialCard -> cardColor = "\033[0m";
            case GoldCard goldCard -> cardColor = getRightColor(goldCard.getReign());
            case ResourceCard resourceCard -> cardColor = getRightColor(resourceCard.getReign());
            default -> throw new CardException("Invalid card type: " + card.getClass() + "!");
        }


        // process information
        Map<Corner, List<String>> cornersAsString = new HashMap<>();
        processCorners(cornersAsString, cornersToProcess, coord, cardColor);

        Map<Integer, String> centerAsString = new HashMap<>();
        switch (card) {
            case InitialCard initialCard -> processCenter(centerAsString, centerToProcess, initialCard);
            case GoldCard goldCard -> processCenter(centerAsString, centerToProcess, goldCard);
            case ResourceCard resourceCard -> processCenter(centerAsString, centerToProcess, resourceCard);
            default -> throw new CardException("Invalid card type: " + card.getClass() + "!");
        }

        // assemble the string
        String newLine = "\n";
        StringBuilder printableCard = new StringBuilder(cardColor).append(newLine);
        assembleCard(printableCard, cornersAsString, centerAsString); // if it doesn't work, use the safe version assembleCardSafe
        printableCard.append(newLine);

        // card assembled
        return printableCard.toString();
    }

    private String parseObjective(Objective card, Pair<Integer, Integer> coord) {

        // process information
        Map<Corner, List<String>> cornersAsString = new HashMap<>();
        processObjectiveCorners(cornersAsString, coord);

        Map<Integer, String> centerAsString = new HashMap<>();
        processObjectiveCenter(centerAsString, card);

        // assemble the string
        String newLine = "\n", cardColor = "\033[0m";
        StringBuilder printableCard = new StringBuilder(cardColor).append(newLine);
        assembleCard(printableCard, cornersAsString, centerAsString); // if it doesn't work, use the safe version assembleCardSafe
        printableCard.append(newLine);

        // card assembled
        return printableCard.toString();
    }


    // ASSEMBLERERS

    // cool algorithmic version
    private void assembleCard(StringBuilder printableCard, Map<Corner, List<String>> cornersAsString, Map<Integer, String> centerAsString) {

        List<Corner> left = new ArrayList<>(), right = new ArrayList<>();
        left.add(Corner.TOP_LEFT);
        left.add(Corner.BOTTOM_LEFT);
        right.add(Corner.TOP_RIGHT);
        right.add(Corner.BOTTOM_RIGHT);

        int i, j, k;
        for (i = 0; i < 6; i++) {
            j = (i <= 2) ? 0 : 1;
            k = i % 3;

            printableCard.append(cornersAsString.get(left.get(j)).get(k));
            printableCard.append(centerAsString.get(i));
            printableCard.append(cornersAsString.get(right.get(j)).get(k));
        }

    }


    // ACQUIRES

    private void acquireCornerSymbols(Map<Corner, Symbol> cornersToProcess, Card card, Boolean isFacingUp) {
        try {
            Side side = (isFacingUp) ? Side.FRONT : Side.BACK;
            Symbol topLeft = card.getSide(side).getCorner(Corner.TOP_LEFT);
            Symbol topRight = card.getSide(side).getCorner(Corner.TOP_RIGHT);
            Symbol bottomLeft = card.getSide(side).getCorner(Corner.BOTTOM_LEFT);
            Symbol bottomRight = card.getSide(side).getCorner(Corner.BOTTOM_RIGHT);

            cornersToProcess.put(Corner.TOP_LEFT, topLeft);
            cornersToProcess.put(Corner.TOP_RIGHT, topRight);
            cornersToProcess.put(Corner.BOTTOM_LEFT, bottomLeft);
            cornersToProcess.put(Corner.BOTTOM_RIGHT, bottomRight);
        } catch (CardException e) {
            throw new RuntimeException(e);
        }
    }

    private void acquireCenterSymbols(Set<Symbol> centerToProcess, Card card, Boolean isFacingUp) {
        Side side = (isFacingUp) ? Side.FRONT : Side.BACK;
        centerToProcess.addAll(card.getSide(side).getCenter());
    }


    // PROCESSERS

    private void processCorners(Map<Corner, List<String>> cornersAsString, Map<Corner, Symbol> cornersToProcess,
            Pair<Integer, Integer> coord, String borderColor) {

        List<String> singleCorner;
        String suffix;

        for (Corner corner : cornersToProcess.keySet()) {
            singleCorner = new ArrayList<>();

            switch (corner) {
                case Corner.TOP_LEFT:
                    suffix = "";
                    singleCorner.addAll(getTopLeftCorner(coord, cornersToProcess.get(corner), suffix, borderColor));
                    break;

                case Corner.TOP_RIGHT:
                    suffix = "\n";
                    singleCorner.addAll(getTopRightCorner(coord, cornersToProcess.get(corner), suffix, borderColor));
                    break;

                case Corner.BOTTOM_LEFT:
                    suffix = "";
                    singleCorner.addAll(getBottomLeftCorner(coord, cornersToProcess.get(corner), suffix, borderColor));
                    break;

                case Corner.BOTTOM_RIGHT:
                    suffix = "\n";
                    singleCorner.addAll(getBottomRightCorner(coord, cornersToProcess.get(corner), suffix, borderColor));
                    break;

                default:
                    break;

            }
            cornersAsString.put(corner, singleCorner);
        }
    }

    // only considers cases of goldcards with 1 or 2 different symbols as placement requirement
    private void processCenter(Map<Integer, String> centerAsString, Set<Symbol> centerToProcess, GoldCard card) {

        centerAsString.put(0, "────────");
        centerAsString.put(3, "        ");
        centerAsString.put(5, "────────");

        String colorReset = getRightColor(card.getReign());

        if (centerToProcess.isEmpty()) {
            centerAsString.put(2, "        ");
            centerAsString.put(1, "   " + getRightColor(card.getMultiplier()) + String.valueOf(card.getPoints())
                    + getRightIcon(card.getMultiplier()) + colorReset + "   ");

            String space = (card.getRequirement().getReqs().size() == 1) ? "   " : "  ";
            centerAsString.put(4, space + getQuantityString(card.getRequirement().getReqs(), colorReset) + space);

            return;
        }

        centerAsString.put(1, "        ");
        centerAsString.put(2, "   " + getRightColor(card.getReign()) + "1" + getRightIcon(card.getReign()) + "   "); // back
        centerAsString.put(4, "        ");
    }

    private void processCenter(Map<Integer, String> centerAsString, Set<Symbol> centerToProcess, ResourceCard card) {
        centerAsString.put(0, "────────");
        centerAsString.put(3, "        ");
        centerAsString.put(4, "        ");
        centerAsString.put(5, "────────");

        if (centerToProcess.isEmpty()) {
            if (card.getPoints() == 0) {
                centerAsString.put(1, "        ");
            } else {
                centerAsString.put(1, "   " + String.valueOf(card.getPoints()) + "    ");
            }

            centerAsString.put(2, "        ");
            return;
        }

        centerAsString.put(1, "        ");
        centerAsString.put(2, "   " + getRightColor(card.getReign()) + "1" + getRightIcon(card.getReign()) + "   "); // back
    }

    private void processCenter(Map<Integer, String> centerAsString, Set<Symbol> centerToProcess, InitialCard card) {
        String colorReset = "\033[0m";

        centerAsString.put(0, colorReset + "────────");
        centerAsString.put(1, colorReset + "        ");
        centerAsString.put(4, colorReset + "        ");
        centerAsString.put(5, colorReset + "────────");

        if (centerToProcess.isEmpty()) {
            centerAsString.put(2, colorReset + "   ╔╗   ");
            centerAsString.put(3, colorReset + "   ╚╝   ");
            return;
        }

        Symbol[] symbols = centerToProcess.toArray(new Symbol[0]);

        centerAsString.put(2, "   " + getRightColor(symbols[0]) + "1" + getRightIcon(symbols[0]) + colorReset + "   ");
        switch (centerToProcess.size()) {
            case 1:
                centerAsString.put(3, "        ");
                break;
            case 2:
                centerAsString.put(3, "   " + getRightColor(symbols[1]) + "1" + getRightIcon(symbols[1]) + colorReset + "   ");
                break;
            case 3:
                centerAsString.put(3, "  " + getRightColor(symbols[1]) + "1" + getRightIcon(symbols[1]) + getRightColor(symbols[2]) + "1"
                        + getRightIcon(symbols[2]) + colorReset + "  ");
                break;
            default:
                break;
        }
    }

    private void processObjectiveCorners(Map<Corner, List<String>> cornersAsString, Pair<Integer, Integer> coord) {
        String leftSuffix = "", rightSuffix = "\n";
        Symbol symbol = Symbol.EMPTY_CORNER;

        cornersAsString.put(Corner.TOP_LEFT, getTopLeftCorner(coord, symbol, leftSuffix, "\033[0m"));
        cornersAsString.put(Corner.TOP_RIGHT, getTopRightCorner(coord, symbol, rightSuffix, "\033[0m"));
        cornersAsString.put(Corner.BOTTOM_LEFT, getBottomLeftCorner(coord, symbol, leftSuffix, "\033[0m"));
        cornersAsString.put(Corner.BOTTOM_RIGHT, getBottomRightCorner(coord, symbol, rightSuffix, "\033[0m"));
    }


    private void processObjectiveCenter(Map<Integer, String> centerAsString, Objective objective) {

        centerAsString.put(0, "────────");
        centerAsString.put(1, "   " + String.valueOf(objective.getPoints()) + "    ");
        centerAsString.put(5, "────────");

        switch (objective.getReq()) {
            case PositionRequirement positionRequirement:
                positioningClassifier(centerAsString, positionRequirement.getReqs());
                break;
            case QuantityRequirement quantityRequirement:
                centerAsString.put(2, "        ");
                centerAsString.put(4, "        ");
                String space = getObjectiveSpace(quantityRequirement);
                if (space != null) {
                    centerAsString.put(3, space + getQuantityString(quantityRequirement.getReqs(), "\033[0m") + space);
                    break;
                }
            default:
                throw new IllegalStateException("Unexpected value: " + objective.getReq());
        }


    }

    // GETTERS

    private String getQuantityString(Map<Symbol, Integer> reqs, String borderColor) {

        StringBuilder reqString = new StringBuilder();

        for (Symbol symbol : reqs.keySet())
            reqString.append(getRightColor(symbol)).append(String.valueOf(reqs.get(symbol))).append(getRightIcon(symbol))
                    .append(borderColor);

        return reqString.toString();

    }

    private String getPrefixCoord(Pair<Integer, Integer> coord, int linePosition) {
        int x = coord.first(), y = coord.second();

        return switch (linePosition) {
            case 1 -> "\033[" + String.valueOf(y) + ";" + String.valueOf(x) + "H";
            case 2 -> "\033[" + String.valueOf(y + 1) + ";" + String.valueOf(x) + "H";
            case 3 -> "\033[" + String.valueOf(y + 2) + ";" + String.valueOf(x) + "H";
            case 4 -> "\033[" + String.valueOf(y + 3) + ";" + String.valueOf(x) + "H";
            case 5 -> "\033[" + String.valueOf(y + 4) + ";" + String.valueOf(x) + "H";
            case 6 -> "\033[" + String.valueOf(y + 5) + ";" + String.valueOf(x) + "H";
            default -> "";
        };

    }

    private String getColorAndIcon(Symbol symbol) {
        return getRightColor(symbol) + getRightIcon(symbol);
    }

    private String getRightColor(Symbol symbol) {
        return switch (symbol) {
            case FUNGUS -> "\033[31m";
            case ANIMAL -> "\033[36m";
            case PLANT -> "\033[32m";
            case INSECT -> "\033[35m";
            case INKWELL, PARCHMENT, FEATHER -> "\033[33m";
            default -> "";
        };
    }

    private String getRightIcon(Symbol symbol) {
        return switch (symbol) {
            case FULL_CORNER, EMPTY_CORNER -> " ";
            case FUNGUS, ANIMAL, PLANT, INSECT -> "■";
            case INKWELL -> "I";
            case PARCHMENT -> "P";
            case FEATHER -> "F";
            case CORNER_OBJ -> "◳";
            default -> " ";
        };
    }

    private List<String> getTopLeftCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix, String borderColor) {
        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add(getPrefixCoord(coord, 1) + "┌────" + suffix);
                corner.add(getPrefixCoord(coord, 2) + "│    " + suffix);
                corner.add(getPrefixCoord(coord, 3) + "│    " + suffix);
                break;

            default:
                corner.add(getPrefixCoord(coord, 1) + "┌───┬" + suffix);
                corner.add(getPrefixCoord(coord, 2) + "│ " + getColorAndIcon(symbol) + borderColor + " │" + suffix);
                corner.add(getPrefixCoord(coord, 3) + "├───┘" + suffix);
                break;
        }

        return corner;
    }

    private List<String> getTopRightCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix, String borderColor) {

        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add("────┐" + suffix);
                corner.add("    │" + suffix);
                corner.add("    │" + suffix);
                break;

            default:
                corner.add("┬───┐" + suffix);
                corner.add("│ " + getColorAndIcon(symbol) + borderColor + " │" + suffix);
                corner.add("└───┤" + suffix);
                break;
        }

        return corner;
    }

    private List<String> getBottomLeftCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix, String borderColor) {

        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add(getPrefixCoord(coord, 4) + "│    " + suffix);
                corner.add(getPrefixCoord(coord, 5) + "│    " + suffix);
                corner.add(getPrefixCoord(coord, 6) + "└────" + suffix);
                break;

            default:
                corner.add(getPrefixCoord(coord, 4) + "├───┐" + suffix);
                corner.add(getPrefixCoord(coord, 5) + "│ " + getColorAndIcon(symbol) + borderColor + " │" + suffix);
                corner.add(getPrefixCoord(coord, 6) + "└───┴" + suffix);
                break;
        }

        return corner;
    }

    private List<String> getBottomRightCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix, String borderColor) {

        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add("    │" + suffix);
                corner.add("    │" + suffix);
                corner.add("────┘" + suffix);
                break;

            default:
                corner.add("┌───┤" + suffix);
                corner.add("│ " + getColorAndIcon(symbol) + borderColor + " │" + suffix);
                corner.add("┴───┘" + suffix);
                break;
        }

        return corner;
    }

    private String getPosixIcon(Symbol symbol) {

        String icon = "▆▆", suffix = "\033[0m";

        return switch (symbol) {
            case FUNGUS -> "\033[31m" + icon + suffix;
            case ANIMAL -> "\033[36m" + icon + suffix;
            case PLANT -> "\033[32m" + icon + suffix;
            case INSECT -> "\033[35m" + icon + suffix;
            default -> icon + suffix;
        };


    }


    // UTILS
    private void positioningClassifier(Map<Integer, String> centerAsString, Map<Pair<Integer, Integer>, Symbol> reqs) {
        Pair<Integer, Integer> bottomMost = null;
        Pair<Integer, Integer> leftMost = null;


        for (Pair<Integer, Integer> coord : reqs.keySet()) {
            if (bottomMost == null || coord.second() < bottomMost.second()) {
                bottomMost = coord;
            }
            if (leftMost == null || coord.first() < leftMost.first()) {
                leftMost = coord;
            }
        }

        Integer col, row;
        for (Pair<Integer, Integer> coord : reqs.keySet()) {
            row = 2-(coord.second()-bottomMost.second()); // 2 (max) - (curr - offset): we go top to bottom
            col = coord.first()-leftMost.first();         // simply offset

            // String building (with horizontal spaces)
            String tmp = "";
            for (int i = 0; i < col; i++) {
                tmp += "  ";
            }
            tmp+=getPosixIcon(reqs.get(coord));
            for (int i = col; i < 3; i++) {
                tmp += "  ";
            }

            centerAsString.put(row+2, tmp); // +2: start from second line of card
        }
    }


    private String getObjectiveSpace(QuantityRequirement reqs) {
        switch (reqs.getReqs().size()) {
            case 1:
                return "   ";
            case 2:
                return "  ";
            case 3:
                return " ";

            default:
                return null;
        }
    }



    public static void main(String[] args) throws IOException, CardException {
        TUICardPrinter printer = new TUICardPrinter();

        System.out.println("\033[2J");
        // ----------------
        // -- OBJECTIVES --
        // ----------------
        System.out.println(printer.getObjectiveString(1,  new Pair<Integer, Integer>(10, 10)));
        System.out.println(printer.getObjectiveString(2,  new Pair<Integer, Integer>(30, 10)));
        System.out.println(printer.getObjectiveString(3,  new Pair<Integer, Integer>(50, 10)));
        System.out.println(printer.getObjectiveString(4,  new Pair<Integer, Integer>(70, 10)));
        System.out.println(printer.getObjectiveString(5,  new Pair<Integer, Integer>(90, 10)));
        System.out.println(printer.getObjectiveString(6,  new Pair<Integer, Integer>(110, 10)));
        System.out.println(printer.getObjectiveString(7,  new Pair<Integer, Integer>(130, 10)));
        System.out.println(printer.getObjectiveString(8,  new Pair<Integer, Integer>(150, 10)));

        System.out.println(printer.getObjectiveString(9,  new Pair<Integer, Integer> (10, 20)));
        System.out.println(printer.getObjectiveString(10, new Pair<Integer, Integer>(30, 20)));
        System.out.println(printer.getObjectiveString(11, new Pair<Integer, Integer>(50, 20)));
        System.out.println(printer.getObjectiveString(12, new Pair<Integer, Integer>(70, 20)));
        System.out.println(printer.getObjectiveString(13, new Pair<Integer, Integer>(90, 20)));
        System.out.println(printer.getObjectiveString(14, new Pair<Integer, Integer>(110, 20)));
        System.out.println(printer.getObjectiveString(15, new Pair<Integer, Integer>(130, 20)));
        System.out.println(printer.getObjectiveString(16, new Pair<Integer, Integer>(150, 20)));


        // // --------------
        // // -- INITIALS --
        // // --------------
        // System.out.println(printer.getInitialString(1, new Pair<Integer, Integer>(10, 10), true));
        // System.out.println(printer.getInitialString(2, new Pair<Integer, Integer>(30, 10), true));
        // System.out.println(printer.getInitialString(3, new Pair<Integer, Integer>(50, 10), true));
        // System.out.println(printer.getInitialString(4, new Pair<Integer, Integer>(70, 10), true));
        // System.out.println(printer.getInitialString(5, new Pair<Integer, Integer>(90, 10), true));
        // System.out.println(printer.getInitialString(6, new Pair<Integer, Integer>(110, 10), true));

        // System.out.println(printer.getInitialString(1, new Pair<Integer, Integer>(10, 20), false));
        // System.out.println(printer.getInitialString(2, new Pair<Integer, Integer>(30, 20), false));
        // System.out.println(printer.getInitialString(3, new Pair<Integer, Integer>(50, 20), false));
        // System.out.println(printer.getInitialString(4, new Pair<Integer, Integer>(70, 20), false));
        // System.out.println(printer.getInitialString(5, new Pair<Integer, Integer>(90, 20), false));
        // System.out.println(printer.getInitialString(6, new Pair<Integer, Integer>(110, 20), false));

        // // --------------
        // // -- PLAYABLE --
        // // --------------
        // System.out.println(printer.getCardString( 1, new Pair<Integer, Integer>(10,  10), true));
        // System.out.println(printer.getCardString( 2, new Pair<Integer, Integer>(30,  10), true));
        // System.out.println(printer.getCardString( 3, new Pair<Integer, Integer>(50,  10), true));
        // System.out.println(printer.getCardString( 4, new Pair<Integer, Integer>(70,  10), true));
        // System.out.println(printer.getCardString( 5, new Pair<Integer, Integer>(90,  10), true));
        // System.out.println(printer.getCardString( 6, new Pair<Integer, Integer>(110, 10), true));
        // System.out.println(printer.getCardString( 7, new Pair<Integer, Integer>(130, 10), true));
        // System.out.println(printer.getCardString( 8, new Pair<Integer, Integer>(150, 10), true));
        // 
        // System.out.println(printer.getCardString( 9, new Pair<Integer, Integer>(10,  20), true));
        // System.out.println(printer.getCardString(10, new Pair<Integer, Integer>(30,  20), true));
        // System.out.println(printer.getCardString(11, new Pair<Integer, Integer>(50,  20), true));
        // System.out.println(printer.getCardString(12, new Pair<Integer, Integer>(70,  20), true));
        // System.out.println(printer.getCardString(13, new Pair<Integer, Integer>(90,  20), true));
        // System.out.println(printer.getCardString(14, new Pair<Integer, Integer>(110, 20), true));
        // System.out.println(printer.getCardString(15, new Pair<Integer, Integer>(130, 20), true));
        // System.out.println(printer.getCardString(16, new Pair<Integer, Integer>(150, 20), true));

        // System.out.println(printer.getCardString(17, new Pair<Integer, Integer>(10,  30), true));
        // System.out.println(printer.getCardString(18, new Pair<Integer, Integer>(30,  30), true));
        // System.out.println(printer.getCardString(19, new Pair<Integer, Integer>(50,  30), true));
        // System.out.println(printer.getCardString(20, new Pair<Integer, Integer>(70,  30), true));
        // System.out.println(printer.getCardString(21, new Pair<Integer, Integer>(90,  30), true));
        // System.out.println(printer.getCardString(22, new Pair<Integer, Integer>(110, 30), true));
        // System.out.println(printer.getCardString(23, new Pair<Integer, Integer>(130, 30), true));
        // System.out.println(printer.getCardString(24, new Pair<Integer, Integer>(150, 30), true));

        // System.out.println(printer.getCardString(25, new Pair<Integer, Integer>(10,  40), true));
        // System.out.println(printer.getCardString(26, new Pair<Integer, Integer>(30,  40), true));
        // System.out.println(printer.getCardString(27, new Pair<Integer, Integer>(50,  40), true));
        // System.out.println(printer.getCardString(28, new Pair<Integer, Integer>(70,  40), true));
        // System.out.println(printer.getCardString(29, new Pair<Integer, Integer>(90,  40), true));
        // System.out.println(printer.getCardString(30, new Pair<Integer, Integer>(110, 40), true));
        // System.out.println(printer.getCardString(31, new Pair<Integer, Integer>(130, 40), true));
        // System.out.println(printer.getCardString(32, new Pair<Integer, Integer>(150, 40), true));

        // System.out.println(printer.getCardString(33, new Pair<Integer, Integer>(10,  50), true));
        // System.out.println(printer.getCardString(34, new Pair<Integer, Integer>(30,  50), true));
        // System.out.println(printer.getCardString(35, new Pair<Integer, Integer>(50,  50), true));
        // System.out.println(printer.getCardString(36, new Pair<Integer, Integer>(70,  50), true));
        // System.out.println(printer.getCardString(37, new Pair<Integer, Integer>(90,  50), true));
        // System.out.println(printer.getCardString(38, new Pair<Integer, Integer>(110, 50), true));
        // System.out.println(printer.getCardString(39, new Pair<Integer, Integer>(130, 50), true));
        // System.out.println(printer.getCardString(40, new Pair<Integer, Integer>(150, 50), true));

        // System.out.println(printer.getCardString(41, new Pair<Integer, Integer>(10,  10), true));
        // System.out.println(printer.getCardString(42, new Pair<Integer, Integer>(30,  10), true));
        // System.out.println(printer.getCardString(43, new Pair<Integer, Integer>(50,  10), true));
        // System.out.println(printer.getCardString(44, new Pair<Integer, Integer>(70,  10), true));
        // System.out.println(printer.getCardString(45, new Pair<Integer, Integer>(90,  10), true));
        // System.out.println(printer.getCardString(46, new Pair<Integer, Integer>(110, 10), true));
        // System.out.println(printer.getCardString(47, new Pair<Integer, Integer>(130, 10), true));
        // System.out.println(printer.getCardString(48, new Pair<Integer, Integer>(150, 10), true));
        // 
        // System.out.println(printer.getCardString(49, new Pair<Integer, Integer>(10,  20), true));
        // System.out.println(printer.getCardString(50, new Pair<Integer, Integer>(30,  20), true));
        // System.out.println(printer.getCardString(51, new Pair<Integer, Integer>(50,  20), true));
        // System.out.println(printer.getCardString(52, new Pair<Integer, Integer>(70,  20), true));
        // System.out.println(printer.getCardString(53, new Pair<Integer, Integer>(90,  20), true));
        // System.out.println(printer.getCardString(54, new Pair<Integer, Integer>(110, 20), true));
        // System.out.println(printer.getCardString(55, new Pair<Integer, Integer>(130, 20), true));
        // System.out.println(printer.getCardString(56, new Pair<Integer, Integer>(150, 20), true));

        // System.out.println(printer.getCardString(57, new Pair<Integer, Integer>(10,  30), true));
        // System.out.println(printer.getCardString(58, new Pair<Integer, Integer>(30,  30), true));
        // System.out.println(printer.getCardString(59, new Pair<Integer, Integer>(50,  30), true));
        // System.out.println(printer.getCardString(60, new Pair<Integer, Integer>(70,  30), true));
        // System.out.println(printer.getCardString(61, new Pair<Integer, Integer>(90,  30), true));
        // System.out.println(printer.getCardString(62, new Pair<Integer, Integer>(110, 30), true));
        // System.out.println(printer.getCardString(63, new Pair<Integer, Integer>(130, 30), true));
        // System.out.println(printer.getCardString(64, new Pair<Integer, Integer>(150, 30), true));

        // System.out.println(printer.getCardString(65, new Pair<Integer, Integer>(10,  40), true));
        // System.out.println(printer.getCardString(66, new Pair<Integer, Integer>(30,  40), true));
        // System.out.println(printer.getCardString(67, new Pair<Integer, Integer>(50,  40), true));
        // System.out.println(printer.getCardString(68, new Pair<Integer, Integer>(70,  40), true));
        // System.out.println(printer.getCardString(69, new Pair<Integer, Integer>(90,  40), true));
        // System.out.println(printer.getCardString(70, new Pair<Integer, Integer>(110, 40), true));
        // System.out.println(printer.getCardString(71, new Pair<Integer, Integer>(130, 40), true));
        // System.out.println(printer.getCardString(72, new Pair<Integer, Integer>(150, 40), true));

        // System.out.println(printer.getCardString(73, new Pair<Integer, Integer>(10,  50), true));
        // System.out.println(printer.getCardString(74, new Pair<Integer, Integer>(30,  50), true));
        // System.out.println(printer.getCardString(75, new Pair<Integer, Integer>(50,  50), true));
        // System.out.println(printer.getCardString(76, new Pair<Integer, Integer>(70,  50), true));
        // System.out.println(printer.getCardString(77, new Pair<Integer, Integer>(90,  50), true));
        // System.out.println(printer.getCardString(78, new Pair<Integer, Integer>(110, 50), true));
        // System.out.println(printer.getCardString(79, new Pair<Integer, Integer>(130, 50), true));
        // System.out.println(printer.getCardString(80, new Pair<Integer, Integer>(150, 50), true));

        // System.out.println(printer.getInitialString(1, new Pair<Integer, Integer>(10, 10), true));
        // System.out.println(printer.getInitialString(2, new Pair<Integer, Integer>(30, 10), true));
        // System.out.println(printer.getInitialString(3, new Pair<Integer, Integer>(50, 10), true));
        // System.out.println(printer.getInitialString(4, new Pair<Integer, Integer>(70, 10), true));
        // System.out.println(printer.getInitialString(5, new Pair<Integer, Integer>(90, 10), true));
        // System.out.println(printer.getInitialString(6, new Pair<Integer, Integer>(110, 10), true));

        // System.out.println(printer.getInitialString(1, new Pair<Integer, Integer>(10, 20), false));
        // System.out.println(printer.getInitialString(2, new Pair<Integer, Integer>(30, 20), false));
        // System.out.println(printer.getInitialString(3, new Pair<Integer, Integer>(50, 20), false));
        // System.out.println(printer.getInitialString(4, new Pair<Integer, Integer>(70, 20), false));
        // System.out.println(printer.getInitialString(5, new Pair<Integer, Integer>(90, 20), false));
        // System.out.println(printer.getInitialString(6, new Pair<Integer, Integer>(110, 20), false));


        // // --------------
        // // -- PLAYABLE --
        // // --------------
        // System.out.println(printer.getCardString( 1, new Pair<Integer, Integer>(10,  10), false));
        // System.out.println(printer.getCardString( 2, new Pair<Integer, Integer>(30,  10), false));
        // System.out.println(printer.getCardString( 3, new Pair<Integer, Integer>(50,  10), false));
        // System.out.println(printer.getCardString( 4, new Pair<Integer, Integer>(70,  10), false));
        // System.out.println(printer.getCardString( 5, new Pair<Integer, Integer>(90,  10), false));
        // System.out.println(printer.getCardString( 6, new Pair<Integer, Integer>(110, 10), false));
        // System.out.println(printer.getCardString( 7, new Pair<Integer, Integer>(130, 10), false));
        // System.out.println(printer.getCardString( 8, new Pair<Integer, Integer>(150, 10), false));

        // System.out.println(printer.getCardString( 9, new Pair<Integer, Integer>(10,  20), false));
        // System.out.println(printer.getCardString(10, new Pair<Integer, Integer>(30,  20), false));
        // System.out.println(printer.getCardString(11, new Pair<Integer, Integer>(50,  20), false));
        // System.out.println(printer.getCardString(12, new Pair<Integer, Integer>(70,  20), false));
        // System.out.println(printer.getCardString(13, new Pair<Integer, Integer>(90,  20), false));
        // System.out.println(printer.getCardString(14, new Pair<Integer, Integer>(110, 20), false));
        // System.out.println(printer.getCardString(15, new Pair<Integer, Integer>(130, 20), false));
        // System.out.println(printer.getCardString(16, new Pair<Integer, Integer>(150, 20), false));

        // System.out.println(printer.getCardString(17, new Pair<Integer, Integer>(10,  30), false));
        // System.out.println(printer.getCardString(18, new Pair<Integer, Integer>(30,  30), false));
        // System.out.println(printer.getCardString(19, new Pair<Integer, Integer>(50,  30), false));
        // System.out.println(printer.getCardString(20, new Pair<Integer, Integer>(70,  30), false));
        // System.out.println(printer.getCardString(21, new Pair<Integer, Integer>(90,  30), false));
        // System.out.println(printer.getCardString(22, new Pair<Integer, Integer>(110, 30), false));
        // System.out.println(printer.getCardString(23, new Pair<Integer, Integer>(130, 30), false));
        // System.out.println(printer.getCardString(24, new Pair<Integer, Integer>(150, 30), false));

        // System.out.println(printer.getCardString(25, new Pair<Integer, Integer>(10,  40), false));
        // System.out.println(printer.getCardString(26, new Pair<Integer, Integer>(30,  40), false));
        // System.out.println(printer.getCardString(27, new Pair<Integer, Integer>(50,  40), false));
        // System.out.println(printer.getCardString(28, new Pair<Integer, Integer>(70,  40), false));
        // System.out.println(printer.getCardString(29, new Pair<Integer, Integer>(90,  40), false));
        // System.out.println(printer.getCardString(30, new Pair<Integer, Integer>(110, 40), false));
        // System.out.println(printer.getCardString(31, new Pair<Integer, Integer>(130, 40), false));
        // System.out.println(printer.getCardString(32, new Pair<Integer, Integer>(150, 40), false));

        // System.out.println(printer.getCardString(33, new Pair<Integer, Integer>(10,  50), false));
        // System.out.println(printer.getCardString(34, new Pair<Integer, Integer>(30,  50), false));
        // System.out.println(printer.getCardString(35, new Pair<Integer, Integer>(50,  50), false));
        // System.out.println(printer.getCardString(36, new Pair<Integer, Integer>(70,  50), false));
        // System.out.println(printer.getCardString(37, new Pair<Integer, Integer>(90,  50), false));
        // System.out.println(printer.getCardString(38, new Pair<Integer, Integer>(110, 50), false));
        // System.out.println(printer.getCardString(39, new Pair<Integer, Integer>(130, 50), false));
        // System.out.println(printer.getCardString(40, new Pair<Integer, Integer>(150, 50), false));

        // System.out.println(printer.getCardString(41, new Pair<Integer, Integer>(10,  10), false));
        // System.out.println(printer.getCardString(42, new Pair<Integer, Integer>(30,  10), false));
        // System.out.println(printer.getCardString(43, new Pair<Integer, Integer>(50,  10), false));
        // System.out.println(printer.getCardString(44, new Pair<Integer, Integer>(70,  10), false));
        // System.out.println(printer.getCardString(45, new Pair<Integer, Integer>(90,  10), false));
        // System.out.println(printer.getCardString(46, new Pair<Integer, Integer>(110, 10), false));
        // System.out.println(printer.getCardString(47, new Pair<Integer, Integer>(130, 10), false));
        // System.out.println(printer.getCardString(48, new Pair<Integer, Integer>(150, 10), false));

        // System.out.println(printer.getCardString(49, new Pair<Integer, Integer>(10,  20), false));
        // System.out.println(printer.getCardString(50, new Pair<Integer, Integer>(30,  20), false));
        // System.out.println(printer.getCardString(51, new Pair<Integer, Integer>(50,  20), false));
        // System.out.println(printer.getCardString(52, new Pair<Integer, Integer>(70,  20), false));
        // System.out.println(printer.getCardString(53, new Pair<Integer, Integer>(90,  20), false));
        // System.out.println(printer.getCardString(54, new Pair<Integer, Integer>(110, 20), false));
        // System.out.println(printer.getCardString(55, new Pair<Integer, Integer>(130, 20), false));
        // System.out.println(printer.getCardString(56, new Pair<Integer, Integer>(150, 20), false));

        // System.out.println(printer.getCardString(57, new Pair<Integer, Integer>(10,  30), false));
        // System.out.println(printer.getCardString(58, new Pair<Integer, Integer>(30,  30), false));
        // System.out.println(printer.getCardString(59, new Pair<Integer, Integer>(50,  30), false));
        // System.out.println(printer.getCardString(60, new Pair<Integer, Integer>(70,  30), false));
        // System.out.println(printer.getCardString(61, new Pair<Integer, Integer>(90,  30), false));
        // System.out.println(printer.getCardString(62, new Pair<Integer, Integer>(110, 30), false));
        // System.out.println(printer.getCardString(63, new Pair<Integer, Integer>(130, 30), false));
        // System.out.println(printer.getCardString(64, new Pair<Integer, Integer>(150, 30), false));

        // System.out.println(printer.getCardString(65, new Pair<Integer, Integer>(10,  40), false));
        // System.out.println(printer.getCardString(66, new Pair<Integer, Integer>(30,  40), false));
        // System.out.println(printer.getCardString(67, new Pair<Integer, Integer>(50,  40), false));
        // System.out.println(printer.getCardString(68, new Pair<Integer, Integer>(70,  40), false));
        // System.out.println(printer.getCardString(69, new Pair<Integer, Integer>(90,  40), false));
        // System.out.println(printer.getCardString(70, new Pair<Integer, Integer>(110, 40), false));
        // System.out.println(printer.getCardString(71, new Pair<Integer, Integer>(130, 40), false));
        // System.out.println(printer.getCardString(72, new Pair<Integer, Integer>(150, 40), false));

        // System.out.println(printer.getCardString(73, new Pair<Integer, Integer>(10,  50), false));
        // System.out.println(printer.getCardString(74, new Pair<Integer, Integer>(30,  50), false));
        // System.out.println(printer.getCardString(75, new Pair<Integer, Integer>(50,  50), false));
        // System.out.println(printer.getCardString(76, new Pair<Integer, Integer>(70,  50), false));
        // System.out.println(printer.getCardString(77, new Pair<Integer, Integer>(90,  50), false));
        // System.out.println(printer.getCardString(78, new Pair<Integer, Integer>(110, 50), false));
        // System.out.println(printer.getCardString(79, new Pair<Integer, Integer>(130, 50), false));
        // System.out.println(printer.getCardString(80, new Pair<Integer, Integer>(150, 50), false));
    }
}
