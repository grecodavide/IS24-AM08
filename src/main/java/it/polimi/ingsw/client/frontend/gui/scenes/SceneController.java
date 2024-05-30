package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.GraphicalViewGUI;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class SceneController {
    protected GraphicalViewGUI view;
    protected Stage stage;

    /**
     * Method to be called to do actions after the controller attributes are set
     * @throws IOException
     */
    public void initializePostController() throws IOException{
    }

    public abstract void initialize() throws IOException;

    public void setGraphicalView(GraphicalViewGUI view) {
        this.view = view;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Load a node from a FXML file path, assign properties to the controller
     * and to the node
     * @param path path of the FXML file
     * @return the node on top of the FXML
     * @param <T> Type of the node
     * @throws IOException
     */
    protected <T extends Node>T loadScene(String path) throws IOException {
        FXMLLoader loader = GuiUtil.getLoader(path);
        T result = loader.load();
        setControllerAttributes(loader, result);
        return result;
    }

    /**
     * Set graphical view and the main stage properties to the scene controller
     * @param loader FXMLL Loader of the scene
     * @throws IOException in case of exception
     */
    protected void setControllerAttributes(FXMLLoader loader) throws IOException {
        SceneController controller = loader.getController();
        controller.setGraphicalView(view);
        controller.setStage(stage);
        controller.initializePostController();
    }
    /**
     * Set graphical view and the main stage properties to the scene controller
     * also sets the "Controller" property for the given node
     * @param loader FXMLL Loader of the scene
     * @throws IOException in case of exception
     */
    protected void setControllerAttributes(FXMLLoader loader, Node node) throws IOException {
        setControllerAttributes(loader);
        node.getProperties().put("Controller", loader.getController());
    }
}
