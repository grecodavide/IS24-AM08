package it.polimi.ingsw.client.frontend.gui.controllers;


import javafx.fxml.FXML;
import javafx.scene.text.Text;

import java.io.IOException;

public class ErrorSceneController extends SceneController {
    @FXML
    private Text errorText;

    @Override
    public void initialize() throws IOException {

    }

    public void setText(String text) {
        errorText.setText(text);
    }
}
