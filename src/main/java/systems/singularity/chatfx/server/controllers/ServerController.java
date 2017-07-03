package systems.singularity.chatfx.server.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.server.db.UserRepository;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

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
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                Platform.runLater(ServerController.this::updateTable);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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
