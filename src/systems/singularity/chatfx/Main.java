package systems.singularity.chatfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import systems.singularity.chatfx.db.Database;

import java.io.File;
import java.sql.Connection;

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

        String documents = System.getProperty("user.home") + File.separator + "Documents" + File.separator;
        File file = new File(documents + "ChatFX");
        if (!file.exists())
            file.mkdir();

        String chatFX = file.getPath() + File.separator;
        Database.createDatabase( chatFX + "ChatFX.db");


    }
}
