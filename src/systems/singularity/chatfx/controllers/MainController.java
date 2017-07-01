package systems.singularity.chatfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by caesa on 01/07/2017.
 */
public class MainController implements Initializable {

    @FXML
    private Button bt_send;

    @FXML
    private ListView<?> lv_users;

    @FXML
    private Button bt_file;

    @FXML
    private TextArea ta_chat;

    @FXML
    private ProgressBar progress;

    @FXML
    private TabPane tp_chats;

    @FXML
    private ListView<?> lv_files;

    @FXML
    private TextField tf_message;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bt_send.setOnAction(e -> {
            //Enviar a mensagem
            sendMessage();
        });

        bt_file.setOnAction(e -> {
            //enviar o arquivo
            sendFile();
        });
    }

    private void sendMessage() {
    }

    private void sendFile() {
    }


}
