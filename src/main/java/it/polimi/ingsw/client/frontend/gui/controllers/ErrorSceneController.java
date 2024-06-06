package it.polimi.ingsw.client.frontend.gui.controllers;


import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.io.IOException;

public class ErrorSceneController extends SceneController {
    public static double windowWidth = 500;
    public static double windowHeight = 200;
    public Text errorTitle;

    @FXML
    private Text errorText;

    @Override
    public void initialize() throws IOException {

    }

    public void setText(String text) {
        errorText.setText(text);
    }

    public void setTitle(String title) {
        errorTitle.setText(title);
    }

    public void setErrror(Exception e) {
        errorTitle.setText(GuiUtil.getExceptionTitle(e));
        errorText.setText(e.getMessage());
    }
}
