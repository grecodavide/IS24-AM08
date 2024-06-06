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

    public static <T>T getFromFXML(String path) throws IOException {
        FXMLLoader loader = GuiUtil.getLoader(path);
        return loader.load();
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

    public static String getResourcesBack(Symbol symbol) {
        String reign = symbol.toString().toUpperCase();
        return playableCardsPath + "/" + reign + "-resources-back.png";
    }

    public static String getGoldsBack(Symbol symbol) {
        String reign = symbol.toString().toUpperCase();
        return playableCardsPath + "/" + reign + "-golds-back.png";
    }

    public static String getImagePath(InitialCard card, Side side) {
        return initialsPath + "/" + side.toString() + "/" + card.getId() + ".png";
    }

    public static String getImagePath(Objective obj, Side side) {
        if (side.equals(Side.FRONT))
            return objectivesPath + "/" + obj.getID() + ".png";
        else
            return objectivesPath + "/objectives-back.png";
    }

    public static String getPawnImagePath(Color color) {
        return pawnsPath + "/" + color.toString().toLowerCase(Locale.ROOT) + "-pawn.png";
    }
    public static String getBlackPawnImagePath() {
        return pawnsPath + "/black-pawn.png";
    }

    public static String getHexFromColor(Color color) {
        return switch (color) {
            case RED -> "#C00402";
            case BLUE -> "#0C6692";
            case GREEN -> "#195C00";
            case YELLOW -> "#BEA013";
        };
    }

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

