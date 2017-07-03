package systems.singularity.chatfx.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.server.db.UserRepository;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;
import systems.singularity.chatfx.util.java.Utilities;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

public class LoginController implements Initializable {

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            bt_login.setOnAction(e -> {

                if (!tf_user.getText().isEmpty() && !tf_pass.getText().isEmpty() && tf_pass.getText().length() >= 8) {
                    try {
                        //ajustar para o RDT
                        String password = Utilities.MD5(tf_pass.getText());
                        int port = Integer.parseInt(tf_port.getText());
                        Map<String, String> map = new HashMap<>();
                        map.put("Authorization", "Basic" + new String(Base64.getEncoder().encode((tf_user + ":" + password).getBytes())));
                        map.put("Pragma", "login;chat");
                        RDT.Sender sender = RDT.getSender(InetAddress.getByName(tf_ip.getText()), port);
                        Protocol.Sender.sendMessage(sender, map, "Vai tomar no cu, pasg!");

                        RDT.getReceiver(sender).setOnReceiveListener(InetAddress.getByName(tf_ip.getText()), (Protocol.Receiver) (address, port1, headers, message) -> {
                            String[] basic = new String(Base64.getDecoder().decode(headers.get("Authorization").split(" ")[1])).split(":");
                            String[] pragma = headers.get("Pragma").split(";");

                            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                                try {
                                    Singleton.getInstance().setChatReceiver(RDT.getReceiver(sender));
                                    this.logged[0] = true;
                                    login();
                                } catch (SocketException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        });

                        map = new HashMap<>();
                        map.put("Authorization", "Basic" + new String(Base64.getEncoder().encode((tf_user + ":" + password).getBytes())));
                        map.put("Pragma", "login;file");
                        Protocol.Sender.sendMessage(sender, map, "Vai tomar no cu, pasg!");

                        RDT.getReceiver(sender).setOnReceiveListener(InetAddress.getByName(tf_ip.getText()), (Protocol.Receiver) (address, port1, headers, message) -> {
                            String[] basic = new String(Base64.getDecoder().decode(headers.get("Authorization").split(" ")[1])).split(":");
                            String[] pragma = headers.get("Pragma").split(";");

                            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                                try {
                                    Singleton.getInstance().setFileReceiver(RDT.getReceiver(sender));
                                    this.logged[1] = true;
                                    login();
                                } catch (SocketException e1) {
                                    e1.printStackTrace();
                                }
                            }

                        });

                        map = new HashMap<>();
                        map.put("Authorization", "Basic" + new String(Base64.getEncoder().encode((tf_user + ":" + password).getBytes())));
                        map.put("Pragma", "login;rtt");
                        Protocol.Sender.sendMessage(sender, map, "Vai tomar no cu, pasg!");

                        RDT.getReceiver(sender).setOnReceiveListener(InetAddress.getByName(tf_ip.getText()), (Protocol.Receiver) (address, port1, headers, message) -> {
                            String[] basic = new String(Base64.getDecoder().decode(headers.get("Authorization").split(" ")[1])).split(":");
                            String[] pragma = headers.get("Pragma").split(";");

                            if (pragma[0].equals("login") && !pragma[1].equals("401")) {
                                new RDT.RTT.Echo(sender).start();
                                this.logged[2] = true;
                                login();
                            }
                        });
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
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/main.fxml"));
                final Parent root = fxmlLoader.load();

                Stage stage = new Stage();
                stage.setScene(new Scene(root, 720, 430));
                stage.show();

                stage.setOnCloseRequest(e -> {

                });
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }
}
