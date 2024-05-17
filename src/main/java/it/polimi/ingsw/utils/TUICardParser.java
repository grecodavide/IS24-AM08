package it.polimi.ingsw.utils;

import java.util.*;
import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.exceptions.InvalidResourceException;
import it.polimi.ingsw.gamemodel.*;


public class TUICardParser {

    public String getGenericBack(Symbol reign, Pair<Integer, Integer> coord) {
        try {
            PlayableCard card = new ResourceCard(null, reign, 0);
            return parseCard(card, coord, null, false);
        } catch (InvalidResourceException | CardException e) {
            return "";
        }
    }




    // PARSERS
    
    /**
     * Generates a printable string representing a {@link Card} of any type
     * 
     * @param card the card to be parsed
     * @param coord where to place the card (relative to the terminal, not {@link Board})
     * @param displayCoord coordinates to display on the card (null if none should be displayed)
     * @param isFacingUp wheter the card is facing up or down
     * 
     * @returns 
     * 
     * @throws CardException 
     */
    public String parseCard(Card card, Pair<Integer, Integer> coord, Pair<Integer, Integer> displayCoord, Boolean isFacingUp)
            throws CardException {

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
            case GoldCard goldCard -> processCenter(centerAsString, displayCoord, centerToProcess, goldCard);
            case ResourceCard resourceCard -> processCenter(centerAsString, displayCoord, centerToProcess, resourceCard);
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

    public String parseObjective(Objective card, Pair<Integer, Integer> coord) {

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


    // NO JAVADOC
    // ASSEMBLERERS
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

    private String getPlayableCoord(Integer x, Integer y) {
        String coords = "(" + x.toString() + "," + y.toString() + ")";
        Integer preSpacesNumber = (8 - coords.length()) / 2; // center is 8 chars
        String preSpaces = " ".repeat(preSpacesNumber);
        String postSpaces = " ".repeat(8 - preSpacesNumber - coords.length());

        return preSpaces + coords + postSpaces;
    }

    // only considers cases of goldcards with 1 or 2 different symbols as placement requirement
    private void processCenter(Map<Integer, String> centerAsString, Pair<Integer, Integer> displayCoord, Set<Symbol> centerToProcess,
            GoldCard card) {
        centerAsString.put(0, "────────");
        if (displayCoord != null) {
            centerAsString.put(2, getPlayableCoord(displayCoord.first(), displayCoord.second()));
        } else {
            centerAsString.put(2, "        ");
        }
        centerAsString.put(5, "────────");

        String colorReset = getRightColor(card.getReign());

        if (centerToProcess.isEmpty()) {
            centerAsString.put(3, "        ");
            centerAsString.put(1, "   " + getRightColor(card.getMultiplier()) + String.valueOf(card.getPoints())
                    + getRightIcon(card.getMultiplier()) + colorReset + "  ");

            String prefixSpace, postfixSpace;
            switch (card.getRequirement().getReqs().size()) {
                case 1:
                    prefixSpace = "   ";
                    postfixSpace = "  ";
                    break;
                case 2:
                    prefixSpace = postfixSpace = " ";
                    break;
                case 3:
                    prefixSpace = postfixSpace = "";
                    break;
                default:
                    prefixSpace = postfixSpace = "";
                    break;
            };
            centerAsString.put(4, prefixSpace + getQuantityString(card.getRequirement().getReqs(), colorReset) + postfixSpace);

            return;
        }

        centerAsString.put(1, "        ");
        centerAsString.put(3, "   " + getRightColor(card.getReign()) + "1" + getRightIcon(card.getReign()) + "  "); // back
        centerAsString.put(4, "        ");
    }

    private void processCenter(Map<Integer, String> centerAsString, Pair<Integer, Integer> displayCoord, Set<Symbol> centerToProcess,
            ResourceCard card) {
        centerAsString.put(0, "────────");
        if (displayCoord != null) {
            centerAsString.put(2, getPlayableCoord(displayCoord.first(), displayCoord.second()));
        } else {
            centerAsString.put(2, "        ");
        }
        centerAsString.put(4, "        ");
        centerAsString.put(5, "────────");

        if (centerToProcess.isEmpty()) {
            if (card.getPoints() == 0) {
                centerAsString.put(1, "        ");
            } else {
                centerAsString.put(1, "   " + String.valueOf(card.getPoints()) + "    ");
            }

            centerAsString.put(3, "        ");
            return;
        }

        centerAsString.put(1, "        ");
        centerAsString.put(3, "   " + getRightColor(card.getReign()) + "1" + getRightIcon(card.getReign()) + "  "); // back
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

        centerAsString.put(2, "   " + getRightColor(symbols[0]) + "1" + getRightIcon(symbols[0]) + colorReset + "  ");
        switch (centerToProcess.size()) {
            case 1:
                centerAsString.put(3, "        ");
                break;
            case 2:
                centerAsString.put(3, "   " + getRightColor(symbols[1]) + "1" + getRightIcon(symbols[1]) + colorReset + "  ");
                break;
            case 3:
                centerAsString.put(3, " " + getRightColor(symbols[1]) + "1" + getRightIcon(symbols[1]) + getRightColor(symbols[2]) + "1"
                        + getRightIcon(symbols[2]) + colorReset + " ");
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
                Map<Symbol, Integer> req = quantityRequirement.getReqs();
                int size = req.size();
                centerAsString.put(2, "        ");
                centerAsString.put(4, "        ");
                int reqSize = req.size();
                if (reqSize < 3) {
                    String space = getObjectiveSpace(size);
                    if (space != null) {
                        centerAsString.put(3, space + " " + getQuantityString(req, "\033[0m") + space);
                        break;
                    }
                } else {
                    int firstHalf = size / 2;
                    String space = getObjectiveSpace(firstHalf);
                    Map<Symbol, Integer> first = new HashMap<>(), second = new HashMap<>();
                    int cycle = 0;
                    for (Symbol s : req.keySet()) {
                        if (cycle < firstHalf)
                            first.put(s, req.get(s));
                        else
                            second.put(s, req.get(s));
                        cycle++;
                    }

                    centerAsString.put(3, space + " " + getQuantityString(first, "\033[0m") + space);
                    space = getObjectiveSpace(size - firstHalf);
                    centerAsString.put(4, space + getQuantityString(second, "\033[0m") + space);
                }
                break;
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

    /**
     * The method is a getter for the TUI-relative color of a specific symbol.
     * 
     * @param symbol the specified symbol
     * @return the color code as String
     */
    public String getRightColor(Symbol symbol) {
        return switch (symbol) {
            case FUNGUS -> "\033[31m";
            case ANIMAL -> "\033[34m";
            case PLANT -> "\033[32m";
            case INSECT -> "\033[35m";

            case INKWELL -> "\033[33m";
            case PARCHMENT -> "\033[33m";
            case FEATHER -> "\033[33m";
            default -> "";
        };
    }

    /**
     * The method is a getter for the TUI-relative icon of a specific symbol.
     * 
     * @param symbol the specified symbol
     * @return the symbol icon as String
     */
    public String getRightIcon(Symbol symbol) {
        return switch (symbol) {
            case FULL_CORNER, EMPTY_CORNER -> "  ";
            case FUNGUS -> "󰟟 ";
            case ANIMAL -> " ";
            case PLANT -> " ";
            case INSECT -> "󱖉 ";
            case INKWELL -> "󱄮 ";
            case PARCHMENT -> " ";
            case FEATHER -> " ";
            case CORNER_OBJ -> "󱨈 ";
            default -> "  ";
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
                corner.add(getPrefixCoord(coord, 2) + "│ " + getColorAndIcon(symbol) + borderColor + "│" + suffix);
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
                corner.add("│ " + getColorAndIcon(symbol) + borderColor + "│" + suffix);
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
                corner.add(getPrefixCoord(coord, 5) + "│ " + getColorAndIcon(symbol) + borderColor + "│" + suffix);
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
                corner.add("│ " + getColorAndIcon(symbol) + borderColor + "│" + suffix);
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
            row = 2 - (coord.second() - bottomMost.second()); // 2 (max) - (curr - offset): we go top to bottom
            col = coord.first() - leftMost.first(); // simply offset

            // String building (with horizontal spaces)
            String tmp = "";
            for (int i = 0; i < col; i++) {
                tmp += "  ";
            }
            tmp += getPosixIcon(reqs.get(coord));
            for (int i = col; i < 3; i++) {
                tmp += "  ";
            }

            centerAsString.put(row + 2, tmp); // +2: start from second line of card
        }
    }

    private String getObjectiveSpace(Integer size) {
        return switch (size) {
            case 1 -> "  ";
            case 2 -> " ";
            case 3 -> "";
            default -> null;
        };
    }

}
