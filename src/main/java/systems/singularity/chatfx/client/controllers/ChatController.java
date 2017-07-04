package systems.singularity.chatfx.client.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
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


    @Override
    public void initialize(URL location, ResourceBundle resources) {



    }



}
