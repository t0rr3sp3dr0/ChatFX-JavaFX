package systems.singularity.chatfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
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
                    //if(usuário registrado) {
                        login();
                    //}
                    //else {
                    //  registra no banco
                    //  faz login
                    //}



                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Error");
                    alert.setHeaderText("Please complete all fields right");
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
        //ir para a tela principal
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/main.fxml"));
            final Parent root = fxmlLoader.load();

            Stage stage = new Stage();
            stage.setTitle("Welcome, " + tf_user.getText());
            stage.setScene(new Scene(root, 720, 430));
            stage.show();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
