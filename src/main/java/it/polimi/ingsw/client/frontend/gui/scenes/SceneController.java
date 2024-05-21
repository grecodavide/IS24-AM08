package it.polimi.ingsw.client.frontend.gui.scenes;

import it.polimi.ingsw.client.frontend.gui.GraphicalViewGUI;
import it.polimi.ingsw.utils.GuiUtil;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneController {
    protected GraphicalViewGUI view;
    protected Stage stage;

    /**
     * Method to be called to do actions after the controller attributes are set
     * @throws IOException
     */
    public void initializePostController() throws IOException{
    }

    public void setGraphicalView(GraphicalViewGUI view) {
        this.view = view;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    protected <T>T loadScene(String path) throws IOException {
        FXMLLoader loader = GuiUtil.getLoader(path);
        T result = loader.load();
        setControllerAttributes(loader);
        return result;
    }

    protected void setControllerAttributes(FXMLLoader loader) throws IOException {
        SceneController controller = loader.getController();
        controller.setGraphicalView(view);
        controller.setStage(stage);
        controller.initializePostController();
    }
}
