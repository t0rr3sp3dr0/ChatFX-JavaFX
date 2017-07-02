package systems.singularity.chatfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import systems.singularity.chatfx.db.Database;
import systems.singularity.chatfx.db.UserRepository;
import systems.singularity.chatfx.structs.User;
import systems.singularity.chatfx.util.Constants;
import systems.singularity.chatfx.util.java.Utilities;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Initialize server
//        Parent root = FXMLLoader.load(getClass().getResource("/layouts/open_server.fxml"));
//        primaryStage.setTitle("Open Server");
//        primaryStage.setScene(new Scene(root, 285, 109));
//        primaryStage.show();


        //Initialize client
        Parent root = FXMLLoader.load(getClass().getResource("/layouts/login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 285, 109));
        primaryStage.show();


        File file = new File(Constants.chatFX);
        if (!file.exists())
            file.mkdir();



        //Server Database
        Database.createDatabase( Constants.chatFX + "ChatFX.db");
        Database.createTable(Constants.chatFX + "ChatFX.db");
        User user = new User(1, "edjan", "12345678", (short)2020, (short)2021, (short)2022, true);
        new UserRepository().insert(new User(2, "pepeu", "12345678", (short)2020, (short)2021, (short)2022, true));
        //boolean exists = new UserRepository().exists(user);
        //user.setPassword("abcd1234");
        //new UserRepository().update(user);
        //ArrayList<User> users = (ArrayList<User>) new UserRepository().getAll();
        //new UserRepository().remove(users.get(1));


        //Client Database


    }


}
