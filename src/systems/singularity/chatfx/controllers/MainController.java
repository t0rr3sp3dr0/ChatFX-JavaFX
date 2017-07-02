package systems.singularity.chatfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.Optional;
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
    private Button bt_showFolder;

    @FXML
    private TextArea ta_chat;

    @FXML
    private ProgressBar progress;

    @FXML
    private TabPane tp_chats;

    @FXML
    private ListView<?> lv_files;

    @FXML
    private Label lb_filename;

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

        bt_showFolder.setOnAction(e -> {

        });
    }

    private void sendMessage() {

    }

    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        File file = fileChooser.showOpenDialog(bt_file.getScene().getWindow());
        if(file != null) {
            lb_filename.setText(file.getName());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Send file");
            alert.setHeaderText("Are you sure?");
            Button OKButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            OKButton.setText("Yes");
            Button CancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            CancelButton.setText("No");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                //enviar o arquivo
            }
            else {
                lb_filename.setText("");
                file = null;
            }
        }
    }
}
