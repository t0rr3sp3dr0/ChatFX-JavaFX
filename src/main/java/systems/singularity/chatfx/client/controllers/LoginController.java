package systems.singularity.chatfx.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;
import systems.singularity.chatfx.util.java.Utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private static InetAddress inetAddress;
    private static int port;
    @FXML
    private Button bt_login;
    @FXML
    private TextField tf_user;
    @FXML
    private PasswordField tf_pass;
    @FXML
    private TextField tf_ip;
    @FXML
    private TextField tf_port;
    private boolean[] logged = new boolean[3];

    public static InetAddress getInetAddress() {
        return inetAddress;
    }

    public static int getPort() {
        return port;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            bt_login.setOnAction(e -> {

                if (!tf_user.getText().isEmpty() && !tf_pass.getText().isEmpty() && tf_pass.getText().length() >= 8) {
                    try {
                        inetAddress = InetAddress.getByName(tf_ip.getText());
                        port = Integer.parseInt(tf_port.getText());
                        //ajustar para o RDT
                        String password = Utilities.md5(tf_pass.getText());
                        Map<String, String> map = new HashMap<>();
                        map.put("Authorization", "Basic " + new String(Base64.getEncoder().encode((tf_user.getText() + ":" + password).getBytes())));
                        map.put("Pragma", "login;chat");

                        RDT.Sender chatSender = RDT.uniqueSender(inetAddress, port);

                        Singleton.getInstance().setChatReceiver(RDT.getReceiver(chatSender));
                        Singleton.getInstance().setChatOnReceiveListener(inetAddress, (Protocol.Receiver) (address, port1, headers, message) -> {
                            String[] pragma = headers.get("Pragma").split(";");
                            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                                System.out.println("\n\nLOGIN: CHAT\n\n");

                                this.logged[0] = true;
                                login();
                            }
                        });

                        Protocol.Sender.sendMessage(chatSender, map, "Vai tomar no cu, pasg!");


                        RDT.Sender fileSender = RDT.uniqueSender(inetAddress, port);

                        map = new HashMap<>();
                        map.put("Authorization", "Basic " + new String(Base64.getEncoder().encode((tf_user.getText() + ":" + password).getBytes())));
                        map.put("Pragma", "login;file");

                        Singleton.getInstance().setFileReceiver(RDT.getReceiver(fileSender));
                        Singleton.getInstance().setFileOnReceiveListener(inetAddress, (Protocol.Receiver) (address, port1, headers, message) -> {
                            String[] pragma = headers.get("Pragma").split(";");
                            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                                System.out.println("\n\nLOGIN: FILE\n\n");

                                this.logged[1] = true;
                                login();
                            }
                        });

                        Protocol.Sender.sendMessage(fileSender, map, "Vai tomar no cu, file!");


                        RDT.Sender rttSender = RDT.uniqueSender(inetAddress, port);

                        map = new HashMap<>();
                        map.put("Authorization", "Basic " + new String(Base64.getEncoder().encode((tf_user.getText() + ":" + password).getBytes())));
                        map.put("Pragma", "login;rtt");

                        RDT.Receiver rttReceiver = RDT.getReceiver(rttSender);
                        rttReceiver.setOnReceiveListener(inetAddress, (Protocol.Receiver) (address, port1, headers, message) -> {
                            String[] pragma = headers.get("Pragma").split(";");
                            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                                System.out.println("\n\nLOGIN: RTT\n\n");

                                new RDT.RTT.Echo(rttSender).start();

                                this.logged[2] = true;
                                login();
                            }
                        });

                        Protocol.Sender.sendMessage(rttSender, map, "Vai tomar no cu, rtt!");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    String error = "";
                    if (tf_user.getText().isEmpty())
                        error = "Username field is invalid.";
                    else if (tf_pass.getText().isEmpty())
                        error = "Password field is invalid.";
                    else if (tf_pass.getText().length() < 8)
                        error = "Password field is invalid. Minimum of 8 characters.";
                    alert.setHeaderText(error);
                    Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                    exitButton.setText("OK");
                    alert.show();
                }
            });
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void login() {
        if (this.logged[0] && this.logged[1] && this.logged[2]) {
            this.logged = new boolean[3];

            try {
                Singleton.getInstance().setUsername(tf_user.getText());
                Singleton.getInstance().setToken(new String(Base64.getEncoder().encode((tf_user.getText() + ":" + Utilities.md5(tf_pass.getText())).getBytes())));
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/main.fxml"));
                final Parent root = fxmlLoader.load();

                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root, 720, 430));
                    stage.show();

                    stage.setOnCloseRequest(e -> {

                    });
                });
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
}
