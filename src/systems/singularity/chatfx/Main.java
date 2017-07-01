package systems.singularity.chatfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import systems.singularity.chatfx.db.Database;

import java.io.File;
import java.nio.file.Paths;
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

        String chatFX = defaultDirectory() + File.separator + "ChatFX" + File.separator;
        File file = new File(chatFX);
        if (!file.exists())
            file.mkdir();

        Database.createDatabase( chatFX + "ChatFX.db");

    }

    private static String defaultDirectory()
    {
        String OS = System.getProperty("os.name").toUpperCase();
        if (OS.contains("WIN"))
            return System.getenv("APPDATA");
        else if (OS.contains("MAC"))
            return Paths.get(System.getProperty("user.home"),"Library", "Application Support").toString();
        else
            return Paths.get(System.getProperty("user.home"), ".local").toString();
    }
}
