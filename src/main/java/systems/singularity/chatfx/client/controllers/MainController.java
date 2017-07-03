package systems.singularity.chatfx.client.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.models.User;
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
import java.util.*;
import java.util.List;

/**
 * Created by caesa on 01/07/2017.
 */
public class MainController implements Initializable {

    @FXML
    private Button btSend;

    @FXML
    private Button btFile;

    @FXML
    private Button btShowFolder;

    @FXML
    private Button btClear;

    @FXML
    private ListView<User> lvUsers;

    @FXML
    private TextArea taChat;

    @FXML
    private ProgressBar progress;

    @FXML
    private TabPane tpChats;

    @FXML
    private ListView<?> lvFiles;

    @FXML
    private Label lbFilename;

    @FXML
    private Label lbRtt;

    @FXML
    private Label lbTime;

    @FXML
    private Label lbProgress;

    @FXML
    private TextField tfMessage;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Map<String, String> map = new HashMap<>();
            map.put("Authorization", "Basic " + new String(Base64.getEncoder().encode(("blá:blá").getBytes())));
            map.put("Pragma", "get;users");
            final RDT.Sender sender = RDT.getSender(LoginController.getInetAddress(), LoginController.getPort());
            Protocol.Sender.sendMessage(sender, map, "Manda esses user aí, seu porra!");

            RDT.getReceiver(sender).setOnReceiveListener(LoginController.getInetAddress(), (Protocol.Receiver) (address, port1, headers, message) -> {
                String[] pragma = headers.get("Pragma").split(";");
                Gson json = new Gson();
                List<User> users = Arrays.asList(json.fromJson(pragma[1], User[].class));


                lvUsers.setItems(FXCollections.observableArrayList(users));
                lvUsers.setCellFactory(param -> new ListCell<User>(){
                    @Override
                    protected void updateItem(User item, boolean empty) {
                        setText(item.getUsername());
                    }
                });
            });

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
        } catch (SocketException | InterruptedException | UnknownHostException e) {
            e.printStackTrace();
        }

        btSend.setOnAction(e -> {
            //Enviar a mensagem
            sendMessage();
        });

        btFile.setOnAction(e -> {
            //enviar o arquivo
            sendFile();
        });

        btShowFolder.setOnAction(e -> {
            try {
                Desktop.getDesktop().open(new File(Constants.PERSISTENT_DIRECTORY));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        btClear.setOnAction(e -> {
            //limpar o histórico
            clearChat();
        });


    }

    private void sendMessage() {
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
                    new Protocol.Uploader(RDT.getSender(InetAddress.getByName("192.168.43.78"), 2020), "", file, ($, bytesSent, elapsedTime) -> {
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

    private void clearChat() {
    }
}
