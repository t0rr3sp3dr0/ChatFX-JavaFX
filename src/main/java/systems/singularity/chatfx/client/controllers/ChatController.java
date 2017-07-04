package systems.singularity.chatfx.client.controllers;

import javafx.fxml.Initializable;
import systems.singularity.chatfx.models.User;

import java.net.URL;
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
