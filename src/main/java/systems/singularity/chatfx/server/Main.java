package systems.singularity.chatfx.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import systems.singularity.chatfx.util.Constants;

import java.io.File;

/**
 * Created by caesa on 02/07/2017.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        File file = new File(Constants.PERSISTENT_DIRECTORY);
        if (!file.exists())
            file.mkdir();

        Parent root = FXMLLoader.load(getClass().getResource("/layouts/open_server.fxml"));
        primaryStage.setTitle("Open Server");
        primaryStage.setScene(new Scene(root, 285, 74));
        primaryStage.show();

        //testes para o server
//        new UserRepository().insert(new User(1, "edjan", "12345678", "192.168.43.43", 2020, 2021, 2022, true));
//        new UserRepository().insert(new User(2, "pepeu", "12345678", "192.168.43.78", 2020, 2021, 2022, true));
        //boolean exists = new UserRepository().exists(user);
        //user.setPassword("abcd1234");
        //new UserRepository().update(user);

    }


}
