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

    public String getCardString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp) {
        if (resourceCards.get(id) != null)
            return parseCard(resourceCards.get(id), coord, isFacingUp);
        else if (goldCards.get(id) != null)
            return parseCard(goldCards.get(id), coord, isFacingUp);
        return "Invalid card";
    }

    public String getInitialString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp){
        return parseCard(initialCards.get(id), coord, isFacingUp);
    }

    public String getObjectiveString(int id, Pair<Integer, Integer> coord, Boolean isFacingUp){
        return parseObjective(objectives.get(id), coord, isFacingUp);
    }

    private String parseCard(Card card, Pair<Integer, Integer> coord, Boolean isFacingUp){

        // acquire information
        Map<Corner, Symbol> cornersToProcess = new HashMap<>();
        acquireCornerSymbols(cornersToProcess, card, isFacingUp);

        Set<Symbol> centerToProcess = new HashSet<>();
        acquireCenterSymbols(centerToProcess, card, isFacingUp);

        // process information
        Map<Corner, List<String>> cornersAsString = new HashMap<>();
        processCorners(cornersAsString, cornersToProcess, coord);

        Map<Integer, List<String>> centerAsString = new HashMap<>();
        processCenter(centerAsString, centerToProcess);

        // assemble the card

        // dipende che tipo di carta è devo o meno pulire la board e settare il colore

        String tripleBrackets = "\"\"\"";

        return "TBA";
    }

    private String parseObjective(Objective card, Pair<Integer, Integer> coord, Boolean isFacingUp){
        return "TBA";
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

    private void processCenter(Map<Integer, List<String>> centerAsString, Set<Symbol> centerToProcess) {
        // TBA
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

    /**
     * The method is used to obtain the right "icon" and color to use during the construction of a card. See { TUICardPrinter#processCorners(Map, Pair)} for reference.
     * @param symbol symbol of the specific corner
     * @return the correct sequence of character, representing the coloured symbol
     */
    private String getRightIcon(Symbol symbol) {
        return switch (symbol) {
            case     FULL_CORNER     -> " ";
            case     EMPTY_CORNER    -> " ";
            case     FUNGUS          -> "\033[31■";
            case     ANIMAL          -> "\033[36■";
            case     PLANT           -> "\033[32■";
            case     INSECT          -> "\033[35■";
            case     INKWELL         -> "\033[33I";
            case     PARCHMENT       -> "\033[33P";
            case     FEATHER         -> "\033[33F";
            default  -> " ";
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
                corner.add(getPrefixCoord(coord, 2) + "│ " + getRightIcon(symbol) + " │" + suffix);
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
                corner.add(getPrefixCoord(coord, 2) + "│ " + getRightIcon(symbol) + " │" + suffix);
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
                corner.add(getPrefixCoord(coord, 5) + "│ " + getRightIcon(symbol) + " │" + suffix);
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
                corner.add(getPrefixCoord(coord, 5) + "│ " + getRightIcon(symbol) + " │" + suffix);
                corner.add(getPrefixCoord(coord, 6) + "┴───┘" + suffix);
                break;
        }

        return corner;
    }

}
