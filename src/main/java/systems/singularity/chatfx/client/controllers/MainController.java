package systems.singularity.chatfx.client.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.models.Chat;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;
import systems.singularity.chatfx.util.Variables;
import systems.singularity.chatfx.util.javafx.StageTools;

import java.io.IOException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by caesa on 01/07/2017.
 */
public class MainController implements Initializable {
    public static final StageTools stageTools = new StageTools();
    private static MainController ourInstance = new MainController();
    private final Chat[] chat = new Chat[1];
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem discardModuleMenuItem;
    @FXML
    private MenuItem newChatMenuItem;
    @FXML
    private TableView<Chat> tableView;
    @FXML
    private TableColumn<Chat, String> tableColumn;
    @FXML
    private TabPane tabPane;
    private Node discardModuleNode;

    private MainController() {
        // Avoid class instantiation
    }

    public static MainController getInstance() {
        return MainController.ourInstance;
    }

    public void setChat(Chat chat) {
        this.chat[0] = chat;
    }

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
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);

        SelectionModel<Chat> tableViewSelectionModel = tableView.getSelectionModel();
        tableViewSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null)
                        newTab(newValue);
                }
        );

        SelectionModel<Tab> tabSelectionModel = tabPane.getSelectionModel();
        tabSelectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null)
                tableViewSelectionModel.select(null);
            else
                for (Chat chat : tableView.getItems())
                    if (chat.getName().equals(newValue.getId())) {
                        tableViewSelectionModel.select(chat);
                        break;
                    }
        });

        tableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

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

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/main.fxml"));
                fxmlLoader.setController(MainController.getInstance());
                final Parent root = fxmlLoader.load();
                Platform.runLater(() -> {
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root, 720, 430));
                    stage.show();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        });

        new Thread(() -> {
            try {
                //noinspection InfiniteLoopStatement
                while (true) {
                    Map<String, String> map = new HashMap<>();
                    map.put("Authorization", "Basic " + Singleton.getInstance().getToken());
                    map.put("Pragma", "get;chats");
                    final RDT.Sender sender = RDT.getSender(Variables.Server.address, Variables.Server.port);
                    Protocol.Sender.sendMessage(sender, map, "");

                    RDT.getReceiver(sender).setOnReceiveListener(Variables.Server.address, (Protocol.Receiver) (address, port1, headers, message) -> {
                        List<Chat> chats = Arrays.asList(new Gson().fromJson(message, Chat[].class));

                        Platform.runLater(() -> tableView.setItems(FXCollections.observableArrayList(chats)));
                    });

                    Thread.sleep(250);
                }
            } catch (SocketException | InterruptedException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void newTab(Chat chat) {
        SelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        for (Tab e : tabPane.getTabs())
            if (e.getId() != null && e.getId().equals(this.chat[0].getName())) {
                selectionModel.select(e);
                return;
            }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/layouts/chat.fxml"));
            fxmlLoader.setController(new ChatController(chat));

            Tab tab = new Tab();
            tab.setId(String.valueOf(this.chat[0].getId()));
            tab.setText(this.chat[0].getName());
            tab.setClosable(true);
            tab.setContent(fxmlLoader.load());
            tabPane.getTabs().add(tab);
            selectionModel.select(tab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
