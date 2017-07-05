package systems.singularity.chatfx.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by phts on 05/07/17.
 */
public class ReceiveFileController implements Initializable {
    @FXML
    private Label speedLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label etaLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public Label getSpeedLabel() {
        return speedLabel;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public Label getProgressLabel() {
        return progressLabel;
    }

    public Label getEtaLabel() {
        return etaLabel;
    }
}
