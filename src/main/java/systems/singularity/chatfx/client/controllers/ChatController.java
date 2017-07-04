package systems.singularity.chatfx.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by pedro on 7/3/17.
 */
public class ChatController implements Initializable {
    private final User user;

    public ChatController(User user) {
        this.user = user;
    }

    @FXML
    private Button btFile;

    @FXML
    private Label lbFilename;

    @FXML
    private Label lbRtt;

    @FXML
    private Button btShowFolder;

    @FXML
    private ListView<?> lvFiles;

    @FXML
    private TextField tfMessage;

    @FXML
    private ProgressBar progress;

    @FXML
    private Label lbTime;

    @FXML
    private Button btClear;

    @FXML
    private TextArea taChat;

    @FXML
    private Button btSend;

    @FXML
    private Label lbProgress;

    @Override
    public void initialize(URL location, ResourceBundle resources) {



        try {
            Singleton.getInstance().setFileOnReceiveListener(InetAddress.getByName(user.getAddress()), (address, port, bytes) -> {
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

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        btClear.setOnAction(event -> {

        });

        btSend.setOnAction(event -> {

        });

        btFile.setOnAction(event -> {
            sendFile();
        });

    }

    private void sendFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        final File file = fileChooser.showOpenDialog(btFile.getScene().getWindow());
        if (file != null) {
            lbFilename.setText(file.getName());
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Send file");
            alert.setHeaderText("Are you sure?");
            Button OkButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            OkButton.setText("Yes");
            Button CancelButton = (Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL);
            CancelButton.setText("No");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    new Protocol.Uploader(RDT.getSender(InetAddress.getByName(user.getAddress()), user.getPortFile()), "", file, ($, bytesSent, elapsedTime) -> {
                        double speed = bytesSent / (elapsedTime * 1e9);
                        double p = bytesSent / file.length();
                        double remainingTime = (file.length() - bytesSent) / speed;
                        Platform.runLater(() -> {
                                    progress.setProgress(p);
                                    lbProgress.setText(String.valueOf(p));
                                    lbTime.setText(String.valueOf(remainingTime));
                                }
                        );
                    }).start();
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            } else {
                lbFilename.setText("");
            }
        }
    }
}
