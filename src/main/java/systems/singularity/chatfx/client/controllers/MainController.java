package systems.singularity.chatfx.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import systems.singularity.chatfx.util.Constants;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by caesa on 01/07/2017.
 */
public class MainController implements Initializable {

    @FXML
    private Button bt_send;

    @FXML
    private Button bt_file;

    @FXML
    private Button bt_showFolder;

    @FXML
    private Button bt_clear;

    @FXML
    private ListView<?> lv_users;

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
    private Label lb_rtt;

    @FXML
    private Label lb_time;

    @FXML
    private Label lb_progress;

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
            try {
                Desktop.getDesktop().open(new File(Constants.PERSISTENT_DIRECTORY));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        bt_clear.setOnAction(e -> {
            //limpar o histórico
            clearChat();
        });

        try {
            RDT.Receiver receiver = RDT.getReceiver(2020);

            receiver.setOnReceiveListener(null, (address, port, bytes) -> {
            //System.out.println("\t" + address.toString());

            Map<String, String> headers = Protocol.extractHeaders(bytes);
            final long contentLength = Long.parseLong(headers.get("Content-Length"));

            Protocol.Downloader downloader = Protocol.getDownloader(headers);
                downloader.setCallback((file, bytesReceived, elapsedTime) -> {
                if (bytesReceived == contentLength)
                    System.out.println("FINISHED");
                else
                    System.out.println((elapsedTime / 1e9) + "s");
            });
            downloader.add(Protocol.extractData(bytes));
        });
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
    }

    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File file = fileChooser.showOpenDialog(bt_file.getScene().getWindow());
        if (file != null) {
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
                try {
                    new Protocol.Uploader(RDT.getSender(InetAddress.getByName("192.168.43.78"), 2020), "", file, ($, bytesSent, elapsedTime) -> {
                        double speed = bytesSent / (elapsedTime * 1e9);
                        double p = bytesSent / file.length();
                        double remainingTime = (file.length() - bytesSent) / speed;
                        Platform.runLater(() -> {
                                    progress.setProgress(p);
                                    lb_progress.setText(String.valueOf(p));
                                    lb_time.setText(String.valueOf(remainingTime));
                                }
                        );
                    }).start();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            } else {
                lb_filename.setText("");
            }
        }
    }

    private void clearChat() {
    }
}
