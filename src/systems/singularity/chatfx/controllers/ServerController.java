package systems.singularity.chatfx.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by caesa on 01/07/2017.
 */
public class ServerController implements Initializable{

    @FXML
    private TableView<?> tv_users;

    @FXML
    private TableColumn<?, ?> tc_username;

    @FXML
    private TableColumn<?, ?> tc_rtt;

    @FXML
    private TableColumn<?, ?> tc_file;

    @FXML
    private TableColumn<?, ?> tc_chat;

    @FXML
    private TableColumn<?, ?> tc_ip;

    @FXML
    private TableColumn<?, ?> tc_status;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


    }
}
