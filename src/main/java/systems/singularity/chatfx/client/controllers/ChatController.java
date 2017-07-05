package systems.singularity.chatfx.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import systems.singularity.chatfx.client.Networking;
import systems.singularity.chatfx.models.Message;
import systems.singularity.chatfx.models.User;

import java.io.File;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Created by pedro on 7/3/17.
 */
public class ChatController implements Initializable {
    private final User user;

    @FXML
    private ResourceBundle resources;
    @FXML
    private URL location;
    @FXML
    private Button clearChatButton;
    @FXML
    private ListView<File> listView;
    @FXML
    private Label speedLabel;
    @FXML
    private VBox root;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button chooseFileButton;
    @FXML
    private Button openDownloadsButton;
    @FXML
    private Label progressLabel;
    @FXML
    private Label etaLabel;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private Button sendButton;

    public ChatController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Networking.receiveMessage(this.user, message -> Platform.runLater(() -> textArea.setText(textArea.getText() + message.getContent() + '\n')));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            Networking.receiveFile(this.user, (progress, speed, remainingTime) -> {
                if (progress == 1)
                    System.out.println("FINISHED");
                else
                    System.out.println(remainingTime + "s");
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        sendButton.setOnAction(event -> {
            String text = textField.getText();

            if (text != null) {
                final String content = text.trim();

                if (content.length() > 0) {
                    new Thread(() -> {
                        Message message = new Message()
                                .id(content.hashCode())
                                .content(content.trim());

                        try {
                            Networking.sendMessage(message, ChatController.this.user);
                        } catch (UnknownHostException | SocketException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();

                    textArea.setText(textArea.getText() + textField.getText() + '\n');
                    textField.setText(null);
                }
            }
        });

        chooseFileButton.setOnAction(event -> {
            chooseFileButton.setDisable(true);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            final File file = fileChooser.showOpenDialog(this.root.getScene().getWindow());
            if (file != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setHeaderText(String.format("Do you want to send \"%s\"?", file.getName()));
                ((Button) confirm.getDialogPane().lookupButton(ButtonType.OK)).setText("Yes");
                ((Button) confirm.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("No");

                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK)
                    try {
                        Networking.sendFile(file, user, "", ((progress, speed, remainingTime) -> {
                            if (progress == 1)
                                Platform.runLater(() -> {
                                    chooseFileButton.setDisable(false);

                                    Alert info = new Alert(Alert.AlertType.INFORMATION);
                                    info.setHeaderText("Upload Finished Successfully!");
                                    info.show();

                                    progressBar.setProgress(0);
                                    progressLabel.setText(null);
                                    etaLabel.setText(null);
                                    speedLabel.setText(null);
                                });
                            else
                                Platform.runLater(() -> {
                                    progressBar.setProgress(progress);
                                    progressLabel.setText(String.format("%.2f%%", progress * 100));
                                    etaLabel.setText(String.format("%.0fs", remainingTime));
                                    speedLabel.setText(String.format("%.2f MB/s", speed / (1024 * 1024)));
                                });
                        }));
                    } catch (SocketException | UnknownHostException e) {
                        e.printStackTrace();
                    }
            } else
                chooseFileButton.setDisable(false);
        });
    }
}
