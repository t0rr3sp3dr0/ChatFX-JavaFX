package systems.singularity.chatfx.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import systems.singularity.chatfx.server.db.UserRepository;
import systems.singularity.chatfx.structs.User;
import systems.singularity.chatfx.util.java.Utilities;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private Button bt_login;

    @FXML
    private TextField tf_user;

    @FXML
    private PasswordField tf_pass;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            bt_login.setOnAction(e -> {

                if (!tf_user.getText().isEmpty() && !tf_pass.getText().isEmpty() && tf_pass.getText().length() >= 8) {
                    try {
                        //ajustar para o RDT
                        User user = new UserRepository().get(new User(0, tf_user.getText(), "", "", (short) 0, (short) 0, (short) 0, false));
                        String password = Utilities.MD5(tf_pass.getText());
                        if (user != null) {
                            if (user.getPassword().equals(password))
                                login(user);
                            else {
                                Alert alert = new Alert(Alert.AlertType.WARNING);
                                alert.setTitle("Error");
                                alert.setHeaderText("Invalid password!");
                                Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                                exitButton.setText("OK");
                                alert.show();
                            }
                        } else {
                            user = new User(0, tf_user.getText(), password, "address", (short) 0, (short) 0, (short) 0, true);
                            new UserRepository().insert(user);
                            login(user);
                        }
                    } catch (SQLException | NoSuchAlgorithmException e1) {
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

    private void login(User user) {
        //ir para a tela principal
        try {
            user.setStatus(true);
            new UserRepository().update(user);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/main.fxml"));
            final Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Welcome, " + tf_user.getText());
            stage.setScene(new Scene(root, 720, 430));
            stage.show();

            stage.setOnCloseRequest(e -> {
                user.setStatus(false);
                try {
                    new UserRepository().update(user);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            });
        } catch (IOException | SQLException e1) {
            e1.printStackTrace();
        }
    }
}
