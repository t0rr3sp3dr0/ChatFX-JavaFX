package systems.singularity.chatfx.server.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import systems.singularity.chatfx.server.Handler;
import systems.singularity.chatfx.util.javafx.Utilities;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by caesa on 01/07/2017.
 */
public class OpenServerController implements Initializable {
    @FXML
    private Parent root;
    @FXML
    private Button btOpen;

    @FXML
    private TextField tfPort;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        btOpen.setOnAction(e -> {
            try {
                new Handler(Integer.parseInt(tfPort.getText())).start();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/server.fxml"));
                final Parent root = fxmlLoader.load();

                Stage stage = new Stage();
                stage.setTitle("Server");
                stage.setScene(new Scene(root, 652, 400));
                stage.show();

                Utilities.setOnCloseRequest(stage);

                ((Stage) OpenServerController.this.root.getScene().getWindow()).close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });
    }
}
