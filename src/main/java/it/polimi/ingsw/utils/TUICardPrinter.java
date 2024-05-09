package it.polimi.ingsw.utils;

import it.polimi.ingsw.gamemodel.*;

import java.io.IOException;
import com.google.gson.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


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
     * @param filePath path of the json file to process
     * @param isFacingUp is the card facing up? If not, it's facing down
     * @param startingCoord x and y coordinates (determined from the terminal size and the board status) in which the card will be rendered
     * @return the string containing the series characters representing the side on which the card was played.
     * @throws IOException if needed
     */
    public String getPrintableCardAsStringWithCoord(int cardID, String filePath, Boolean isFacingUp, Pair<Integer, Integer> startingCoord) throws IOException {

        StringBuffer printableCard = new StringBuffer("\"\"\"\n"); // stringa per la stampa

        try {
            if(filePath.contains("initial")) {
                Map<String, List<Symbol>> jsonMap = initialJsonExtractor(filePath, cardID, isFacingUp);
                processInitialCard(jsonMap, printableCard, isFacingUp, startingCoord);
                printableCard.append("\"\"\"");
            }
            else if (filePath.contains("gold")) {
                goldJsonExtractor(filePath, cardID, isFacingUp);
                // TBA
            }
            else if (filePath.contains("resource")) {
                resourceJsonExtractor(filePath, cardID, isFacingUp);
                // TBA
            }
            else if (filePath.contains("objective")) {
                objectiveJsonExtractor(filePath, cardID, isFacingUp);
                // TBA
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return printableCard.toString();
        
    }

    private void processInitialCard(Map<String, List<Symbol>> jsonMap, StringBuffer printableCard, Boolean isFacingUp, Pair<Integer, Integer> startingCoord) {
        // TBA
    }

    private void processObjectiveCard(Map<String, List<Symbol>> jsonMap, StringBuffer printableCard) {
        // TBA
    }

    private void processGoldCard(Map<String, List<Symbol>> jsonMap, StringBuffer printableCard) {
        // TBA
    }

    private void processResourceCard(Map<String, List<Symbol>> jsonMap, StringBuffer printableCard) {
        // TBA
    }

    /**
     * The method converts the corners' information to the corresponding sequence of characters.
     * @param jsonMap is the map with the information to process (see the jsonExtractor methods for more details)
     * @param coord starting coordinates for the card
     * @return a map where each element (corner) is mapped to its corresponding 3 lines of characters
     */
    private Map<String, List<String>> processCorners(Map<String, List<Symbol>> jsonMap, Pair<Integer, Integer> coord) {

        //NB. L'assegnazione complessiva del colore della carta va fatta nella funzione di processamento generale

        int rows = coord.first(),
            cols = coord.second();

        Map<String, Symbol> cornersToProcess = new HashMap<>();
        cornersToProcess.put("topLeft", jsonMap.get("topLeft").getFirst());
        cornersToProcess.put("topRight", jsonMap.get("topRight").getFirst());
        cornersToProcess.put("bottomLeft", jsonMap.get("bottomLeft").getFirst());
        cornersToProcess.put("bottomRight", jsonMap.get("bottomRight").getFirst());

        Map<String, List<String>> cornersAsStrings = new HashMap<>();
        List<String> singleCorner;
        String prefix, suffix = "\n";
        Symbol symbol;

        for (String corner : cornersToProcess.keySet()) {

            prefix = "\033[" + String.valueOf(rows) + ";" + String.valueOf(cols) + "H";
            symbol = cornersToProcess.get(corner);
            singleCorner = new ArrayList<>();

            switch (corner) {

                case "topLeft":
                    if (symbol.equals(Symbol.EMPTY_CORNER)) {
                        singleCorner.add(prefix + "┌────");
                        prefix = "\033[" + String.valueOf(rows+1) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│    ");
                        prefix = "\033[" + String.valueOf(rows+2) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│    ");

                    } else {
                        singleCorner.add(prefix + "┌───┬");
                        prefix = "\033[" + String.valueOf(rows+1) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│ " + getRightIcon(symbol) + " │");
                        prefix = "\033[" + String.valueOf(rows+2) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "├───┘");
                    }
                    break;

                case "topRight":
                    if (symbol.equals(Symbol.EMPTY_CORNER)){
                        singleCorner.add(prefix + "────┐" + suffix);
                        prefix = "\033[" + String.valueOf(rows+1) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "    │" + suffix);
                        prefix = "\033[" + String.valueOf(rows+2) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "    │" + suffix);

                    } else {
                        singleCorner.add(prefix + "┬───┐" + suffix);
                        prefix = "\033[" + String.valueOf(rows+1) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│ " + getRightIcon(symbol) + " │" + suffix);
                        prefix = "\033[" + String.valueOf(rows+2) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "└───┤" + suffix);
                    }
                    break;

                case "bottomLeft":
                    if (symbol.equals(Symbol.EMPTY_CORNER)){
                        prefix = "\033[" + String.valueOf(rows+3) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│    " );
                        prefix = "\033[" + String.valueOf(rows+4) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│    ");
                        prefix = "\033[" + String.valueOf(rows+5) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "└────");

                    } else {
                        prefix = "\033[" + String.valueOf(rows+3) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "├───┐");
                        prefix = "\033[" + String.valueOf(rows+4) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│ " + getRightIcon(symbol) + " │");
                        prefix = "\033[" + String.valueOf(rows+5) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "└───┴");
                    }
                    break;

                case "bottomRight":
                    if (symbol.equals(Symbol.EMPTY_CORNER)){
                        prefix = "\033[" + String.valueOf(rows+3) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "    │" + suffix);
                        prefix = "\033[" + String.valueOf(rows+4) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "    │" + suffix);
                        prefix = "\033[" + String.valueOf(rows+5) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "────┘" + suffix);

                    } else {
                        prefix = "\033[" + String.valueOf(rows+3) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "┌───┤" + suffix);
                        prefix = "\033[" + String.valueOf(rows+4) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "│ " + getRightIcon(symbol) + " │" + suffix);
                        prefix = "\033[" + String.valueOf(rows+5) + ";" + String.valueOf(cols) + "H";
                        singleCorner.add(prefix + "┴───┘" + suffix);
                    }
                    break;

                default:
                    break;
            }

            cornersAsStrings.put(corner, singleCorner);
        }

        return cornersAsStrings;
    }

    /**
     * The method is used to obtain the right "icon" and color to use during the construction of a card. See {@link TUICardPrinter#processCorners(Map, Pair)} for reference.
     * @param symbol symbol of the specific corner
     * @return the correct sequence of character, representing the coloured symbol
     */
    private String getRightIcon(Symbol symbol) {
        switch (symbol) {
            case FULL_CORNER:
                return " ";
            case EMPTY_CORNER:
                return " ";
            case FUNGUS:
                return "\033[31■";
            case ANIMAL:
                return "\033[36■";
            case PLANT:
                return "\033[32■";
            case INSECT:
                return "\033[35■";
            case INKWELL:
                return "\033[33I";
            case PARCHMENT:
                return "\033[33P";
            case FEATHER:
                return "\033[33F";
            default:
                return " ";
        }
    }


    private void processCenteredSpace(Map<String, List<Symbol>> jsonMap) {
        // TBA
    }


    /**
     * It processes (from the JsonObject) the corners and the center of the given side of the card.
     * @param sideObject the card's side to convert
     * @param elements the map containing the association JsonKey-Symbols
     */
    private void extractJsonCornersAndCenter(JsonObject sideObject, Map<String, List<Symbol>> elements) {

        for (String jsonKey : sideObject.keySet()) {

            if(jsonKey.equals("center")){
                JsonArray symbols = sideObject.getAsJsonArray("center");
                List<Symbol> centerSymbols = new ArrayList<>();
                for (JsonElement symbol : symbols) {
                    centerSymbols.add(Symbol.valueOf(symbol.getAsString().toUpperCase()));
                }
                elements.put("center", centerSymbols);

            }else{
                // lista di simboli per ogni chiave del json a cui aggiungo tutti i vari simboli (più per il centro, uno per angolo)
                List<Symbol> symbols = new ArrayList<>();

                symbols.add(Symbol.valueOf(sideObject.get(jsonKey).getAsString().toUpperCase()));
                elements.put(jsonKey, symbols);
            }

        }
    }

    /**
     * The method looks in the Json file of initial cards for the wanted side of the card, and processes it.
     * @param filePath path of the json file (of initial cards) to process
     * @param id ID of the initial card to process
     * @param isFrontWanted is it requiring the face of the card? If not, then it is the back
     * @return the map containing the association JsonKey-Symbols
     * @throws IOException if needed
     */
    private Map<String, List<Symbol>> initialJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(content).getAsJsonObject();

        // final result
        Map<String, List<Symbol>> elements = new HashMap<>();

        // itera tra tutti gli elementi del json finché non trova l'id giusto
        for (String jasonKey : jsonObject.keySet()) {
            JsonObject object = jsonObject.getAsJsonObject(jasonKey);

            // se trova la carta con l'id giusto
            if (object.get("id").getAsInt() == id) {
                // estrae il lato giusto della carta
                JsonObject sideObject = isFrontWanted ? object.getAsJsonObject("front") : object.getAsJsonObject("back");

                extractJsonCornersAndCenter(sideObject, elements);
            }
            break;
        }
        return elements;
    }

    private void objectiveJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        // TBA
    }
    private void goldJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        // TBA
    }
    private void resourceJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        // TBA
    }
}
