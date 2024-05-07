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

        int rows = startingCoord.first(),
            cols = startingCoord.second();
        StringBuffer printableCard = new StringBuffer("\"\"\"\n");

        try {
            if(filePath.contains("initial")) {
                // recupero le informazioni della card
                Map<String, List<Symbol>> map = initialJsonExtractor(filePath, cardID, isFacingUp);
                // ...
            }
            else if (filePath.contains("gold")) {
                goldJsonExtractor(filePath, cardID, isFacingUp);
                // ...
            }
            else if (filePath.contains("resource")) {
                resourceJsonExtractor(filePath, cardID, isFacingUp);
                // ...
            }
            else if (filePath.contains("objective")) {
                objectiveJsonExtractor(filePath, cardID, isFacingUp);
                // ...
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return "TBA";
    }

    /**
     * It processes (from the JsonObject) the corners and the center of the given side of the card.
     * @param sideObject the card's side to convert
     * @param elements the map containing the association JsonKey-Symbols
     */
    private static void processCornersAndCenter(JsonObject sideObject, Map<String, List<Symbol>> elements) {

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


    private static void initialJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        // TBA
    }

    private static void objectiveJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        // TBA
    }
    private static void goldJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        // TBA
    }
    private static void resourceJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
        // TBA
    }
}
