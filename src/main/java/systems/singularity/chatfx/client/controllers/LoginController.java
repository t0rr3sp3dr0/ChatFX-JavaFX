package systems.singularity.chatfx.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;
import systems.singularity.chatfx.util.java.Utilities;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    private boolean[] logged = new boolean[3];

    @FXML
    private TextField userTextField;

    @FXML
    private TextField portTextField;

    @FXML
    private TextField rttTextField;

    @FXML
    private VBox root;

    @FXML
    private Button loginButton;

    @FXML
    private TextField fileTextField;

    @FXML
    private TextField hostTextField;

    @FXML
    private PasswordField passTextField;

    @FXML
    private TextField chatTextField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            loginButton.setOnAction(e -> {
                if (!userTextField.getText().isEmpty() && !passTextField.getText().isEmpty() && passTextField.getText().length() >= 8 && hostTextField.getText().length() > 0 && portTextField.getText().trim().length() > 0)
                    try {
                        InetAddress host = InetAddress.getByName(hostTextField.getText());
                        int port = Integer.parseInt(portTextField.getText());

                        String password = Utilities.md5(passTextField.getText());
                        String token = new String(Base64.getEncoder().encode((userTextField.getText() + ":" + password).getBytes()));

                        openChatConnection(host, port, token);
                        openFileConnection(host, port, token);
                        openRTTConnection(host, port, token);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                else
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("Error");
                        String error = "";
                        if (userTextField.getText().isEmpty())
                            error = "Username field is invalid.";
                        else if (passTextField.getText().isEmpty())
                            error = "Password field is invalid.";
                        else if (passTextField.getText().length() < 8)
                            error = "Password field is invalid. Minimum of 8 characters.";
                        else if (hostTextField.getText().length() <= 0)
                            error = "Invalid server hostname.";
                        else if (portTextField.getText().length() <= 0)
                            error = "Invalid server port.";
                        alert.setHeaderText(error);
                        Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                        exitButton.setText("OK");
                        alert.show();
                    });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void accessDenied() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Access Denied");
        alert.setContentText("Please, verify your credentials and try again.");
        Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        exitButton.setText("OK");
        alert.show();
    }

    private void openChatConnection(InetAddress host, int port, String token) throws SocketException, UnknownHostException, InterruptedException {
        RDT.Sender chatSender = RDT.uniqueSender(host, port);

        try {
            RDT.Receiver chatReceiver = RDT.getReceiver(Integer.parseInt(chatTextField.getText()));
            Field receiverSocker = chatReceiver.getClass().getDeclaredField("socket");
            receiverSocker.setAccessible(true);

            Field field = chatSender.getClass().getDeclaredField("socket");
            field.setAccessible(true);
            field.set(chatSender, receiverSocker.get(chatReceiver));
        } catch (NumberFormatException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        Map<String, String> chatHeaders = new HashMap<>();
        chatHeaders.put("Authorization", "Basic " + token);
        chatHeaders.put("Pragma", "login;chat");

        Singleton.getInstance().setChatReceiver(RDT.getReceiver(chatSender));
        Singleton.getInstance().setChatOnReceiveListener(host, (Protocol.Receiver) (address, _port, headers, _message) -> {
            String[] pragma = headers.get("Pragma").split(";");
            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                System.out.println("\n\nLOGIN: CHAT\n\n");

                this.logged[0] = true;
                login();
            } else
                Platform.runLater(LoginController.this::accessDenied);
        });

        Protocol.Sender.sendMessage(chatSender, chatHeaders, "");
    }

    private void openFileConnection(InetAddress host, int port, String token) throws SocketException, UnknownHostException, InterruptedException {
        RDT.Sender fileSender = RDT.uniqueSender(host, port);

        try {
            RDT.Receiver fileReceiver = RDT.getReceiver(Integer.parseInt(fileTextField.getText()));
            Field receiverSocker = fileReceiver.getClass().getDeclaredField("socket");
            receiverSocker.setAccessible(true);

            Field field = fileSender.getClass().getDeclaredField("socket");
            field.setAccessible(true);
            field.set(fileSender, receiverSocker.get(fileReceiver));
        } catch (NumberFormatException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        Map<String, String> fileHeaders = new HashMap<>();
        fileHeaders.put("Authorization", "Basic " + token);
        fileHeaders.put("Pragma", "login;file");

        Singleton.getInstance().setFileReceiver(RDT.getReceiver(fileSender));
        Singleton.getInstance().setFileOnReceiveListener(host, (Protocol.Receiver) (address, _port, headers, _message) -> {
            String[] pragma = headers.get("Pragma").split(";");
            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                System.out.println("\n\nLOGIN: FILE\n\n");

                this.logged[1] = true;
                login();
            } else
                Platform.runLater(LoginController.this::accessDenied);
        });

        Protocol.Sender.sendMessage(fileSender, fileHeaders, "");
    }

    private void openRTTConnection(InetAddress host, int port, String token) throws SocketException, UnknownHostException, InterruptedException {
        RDT.Sender rttSender = RDT.uniqueSender(host, port);

        try {
            RDT.Receiver rttReceiver = RDT.getReceiver(Integer.parseInt(rttTextField.getText()));
            Field receiverSocker = rttReceiver.getClass().getDeclaredField("socket");
            receiverSocker.setAccessible(true);

            Field field = rttSender.getClass().getDeclaredField("socket");
            field.setAccessible(true);
            field.set(rttSender, receiverSocker.get(rttReceiver));
        } catch (NumberFormatException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        Map<String, String> rttHeaders = new HashMap<>();
        rttHeaders.put("Authorization", "Basic " + token);
        rttHeaders.put("Pragma", "login;rtt");

        RDT.Receiver rttReceiver = RDT.getReceiver(rttSender);
        rttReceiver.setOnReceiveListener(host, (Protocol.Receiver) (address, _port, headers, _message) -> {
            String[] pragma = headers.get("Pragma").split(";");
            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                System.out.println("\n\nLOGIN: RTT\n\n");

                new RDT.RTT.Echo(rttSender).start();

                this.logged[2] = true;
                login();
            } else
                Platform.runLater(LoginController.this::accessDenied);
        });

        Protocol.Sender.sendMessage(rttSender, rttHeaders, "");
    }

    private void login() {
        if (this.logged[0] && this.logged[1] && this.logged[2]) {
            this.logged = new boolean[3];

            try {
                Singleton.getInstance().setUsername(userTextField.getText());
                Singleton.getInstance().setToken(new String(Base64.getEncoder().encode((userTextField.getText() + ":" + Utilities.md5(passTextField.getText())).getBytes())));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/main.fxml"));
                fxmlLoader.setController(MainController.getInstance());
                final Parent root = fxmlLoader.load();

                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root, 720, 480));
                    stage.show();

                    systems.singularity.chatfx.util.javafx.Utilities.setOnCloseRequest(stage);

                    ((Stage) LoginController.this.root.getScene().getWindow()).close();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
