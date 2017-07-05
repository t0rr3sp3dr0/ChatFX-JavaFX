package systems.singularity.chatfx.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import systems.singularity.chatfx.util.Variables;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by phts on 05/07/17.
 */
public class DiscardModuleController implements Initializable {

    @FXML
    private Slider receiverLossProbabilitySlider;

    @FXML
    private TextField receiverLossProbabilityTextField;

    @FXML
    private TextField senderLossProbabilityTextField;

    @FXML
    private Label receiverLossCountLabel;

    @FXML
    private Label senderLossCountLabel;

    @FXML
    private Slider senderLossProbabilitySlider;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.senderLossProbabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Variables.senderLossProbability = newValue.doubleValue() / 100;
            DiscardModuleController.this.senderLossProbabilityTextField.setText(String.format("%.0f", newValue.doubleValue()));
        });
        this.senderLossProbabilityTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                this.senderLossProbabilitySlider.setValue(Double.parseDouble(DiscardModuleController.this.senderLossProbabilityTextField.getText()));
        });
        this.senderLossProbabilityTextField.setText(String.format("%.0f", Variables.senderLossProbability));

        this.receiverLossProbabilitySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Variables.receiverLossProbability = newValue.doubleValue() / 100;
            DiscardModuleController.this.receiverLossProbabilityTextField.setText(String.format("%.0f", newValue.doubleValue()));
        });
        this.receiverLossProbabilityTextField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER))
                this.receiverLossProbabilitySlider.setValue(Double.parseDouble(DiscardModuleController.this.receiverLossProbabilityTextField.getText()));
        });
        this.receiverLossProbabilityTextField.setText(String.format("%.0f", Variables.receiverLossProbability));

        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true) {
                DiscardModuleController.this.senderLossCountLabel.setText(String.format("%d", Variables.senderLossCount));
                DiscardModuleController.this.receiverLossCountLabel.setText(String.format("%d", Variables.receiverLossCount));

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
