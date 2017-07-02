package systems.singularity.chatfx.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import systems.singularity.chatfx.db.UserRepository;
import systems.singularity.chatfx.structs.User;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by caesa on 01/07/2017.
 */
public class ServerController implements Initializable {

    @FXML
    private TableView<User> tv_users;

    @FXML
    private TableColumn<User, String> tc_username;

    @FXML
    private TableColumn<User, Short> tc_rtt;

    @FXML
    private TableColumn<User, Short> tc_file;

    @FXML
    private TableColumn<User, Short> tc_chat;

    @FXML
    private TableColumn<User, Short> tc_ip;

    @FXML
    private TableColumn<User, Boolean> tc_status;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        updateTable();

        int delay = 2000;   // delay de 2 seg.
        int interval = 1000;  // intervalo de 1 seg.
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                updateTable();
            }
        }, delay, interval);

    }

    private void updateTable() {
        try {
            tv_users.setItems(FXCollections.observableArrayList(new UserRepository().getAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tc_username.setCellValueFactory(new PropertyValueFactory<>("username"));
        tc_ip.setCellValueFactory(new PropertyValueFactory<>("address"));
        tc_chat.setCellValueFactory(new PropertyValueFactory<>("port_chat"));
        tc_file.setCellValueFactory(new PropertyValueFactory<>("port_file"));
        tc_rtt.setCellValueFactory(new PropertyValueFactory<>("port_rtt"));
        tc_status.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
}
