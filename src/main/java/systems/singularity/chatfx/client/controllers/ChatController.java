package systems.singularity.chatfx.client.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import org.joda.time.DateTime;
import systems.singularity.chatfx.client.Networking;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.client.db.Database;
import systems.singularity.chatfx.client.db.MessageRepository;
import systems.singularity.chatfx.models.Message;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.util.RDT;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Created by pedro on 7/3/17.
 */
public class ChatController implements Initializable {
    private final User user;
    private final boolean[] downloadInProgress = {false};

    private Node receiveFileNode;
    private Dialog receiveFileDialog;
    private ReceiveFileController receiveFileController;

    @FXML
    private Parent root;

    @FXML
    private ListView<User> participantsList;

    @FXML
    private ListView<Message> messagesList;

    @FXML
    private Label rttLabel;

    @FXML
    private Label speedLabel;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Label progressLabel;

    @FXML
    private Label etaLabel;

    @FXML
    private TextField textField;

    @FXML
    private Button sendButton;

    public ChatController(User user) {
        this.user = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/receive_file.fxml"));
                ChatController.this.receiveFileNode = fxmlLoader.load();
                ChatController.this.receiveFileController = fxmlLoader.getController();
                ChatController.this.receiveFileDialog = new Dialog();

                ChatController.this.receiveFileDialog.setTitle("Receiving File");
                ChatController.this.receiveFileDialog.initModality(Modality.NONE);
                ChatController.this.receiveFileDialog.getDialogPane().setContent(ChatController.this.receiveFileNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            Networking.receiveMessage(this.user, (headers, message) -> {
                try {
                    if (headers.get("Pragma").equals("message")) {
                        MessageRepository.getInstance().insert(message.status("sent"));
                        Networking.sendACK(message, ChatController.this.user);
                    } else if (headers.get("Pragma").equals("ack"))
                        MessageRepository.getInstance().update(MessageRepository.getInstance().get(new Message().id(Integer.parseInt(headers.get("Message-ID"))).status("ack")));
                    else if (headers.get("Pragma").equals("seen"))
                        MessageRepository.getInstance().update(MessageRepository.getInstance().get(new Message().id(Integer.parseInt(headers.get("Message-ID"))).status("seen")));
                } catch (SQLException | InterruptedException | SocketException | UnknownHostException e) {
                    e.printStackTrace();
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            Networking.receiveFile(this.user, (file, progress, speed, remainingTime) -> Platform.runLater(() -> {
                if (!downloadInProgress[0]) {
                    downloadInProgress[0] = true;

                    ChatController.this.receiveFileDialog.setHeaderText(file.getName());
                    ChatController.this.receiveFileDialog.show();
                }

                if (progress == 1) {
                    downloadInProgress[0] = false;

                    ChatController.this.receiveFileDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                    Node closeButton = ChatController.this.receiveFileDialog.getDialogPane().lookupButton(ButtonType.CLOSE);
                    closeButton.managedProperty().bind(closeButton.visibleProperty());
                    closeButton.setVisible(false);

                    ChatController.this.receiveFileDialog.close();
                } else {
                    ChatController.this.receiveFileController.getProgressBar().setProgress(progress);
                    ChatController.this.receiveFileController.getProgressLabel().setText(String.format("%.2f%%", progress * 100));
                    ChatController.this.receiveFileController.getEtaLabel().setText(String.format("%.0fs", remainingTime));
                    ChatController.this.receiveFileController.getSpeedLabel().setText(String.format("%.2f MB/s", speed / (1024 * 1024)));

                }
            }));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            RDT.RTT.Probe probe = new RDT.RTT.Probe(InetAddress.getByName(this.user.getAddress()), this.user.getPortRtt());
            probe.setOnRTTListenner(objects -> Platform.runLater(() -> ChatController.this.rttLabel.setText(String.format("%.3f ms", ((double) objects[0]) / 1e6))));
            probe.start();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        messagesList.setCellFactory(param -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setAlignment(Pos.CENTER_LEFT);
                    setGraphic(null);
                } else
                    try {
                        if (item.getAuthorId().equals(Singleton.getInstance().getUser().getId())) {
                            setAlignment(Pos.CENTER_RIGHT);

                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/sender_message_row.fxml"));
                            setGraphic(fxmlLoader.load());

                            SenderMessageRowController senderRowController = fxmlLoader.getController();
                            senderRowController.getMessageLabel().setText(item.getContent());
                            senderRowController.getMessageLabel().maxWidthProperty().bind(messagesList.widthProperty().add(-26));

                            switch (item.getStatus()) {
                                case "processing":
                                    senderRowController.getCheckImage().setImage(new Image("/icons/ic_schedule.png"));
                                    break;

                                case "sent":
                                    senderRowController.getCheckImage().setImage(new Image("/icons/ic_done.png"));
                                    break;

                                case "seen":
                                    senderRowController.getCheckImage().setImage(new Image("/icons/ic_done_all.png"));
                                    break;

                                default:
                                    break;
                            }
                        } else {
                            setAlignment(Pos.CENTER_LEFT);

                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/receiver_message_row.fxml"));
                            setGraphic(fxmlLoader.load());

                            ReceiverMessageRowController messageRowController = fxmlLoader.getController();
                            messageRowController.getSenderLabel().setText(ChatController.this.user.getUsername());
                            messageRowController.getSenderLabel().maxWidthProperty().bind(messagesList.widthProperty().add(-26));
                            messageRowController.getMessageLabel().setText(item.getContent());
                            messageRowController.getMessageLabel().maxWidthProperty().bind(messagesList.widthProperty().add(-26));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });

        textField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                sendButton.fire();
                textField.requestFocus();
            }
        });

        sendButton.setOnAction(event -> {
            String text = textField.getText();

            if (text != null) {
                final String content = text.trim();

                if (content.length() > 0) {
                    new Thread(() -> {
                        Message message = new Message()
                                .authorId(Singleton.getInstance().getUser().getId())
                                .content(content)
                                .chatId(Singleton.getInstance().getUser().hashCode() & ChatController.this.user.hashCode())
                                .time(DateTime.now().toString())
                                .status("processing");

                        try {
                            Networking.sendMessage(message.id(message.hashCode()), ChatController.this.user);
                            MessageRepository.getInstance().insert(message);
                        } catch (UnknownHostException | SocketException | InterruptedException | SQLException e) {
                            e.printStackTrace();
                        }
                    }).start();

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
                        Networking.sendFile(file, user, "", (($, progress, speed, remainingTime) -> Platform.runLater(() -> {
                            if (progress == 1) {
                                chooseFileButton.setDisable(false);

                                Alert info = new Alert(Alert.AlertType.INFORMATION);
                                info.setHeaderText("Upload Finished Successfully!");
                                info.show();

                                progressBar.setProgress(0);
                                progressLabel.setText(null);
                                etaLabel.setText(null);
                                speedLabel.setText(null);
                            } else {
                                progressBar.setProgress(progress);
                                progressLabel.setText(String.format("%.2f%%", progress * 100));
                                etaLabel.setText(String.format("%.0fs", remainingTime));
                                speedLabel.setText(String.format("%.2f MB/s", speed / (1024 * 1024)));
                            }
                        })));
                    } catch (SocketException | UnknownHostException e) {
                        e.printStackTrace();
                    }
            } else
                chooseFileButton.setDisable(false);
        });

        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true)
                try {
                    List<Message> messages = MessageRepository.getInstance().getAll().stream().filter(message -> message.getChatId().equals(Singleton.getInstance().getUser().hashCode() & ChatController.this.user.hashCode())).collect(Collectors.toList());
                    Platform.runLater(() -> {
                        ChatController.this.messagesList.setItems(FXCollections.observableArrayList(messages));
                        ChatController.this.messagesList.scrollTo(messages.size() - 1);
                    });

                    Thread.sleep(250);
                } catch (SQLException | InterruptedException e) {
                    e.printStackTrace();
                }
        }).start();
    }
}
