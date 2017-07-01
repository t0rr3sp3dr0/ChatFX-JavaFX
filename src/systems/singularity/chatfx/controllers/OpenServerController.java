package systems.singularity.chatfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.Socket;
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
                //Abrir conexão com o Server

            } catch (Exception e1) {
                e1.printStackTrace();
            }

        });
    }
}
