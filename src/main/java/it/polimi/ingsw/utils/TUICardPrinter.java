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
                List<Symbol> symbolList;

                if(isFacingUp) {
                    // converto gli elementi in una stringa da stampare (la carta)
                    printableCard.append("\033[2J\n"); // clear tui
                    printableCard.append("\033[0m\n"); // color reset

                    printableCard.append("\033[").append(String.valueOf(rows)).append(";"); // rows coordinates
                    printableCard.append(String.valueOf(cols)).append("H");                 // columns coordinates
                    printableCard.append("┌───┬────────┬───┐");
                    printableCard.append("\n");
                    rows++;

                    printableCard.append("\033[").append(String.valueOf(rows)).append(";"); // rows coordinates
                    printableCard.append(String.valueOf(cols)).append("H");                 // columns coordinates
                    printableCard.append("│ ");
                    symbolList = map.get("topLeft");
                    for (Symbol symbol : symbolList) {
                        switch (symbol) {
                            case FUNGUS:
                                printableCard.append("\033[31m■\033[0m");
                                break;
                            case ANIMAL:
                                printableCard.append("\036[31m■\033[0m");
                                break;
                            case PLANT:
                                printableCard.append("\032[31m■\033[0m");
                                break;
                            case INSECT:
                                printableCard.append("\035[31m■\033[0m");
                                break;
                            default:
                                printableCard.append("■");
                                break;
                        }
                    }
                    printableCard.append(" │        │ ");
                    symbolList = map.get("topRight");
                    for (Symbol symbol : symbolList) {
                        switch (symbol) {
                            case FUNGUS:
                                printableCard.append("\033[31m■\033[0m");
                                break;
                            case ANIMAL:
                                printableCard.append("\036[31m■\033[0m");
                                break;
                            case PLANT:
                                printableCard.append("\032[31m■\033[0m");
                                break;
                            case INSECT:
                                printableCard.append("\035[31m■\033[0m");
                                break;
                            default:
                                printableCard.append("■");
                                break;
                        }
                    }
                    printableCard.append(" │");
                    printableCard.append("\n");
                    rows++;

                    printableCard.append("\033[").append(String.valueOf(rows)).append(";"); // rows coordinates
                    printableCard.append(String.valueOf(cols)).append("H");                 // columns coordinates
                    printableCard.append("├───┘   ╔╗   └───┤");
                    printableCard.append("\n");
                    rows++;

                    printableCard.append("\033[").append(String.valueOf(rows)).append(";"); // rows coordinates
                    printableCard.append(String.valueOf(cols)).append("H");                 // columns coordinates
                    printableCard.append("├───┐   ╚╝   ┌───┤");
                    printableCard.append("\n");
                    rows++;

                    printableCard.append("\033[").append(String.valueOf(rows)).append(";"); // rows coordinates
                    printableCard.append(String.valueOf(cols)).append("H");                 // columns coordinates
                    printableCard.append("│ ");
                    symbolList = map.get("bottomLeft");
                    for (Symbol symbol : symbolList) {
                        switch (symbol) {
                            case FUNGUS:
                                printableCard.append("\033[31m■\033[0m");
                                break;
                            case ANIMAL:
                                printableCard.append("\036[31m■\033[0m");
                                break;
                            case PLANT:
                                printableCard.append("\032[31m■\033[0m");
                                break;
                            case INSECT:
                                printableCard.append("\035[31m■\033[0m");
                                break;
                            default:
                                printableCard.append("■");
                                break;
                        }
                    }
                    printableCard.append(" │        │ ");
                    symbolList = map.get("bottomRight");
                    for (Symbol symbol : symbolList) {
                        switch (symbol) {
                            case FUNGUS:
                                printableCard.append("\033[31m■\033[0m");
                                break;
                            case ANIMAL:
                                printableCard.append("\036[31m■\033[0m");
                                break;
                            case PLANT:
                                printableCard.append("\032[31m■\033[0m");
                                break;
                            case INSECT:
                                printableCard.append("\035[31m■\033[0m");
                                break;
                            default:
                                printableCard.append("■");
                                break;
                        }
                    }
                    printableCard.append(" │");
                    printableCard.append("\n");
                    rows++;

                    printableCard.append("\033[").append(String.valueOf(rows)).append(";"); // rows coordinates
                    printableCard.append(String.valueOf(cols)).append("H");                 // columns coordinates
                    printableCard.append("└───┴────────┴───┘");
                    printableCard.append("\n");
                    printableCard.append("\"\"\"");

                } else {

                    // TBA back of initial card

                }


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

    /**
     * The method looks in the Json file of initial cards for the wanted side of the card, and processes it.
     * @param filePath path of the json file (of initial cards) to process
     * @param id ID of the initial card to process
     * @param isFrontWanted is it requiring the face of the card? If not, then it is the back
     * @return the map containing the association JsonKey-Symbols
     * @throws IOException if needed
     */
    private static Map<String, List<Symbol>> initialJsonExtractor(String filePath, int id, boolean isFrontWanted) throws IOException {
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

                processCornersAndCenter(sideObject, elements);
            }
            break;
        }
        return elements;
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
