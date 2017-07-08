package systems.singularity.chatfx.client.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;
import systems.singularity.chatfx.util.javafx.StageTools;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by caesa on 01/07/2017.
 */
public class MainController implements Initializable {
    public static final StageTools stageTools = new StageTools();

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem discardModuleMenuItem;

    @FXML
    private MenuItem newChatMenuItem;

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> tableColumn;

    @FXML
    private TabPane tabPane;

    private Node discardModuleNode;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/discard_module.fxml"));
                MainController.this.discardModuleNode = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stageTools.setTabPane(tabPane);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        SelectionModel<User> tableViewSelectionModel = tableView.getSelectionModel();
        tableViewSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null)
                openTab(newValue);
        });

        SelectionModel<Tab> tabSelectionModel = tabPane.getSelectionModel();
        tabSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                tableViewSelectionModel.select(null);
            else
                for (User user : tableView.getItems())
                    if (user.getUsername().equals(newValue.getId())) {
                        tableViewSelectionModel.select(user);
                        break;
                    }
        });

        tableColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        discardModuleMenuItem.setOnAction(event -> {
            Dialog dialog = new Dialog();
            dialog.initModality(Modality.NONE);
            dialog.getDialogPane().setContent(discardModuleNode);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);


            Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
            closeButton.managedProperty().bind(closeButton.visibleProperty());
            closeButton.setVisible(false);

            dialog.show();
        });

        newChatMenuItem.setOnAction(event -> {

        });

        new Thread(() -> {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    Map<String, String> map = new HashMap<>();
                    map.put("Authorization", "Basic " + Singleton.getInstance().getToken());
                    map.put("Pragma", "get;users");
                    final RDT.Sender sender = RDT.getSender(LoginController.getInetAddress(), LoginController.getPort());
                    Protocol.Sender.sendMessage(sender, map, "Manda esses user aí, seu porra!");

                    RDT.getReceiver(sender).setOnReceiveListener(LoginController.getInetAddress(), (Protocol.Receiver) (address, port1, headers, message) -> {
                        List<User> users = Arrays.stream(new Gson().fromJson(message, User[].class)).filter(user -> {
                            if (user.getUsername().equals(Singleton.getInstance().getUsername())) {
                                Singleton.getInstance().setUser(user);
                                return false;
                            }

                            return user.getStatus();
                        }).collect(Collectors.toList());


                        Platform.runLater(() -> tableView.setItems(FXCollections.observableArrayList(users)));

                        final List<Tab> tabsToBeClosed = tabPane.getTabs().filtered(tab -> {
                            for (User user : users)
                                if (user.getUsername().equals(tab.getId()))
                                    return false;
                            return true;
                        });
                        for (Tab tab : tabsToBeClosed)
                            Platform.runLater(() -> tabPane.getTabs().remove(tab));

                        final List<User> usersToBeOpened = users.stream().filter(user -> {
                            for (Tab tab : tabPane.getTabs())
                                if (tab.getId().equals(user.getUsername()))
                                    return false;
                            return true;
                        }).collect(Collectors.toList());
                        for (User user : usersToBeOpened)
                            Platform.runLater(() -> MainController.this.openTab(user));
                    });

                    Thread.sleep(250);
                }
            } catch (SocketException | InterruptedException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void openTab(User user) {
        SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        for (Tab e : tabPane.getTabs())
            if (e.getId() != null && e.getId().equals(user.getUsername())) {
                selectionModel.select(e);
                return;
            }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/chat.fxml"));
            fxmlLoader.setController(new ChatController(user, selectionModel));

            Tab tab = new Tab();
            tab.setId(user.getUsername());
            tab.setText(user.getUsername());
            tab.setClosable(true);
            tab.setContent(fxmlLoader.load());
            tabPane.getTabs().add(tab);
            selectionModel.select(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeTab(User user) {
        SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        for (Tab e : tabPane.getTabs())
            if (e.getId() != null && e.getId().equals(user.getUsername())) {
                tabPane.getTabs().remove(e);
                return;
            }
    }
}
