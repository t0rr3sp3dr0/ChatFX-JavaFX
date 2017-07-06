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
        //noinspection StatementWithEmptyBody
        if (!file.exists() && file.mkdir()) ;

        Parent root = FXMLLoader.load(getClass().getResource("/layouts/open_server.fxml"));
        primaryStage.setTitle("Open Server");
        primaryStage.setScene(new Scene(root, 264, 74));
        primaryStage.show();
    }
}
