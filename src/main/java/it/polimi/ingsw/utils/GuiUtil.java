package it.polimi.ingsw.utils;

import it.polimi.ingsw.client.frontend.gui.GraphicalApplication;
import it.polimi.ingsw.exceptions.AlreadyUsedUsernameException;
import it.polimi.ingsw.exceptions.WrongChoiceException;
import it.polimi.ingsw.exceptions.WrongStateException;
import it.polimi.ingsw.exceptions.WrongTurnException;
import it.polimi.ingsw.gamemodel.*;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Locale;

public class GuiUtil {
    public static String playableCardsPath = "/images/playable_cards";
    public static String objectivesPath = "/images/objectives";
    public static String initialsPath = "/images/initial_cards";
    public static String pawnsPath = "/images/pawn_colors";

    /**
     * Get a node from the given FXML
     * @param path path of the fxml
     * @return the requested node
     * @param <T> type of the expected node
     * @throws IOException if there are errors reading the file
     */
    public static <T>T getFromFXML(String path) throws IOException {
        FXMLLoader loader = GuiUtil.getLoader(path);
        return loader.load();
    }

    /**
     * Check if the username/match name is valid
     * The name must be alphanumeric and between 1 and 32 characters
     * @param name string to check
     * @return if the name is valid
     */
    public static boolean isValidName(String name) {
        return name.matches("^[a-zA-Z0-9]{1,32}$");
    }

    /**
     * Get the loader from the specified path
     * @param path file path of fxml
     * @return loader
     */
    public static FXMLLoader getLoader(String path) {
        return new FXMLLoader(GraphicalApplication.class.getResource(path));
    }

    /**
     * Applies the specified CSS to a javafx scene parent
     * @param w The parent to apply the css to
     * @param path Path of the css file
     */
    public static void applyCSS(javafx.scene.Parent w, String path) {
        w.getStylesheets().addAll(GraphicalApplication.class.getResource(path).toExternalForm());
    }

    /**
     * Get the image path of a playable card
     * @param card card to get the image
     * @param side side of the card
     * @return the path as a string
     */
    public static String getImagePath(PlayableCard card, Side side) {
        if (side.equals(Side.FRONT)) {
            return playableCardsPath + "/" + card.getId() + ".png";
        } else {
            return switch (card){
                case GoldCard ignored -> getGoldsBack(card.getReign());
                case ResourceCard ignored -> getResourcesBack(card.getReign());
            };
        }
    }

    /**
     * Get the back of a resource card
     * @param symbol symbol of the resource card
     * @return the path as a string
     */
    public static String getResourcesBack(Symbol symbol) {
        String reign = symbol.toString().toUpperCase();
        return playableCardsPath + "/" + reign + "-resources-back.png";
    }

    /**
     * Get the back of a gold card
     * @param symbol symbol of the gold card
     * @return the path as a string
     */
    public static String getGoldsBack(Symbol symbol) {
        String reign = symbol.toString().toUpperCase();
        return playableCardsPath + "/" + reign + "-golds-back.png";
    }

    /**
     * Get the image path of an intial card
     * @param card card to get the image
     * @param side side of the card
     * @return the path as a string
     */
    public static String getImagePath(InitialCard card, Side side) {
        return initialsPath + "/" + side.toString() + "/" + card.getId() + ".png";
    }

    /**
     * Get the image path of an objective card
     * @param obj card to get the image
     * @param side side of the card
     * @return the path as a string
     */
    public static String getImagePath(Objective obj, Side side) {
        if (side.equals(Side.FRONT))
            return objectivesPath + "/" + obj.getID() + ".png";
        else
            return objectivesPath + "/objectives-back.png";
    }

    /**
     * Get image path of a pawn
     * @param color color of the pawn
     * @return the path as a string
     */
    public static String getPawnImagePath(Color color) {
        return pawnsPath + "/" + color.toString().toLowerCase(Locale.ROOT) + "-pawn.png";
    }

    /**
     * Get image path of the black pawn
     * @return the path as a string
     */
    public static String getBlackPawnImagePath() {
        return pawnsPath + "/black-pawn.png";
    }

    /**
     * Get the hex code of the given color
     * @param color color to convert
     * @return the hex code of color
     */
    public static String getHexFromColor(Color color) {
        return switch (color) {
            case RED -> "#C00402";
            case BLUE -> "#0C6692";
            case GREEN -> "#195C00";
            case YELLOW -> "#BEA013";
        };
    }

    /**
     * Translate exception type into human-readable titles
     * @param e exception to translate
     * @return human-readable title
     */
    public static String getExceptionTitle(Exception e) {
        return switch (e) {
            case WrongStateException ignored -> "Wrong turn!";
            case WrongChoiceException ignored -> "Wrong move!";
            case AlreadyUsedUsernameException ignored -> "Username already used!";
            case WrongTurnException ignored -> "It is not your turn!";
            case RemoteException ignored -> "Connection error";
            default -> "Error!";
        };
    }

}

