package systems.singularity.chatfx.server.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by caesa on 01/07/2017.
 */
public class OpenServerController implements Initializable{
    @FXML
    private Button bt_open;

    @FXML
    private TextField tf_ip;

    @FXML
    private TextField tf_port;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        bt_open.setOnAction( e -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/server.fxml"));
                final Parent root = fxmlLoader.load();

                Stage stage = new Stage();
                stage.setTitle("Server");
                stage.setScene(new Scene(root, 600, 400));
                stage.show();

            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });
    }
}
