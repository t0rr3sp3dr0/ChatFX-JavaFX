package systems.singularity.chatfx.server.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private TableView<User> tvUsers;

    @FXML
    private TableColumn<User, String> tcUsername;

    @FXML
    private TableColumn<User, Short> tcRtt;

    @FXML
    private TableColumn<User, Short> tcFile;

    @FXML
    private TableColumn<User, Short> tcChat;

    @FXML
    private TableColumn<User, Short> tcIp;

    @FXML
    private TableColumn<User, Boolean> tcStatus;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    final ObservableList<User> users = FXCollections.observableArrayList(UserRepository.getInstance().getAll());
                    Platform.runLater(() -> ServerController.this.updateTable(users));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updateTable(ObservableList<User> users) {
        tvUsers.setItems(users);

        tcUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        tcIp.setCellValueFactory(new PropertyValueFactory<>("address"));
        tcChat.setCellValueFactory(new PropertyValueFactory<>("portChat"));
        tcFile.setCellValueFactory(new PropertyValueFactory<>("portFile"));
        tcRtt.setCellValueFactory(new PropertyValueFactory<>("portRtt"));
        tcStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
}
