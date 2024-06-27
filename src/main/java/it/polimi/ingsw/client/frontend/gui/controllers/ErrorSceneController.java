package it.polimi.ingsw.client.frontend.gui.controllers;


import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Controller of the error scene window
 */
public class ErrorSceneController extends SceneController {
    // Window dimensions
    public static double windowWidth = 500;
    public static double windowHeight = 200;
    public Text errorTitle;

    @FXML
    private Text errorText;

    @Override
    public void initialize() throws IOException {

    }

    /**
     * Set the text of the error that is shown in the bottom
     * @param text text of the error
     */
    public void setText(String text) {
        errorText.setText(text);
    }

    /**
     * Set the title of the error that is shown on the top
     * @param title text of the title
     */
    public void setTitle(String title) {
        errorTitle.setText(title);
    }

    /**
     * Show an error from an exception
     * @param e exception
     */
    public void setErrror(Exception e) {
        errorTitle.setText(GuiUtil.getExceptionTitle(e));
        errorText.setText(e.getMessage());
    }
}
