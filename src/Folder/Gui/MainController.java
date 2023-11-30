package Folder.Gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import Folder.Bll.DataProcessor;

public class MainController {
    @FXML
    private Label messagelbl;
    @FXML
    private Button greetingsbtn;
    @FXML
    private TextField nametxt;
    @FXML
    private TextField agetxt;
    private final DataProcessor dataProcessor = new DataProcessor();


    public void displayGreetingsMessage(ActionEvent actionEvent) {
        messagelbl.setText(dataProcessor.personalizedMessage(agetxt.getText(), nametxt.getText()));

    }
}
