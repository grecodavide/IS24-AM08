package it.polimi.ingsw.utils;

import it.polimi.ingsw.exceptions.CardException;
import it.polimi.ingsw.gamemodel.*;

import java.util.*;


public class TUICardPrinter {
    Map<Integer, InitialCard> initialCards;
    Map<Integer, Objective> objectives;
    Map<Integer, GoldCard> goldCards;
    Map<Integer, ResourceCard> resourceCards;

    public TUICardPrinter(){
        CardsManager cardsManager = CardsManager.getInstance();

        initialCards = cardsManager.getInitialCards();
        objectives = cardsManager.getObjectives();
        goldCards = cardsManager.getGoldCards();
        resourceCards = cardsManager.getResourceCards();

    }

    // TBA JAVADOC

    public String getCardString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp) throws CardException {
        if (resourceCards.get(id) != null)
            return parseCard(resourceCards.get(id), coord, isFacingUp);
        else if (goldCards.get(id) != null)
            return parseCard(goldCards.get(id), coord, isFacingUp);
        throw new CardException("Invalid card type: " + initialCards.get(id).getClass() + "or" + objectives.get(id).getClass() + "!");
    }

    public String getInitialString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp) throws CardException {
        return parseCard(initialCards.get(id), coord, isFacingUp);
    }

    public String getObjectiveString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp){
        return parseObjective(objectives.get(id), coord, isFacingUp);
    }

    // NO JAVADOC

    private String parseCard(Card card, Pair<Integer, Integer> coord, Boolean isFacingUp) throws CardException {

        // acquire information
        Map<Corner, Symbol> cornersToProcess = new HashMap<>();
        acquireCornerSymbols(cornersToProcess, card, isFacingUp);

        Set<Symbol> centerToProcess = new HashSet<>();
        acquireCenterSymbols(centerToProcess, card, isFacingUp);

        String cardColor;
        switch (card) {
            case InitialCard initialCard        -> cardColor = "\033[0m";
            case GoldCard goldCard              -> cardColor = getRightColor(goldCard.getReign());
            case ResourceCard resourceCard      -> cardColor = getRightColor(resourceCard.getReign());
            default                             -> throw new CardException("Invalid card type: " + card.getClass() + "!");
        }

        // process information
        Map<Corner, List<String>> cornersAsString = new HashMap<>();
        processCorners(cornersAsString, cornersToProcess, coord);

        Map<Integer, String> centerAsString = new HashMap<>();
        switch (card) {
            case InitialCard initialCard        -> processCenter(centerAsString, centerToProcess, initialCard);
            case GoldCard goldCard              -> processCenter(centerAsString, centerToProcess, goldCard);
            case ResourceCard resourceCard      -> processCenter(centerAsString, centerToProcess, resourceCard);
            default                             -> throw new CardException("Invalid card type: " + card.getClass() + "!");
        }

        // assemble the string
        String tripleBrackets = "\"\"\"", newLine = "\n";
        StringBuilder printableCard = new StringBuilder(tripleBrackets + newLine).append(cardColor).append(newLine);
        assembleCard(printableCard, cornersAsString, centerAsString); // if it doesn't work, use the safe version assembleCardSafe
        printableCard.append(newLine);

        // card assembled
        return printableCard.toString();
    }

    private String parseObjective(Objective card, Pair<Integer, Integer> coord, Boolean isFacingUp){
        return "TBA";
    }

    // cool algorithmic version
    private void assembleCard(StringBuilder printableCard, Map<Corner, List<String>> cornersAsString, Map<Integer, String> centerAsString){

        List<Corner> left = new ArrayList<>(), right = new ArrayList<>();
        left.add(Corner.TOP_LEFT);
        left.add(Corner.BOTTOM_LEFT);
        right.add(Corner.TOP_RIGHT);
        right.add(Corner.BOTTOM_RIGHT);

        int i, j, k;
        for(i = 0; i < 6; i++){
            j = (i <= 2) ? 0 : 1;
            k = i % 3;

            printableCard.append(cornersAsString.get(left.get(j)).get(k));
            printableCard.append(centerAsString.get(i));
            printableCard.append(cornersAsString.get(right.get(j)).get(k));
        }

    }

    // boring safe version
    private void assembleCardSafe(StringBuilder printableCard, Map<Corner, List<String>> cornersAsString, Map<Integer, String> centerAsString){
        printableCard.append(cornersAsString.get(Corner.TOP_LEFT).get(0));
        printableCard.append(centerAsString.get(0));
        printableCard.append(cornersAsString.get(Corner.TOP_RIGHT).get(0));

        printableCard.append(cornersAsString.get(Corner.TOP_LEFT).get(1));
        printableCard.append(centerAsString.get(1));
        printableCard.append(cornersAsString.get(Corner.TOP_RIGHT).get(1));

        printableCard.append(cornersAsString.get(Corner.TOP_LEFT).get(2));
        printableCard.append(centerAsString.get(2));
        printableCard.append(cornersAsString.get(Corner.TOP_RIGHT).get(2));

        printableCard.append(cornersAsString.get(Corner.BOTTOM_LEFT).get(0));
        printableCard.append(centerAsString.get(3));
        printableCard.append(cornersAsString.get(Corner.BOTTOM_RIGHT).get(0));

        printableCard.append(cornersAsString.get(Corner.BOTTOM_LEFT).get(1));
        printableCard.append(centerAsString.get(4));
        printableCard.append(cornersAsString.get(Corner.BOTTOM_RIGHT).get(1));

        printableCard.append(cornersAsString.get(Corner.BOTTOM_LEFT).get(2));
        printableCard.append(centerAsString.get(5));
        printableCard.append(cornersAsString.get(Corner.BOTTOM_RIGHT).get(2));
    }

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

    private void acquireCenterSymbols(Set<Symbol> centerToProcess, Card card, Boolean isFacingUp){
        Side side = (isFacingUp) ? Side.FRONT : Side.BACK;
        centerToProcess.addAll(card.getSide(side).getCenter());
    }

    private void processCorners(Map<Corner, List<String>> cornersAsString, Map<Corner, Symbol> cornersToProcess, Pair<Integer, Integer> coord){

        List<String> singleCorner;
        String suffix;

        for (Corner corner : cornersToProcess.keySet()) {
            singleCorner = new ArrayList<>();

            switch (corner){
                case Corner.TOP_LEFT:
                    suffix = "";
                    singleCorner.addAll(getTopLeftCorner(coord, cornersToProcess.get(corner), suffix));
                    break;

                case Corner.TOP_RIGHT:
                    suffix = "\n";
                    singleCorner.addAll(getTopRightCorner(coord, cornersToProcess.get(corner), suffix));
                    break;

                case Corner.BOTTOM_LEFT:
                    suffix = "";
                    singleCorner.addAll(getBottomLeftCorner(coord, cornersToProcess.get(corner), suffix));
                    break;

                case Corner.BOTTOM_RIGHT:
                    suffix = "\n";
                    singleCorner.addAll(getBottomRightCorner(coord, cornersToProcess.get(corner), suffix));
                    break;

                default:
                    break;

            }
            cornersAsString.put(corner, singleCorner);
        }
    }

    private void processCenter(Map<Integer, String> centerAsString, Set<Symbol> centerToProcess, InitialCard card) {
        centerAsString.put(1, "────────");
        centerAsString.put(2, "        ");
        centerAsString.put(5, "        ");
        centerAsString.put(6, "────────");

        if (centerToProcess.isEmpty()) {
            centerAsString.put(3, "   ╔╗   ");
            centerAsString.put(4, "   ╚╝   ");
            return;
        }

        String colorReset = "\033[0m";
        Symbol[] symbols = centerToProcess.toArray(new Symbol[0]);

        centerAsString.put(3, "   " + getRightColor(symbols[0]) + "1" + getRightIcon(symbols[0]) + colorReset + "   ");
        switch (centerToProcess.size()){
            case 1:
                centerAsString.put(4, "        ");
                break;
            case 2:
                centerAsString.put(4, "   " + getRightColor(symbols[1]) + "1" + getRightIcon(symbols[1]) + colorReset + "   ");
                break;
            case 3:
                centerAsString.put(4, "  " + getRightColor(symbols[1]) + "1" + getRightIcon(symbols[1]) +
                                             getRightColor(symbols[2]) + "1" + getRightIcon(symbols[2]) + colorReset + "  ");
                break;
            default:
                break;
        }
    }

    private void processCenter(Map<Integer, String> centerAsString, Set<Symbol> centerToProcess, ResourceCard card) {
        centerAsString.put(1, "────────");
        centerAsString.put(4, "        ");
        centerAsString.put(5, "        ");
        centerAsString.put(6, "────────");

        if (centerToProcess.isEmpty()){
            if (card.getPoints() == 0) { centerAsString.put(2, "        "); }
            else { centerAsString.put(2, "   " + String.valueOf(card.getPoints()) + "    "); }

            centerAsString.put(3, "        ");
            return;
        }

        centerAsString.put(2, "        ");
        centerAsString.put(3, "   " + getRightColor(card.getReign()) + "1" + getRightIcon(card.getReign()) + "   "); // back
    }

    // only considers cases of goldcards with 1 or 2 different symbols as placement requirement
    private void processCenter(Map<Integer, String> centerAsString, Set<Symbol> centerToProcess, GoldCard card) {

        centerAsString.put(1, "────────");
        centerAsString.put(4, "        ");
        centerAsString.put(6, "────────");

        String colorReset = getRightColor(card.getReign());

        if (centerToProcess.isEmpty()){
            centerAsString.put(3, "        ");
            centerAsString.put(2, "   " + getRightColor(card.getMultiplier()) + String.valueOf(card.getPoints()) + getRightIcon(card.getMultiplier()) + colorReset + "   ");

            String space = (card.getRequirement().getReqs().size() == 1) ? "   " : "  ";
            centerAsString.put(5, space + getGoldReqString(card.getRequirement().getReqs()) + space);

            return;
        }

        centerAsString.put(2, "        ");
        centerAsString.put(3, "   " + getRightColor(card.getReign()) + "1" + getRightIcon(card.getReign()) + "   "); // back
        centerAsString.put(5, "        ");
    }

    private String getGoldReqString(Map<Symbol, Integer> reqs) {

        StringBuilder reqString = new StringBuilder();

        for (Symbol symbol : reqs.keySet())
            reqString.append(getRightColor(symbol)).append(String.valueOf(reqs.get(symbol))).append(getRightIcon(symbol));

        return reqString.toString();

    }

    private String getPrefixCoord(Pair<Integer, Integer> coord, int linePosition){
        int x = coord.first(),
                y = coord.second();

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
            case     FUNGUS                         -> "\033[31";
            case     ANIMAL                         -> "\033[36";
            case     PLANT                          -> "\033[32";
            case     INSECT                         -> "\033[35";
            case     INKWELL, PARCHMENT, FEATHER    -> "\033[33";
            default                                 -> "";
        };
    }

    private String getRightIcon(Symbol symbol) {
        return switch (symbol) {
            case     FULL_CORNER, EMPTY_CORNER      -> " ";
            case     FUNGUS, ANIMAL, PLANT, INSECT  -> "■";
            case     INKWELL                        -> "I";
            case     PARCHMENT                      -> "P";
            case     FEATHER                        -> "F";
            case     CORNER_OBJ                     -> "◳";
            default                                 -> " ";
        };
    }

    private List<String> getTopLeftCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix){

        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add(getPrefixCoord(coord, 1) + "┌────" + suffix);
                corner.add(getPrefixCoord(coord, 2) + "│    " + suffix);
                corner.add(getPrefixCoord(coord, 3) + "│    " + suffix);
                break;

            default:
                corner.add(getPrefixCoord(coord, 1) + "┌───┬" + suffix);
                corner.add(getPrefixCoord(coord, 2) + "│ " + getColorAndIcon(symbol) + " │" + suffix);
                corner.add(getPrefixCoord(coord, 3) + "├───┘" + suffix);
                break;
        }

        return corner;
    }

    private List<String> getTopRightCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix){

        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add(getPrefixCoord(coord, 1) + "────┐" + suffix);
                corner.add(getPrefixCoord(coord, 2) + "    │" + suffix);
                corner.add(getPrefixCoord(coord, 3) + "    │" + suffix);
                break;

            default:
                corner.add(getPrefixCoord(coord, 1) + "┬───┐" + suffix);
                corner.add(getPrefixCoord(coord, 2) + "│ " + getColorAndIcon(symbol) + " │" + suffix);
                corner.add(getPrefixCoord(coord, 3) + "└───┤" + suffix);
                break;
        }

        return corner;
    }

    private List<String> getBottomLeftCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix){

        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add(getPrefixCoord(coord, 4) + "│    " + suffix);
                corner.add(getPrefixCoord(coord, 5) + "│    " + suffix);
                corner.add(getPrefixCoord(coord, 6) + "└────" + suffix);
                break;

            default:
                corner.add(getPrefixCoord(coord, 4) + "├───┐" + suffix);
                corner.add(getPrefixCoord(coord, 5) + "│ " + getColorAndIcon(symbol) + " │" + suffix);
                corner.add(getPrefixCoord(coord, 6) + "└───┴" + suffix);
                break;
        }

        return corner;
    }

    private List<String> getBottomRightCorner(Pair<Integer, Integer> coord, Symbol symbol, String suffix){

        List<String> corner = new ArrayList<>();

        switch (symbol) {
            case Symbol.EMPTY_CORNER:
                corner.add(getPrefixCoord(coord, 4) + "    │" + suffix);
                corner.add(getPrefixCoord(coord, 5) + "    │" + suffix);
                corner.add(getPrefixCoord(coord, 6) + "────┘" + suffix);
                break;

            default:
                corner.add(getPrefixCoord(coord, 4) + "┌───┤" + suffix);
                corner.add(getPrefixCoord(coord, 5) + "│ " + getColorAndIcon(symbol) + " │" + suffix);
                corner.add(getPrefixCoord(coord, 6) + "┴───┘" + suffix);
                break;
        }

        return corner;
    }

}
