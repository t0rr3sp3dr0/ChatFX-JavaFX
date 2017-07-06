package systems.singularity.chatfx.client.controllers;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import systems.singularity.chatfx.client.Singleton;
import systems.singularity.chatfx.client.db.ChatRepository;
import systems.singularity.chatfx.client.db.MemberRepository;
import systems.singularity.chatfx.models.Chat;
import systems.singularity.chatfx.models.Member;
import systems.singularity.chatfx.models.Message;
import systems.singularity.chatfx.models.User;
import systems.singularity.chatfx.util.Protocol;
import systems.singularity.chatfx.util.RDT;
import systems.singularity.chatfx.util.Variables;

import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by phts on 06/07/17.
 */
public class NewChatController implements Initializable {

    @FXML
    private Parent root;

    @FXML
    private Button button;

    @FXML
    private ListView<User> listView;

    @FXML
    private TextField textField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            try {
                //noinspection InfiniteLoopStatement
                Map<String, String> map = new HashMap<>();
                map.put("Authorization", "Basic " + Singleton.getInstance().getToken());
                map.put("Pragma", "get;users");
                final RDT.Sender sender = RDT.getSender(Variables.Server.address, Variables.Server.port);
                Protocol.Sender.sendMessage(sender, map, "");

                RDT.getReceiver(sender).setOnReceiveListener(Variables.Server.address, (Protocol.Receiver) (address, port1, headers, message) -> {
                    List<User> users = Arrays.stream(new Gson().fromJson(message, User[].class)).filter(user -> {
                        if (user.getUsername().equals(Singleton.getInstance().getUsername())) {
                            Singleton.getInstance().setUser(user);
                            return false;
                        }

                        return user.getStatus();
                    }).collect(Collectors.toList());

                    Platform.runLater(() ->
                            listView.setItems(FXCollections.observableArrayList(users)));
                });
            } catch (SocketException | InterruptedException | UnknownHostException e) {
                e.printStackTrace();
            }
        }).start();

        button.setOnAction(event -> {
            if (!textField.getText().isEmpty()) {
                List<User> users = new ArrayList<>(listView.getSelectionModel().getSelectedItems());
                users.add(Singleton.getInstance().getUser());

                try {
                    Chat chat = new Chat().name(textField.getText());
                    chat.setGroup(users.size() > 2);
                    chat.id(chat.hashCode());
                    ArrayList<Member> members = new ArrayList<>();
                    for (User user : users) {
                        Member member = new Member()
                                .chatId(chat.getId())
                                .id(user.getId());
                        member.setId(member.hashCode());
                        members.add(member);
                        MemberRepository.getInstance().insert(member);
                    }

                    ChatRepository.getInstance().insert(chat);
                    MainController.getInstance().setChat(chat);

                    new Thread(() -> {
                        try {
                            //noinspection InfiniteLoopStatement
                            Map<String, String> map = new HashMap<>();
                            map.put("Authorization", "Basic " + Singleton.getInstance().getToken());
                            map.put("Pragma", "set;chat");
                            final RDT.Sender sender = RDT.getSender(Variables.Server.address, Variables.Server.port);
                            Protocol.Sender.sendMessage(sender, map, new Gson().toJson(chat));
                            map = new HashMap<>();
                            map.put("Authorization", "Basic " + Singleton.getInstance().getToken());
                            map.put("Pragma", "set;members");
                            Protocol.Sender.sendMessage(sender, map, new Gson().toJson(members));
                            RDT.getReceiver(sender).setOnReceiveListener(Variables.Server.address, (Protocol.Receiver) (address, port1, headers, message) -> {

                            });
                        } catch (SocketException | InterruptedException | UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }).start();
                    Platform.runLater(() -> ((Stage) NewChatController.this.root.getScene().getWindow()).close());

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle(null);
                alert.setHeaderText("Please insert a name to chat!");
                alert.setContentText(null);

                Button OK = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
                OK.setText("OK!");
                alert.show();
            }
        });

        listView.setCellFactory(param -> new ListCell<User>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty)
                    setText(item.getUsername());
            }
        });



    }
}
