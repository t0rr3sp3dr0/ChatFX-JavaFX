package systems.singularity.chatfx.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by phts on 05/07/17.
 */
public class ReceiverMessageRowController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="senderLabel"
    private Label senderLabel; // Value injected by FXMLLoader

    @FXML // fx:id="messageLabel"
    private Label messageLabel; // Value injected by FXMLLoader

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert senderLabel != null : "fx:id=\"senderLabel\" was not injected: check your FXML file 'receiver_message_row.fxml'.";
        assert messageLabel != null : "fx:id=\"messageLabel\" was not injected: check your FXML file 'receiver_message_row.fxml'.";
    }

    public Label getSenderLabel() {
        return senderLabel;
    }

    public Label getMessageLabel() {
        return messageLabel;
    }
}

