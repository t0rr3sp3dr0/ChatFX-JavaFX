package systems.singularity.chatfx.util.javafx;

import com.sun.istack.internal.NotNull;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.Closeable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

import static systems.singularity.chatfx.util.java.Utilities.closeCloseables;

/**
 * Created by pedro on 5/12/17.
 */
public final class Utilities {
    private Utilities() {
        // Avoid class instantiation
    }

    public static EventHandler<WindowEvent> closeCloseablesOnAbortRequest(@NotNull Closeable... closeables) {
        return event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(null);
            alert.setHeaderText("Are you sure you want to abort?");
            alert.setContentText(null);

            Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            exitButton.setText("Abort");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK)
                closeCloseables(closeables);
            else
                event.consume();
        };
    }

    public static void alertException(Exception e, boolean wait) {
//        if (Main.isWaitOnExcept())
//            e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Exception");
        alert.setHeaderText(e.getMessage());
        alert.setContentText(e.toString().split(":")[0]);

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);

        if (wait)
            alert.showAndWait();
        else
            alert.show();
    }

    public static void setOnCloseRequest(Stage stage) {
        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(null);
            alert.setHeaderText("Are you sure you want to exit?");
            alert.setContentText(null);

            Button exitButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
            exitButton.setText("Exit");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK)
                System.exit(0);
            else
                event.consume();
        });
    }
}
