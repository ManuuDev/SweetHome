package SysInfo;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class Console {

    @FXML
    private TextArea console;

    public void insertMessage(String message) {
        console.appendText(message + "\n");
    }

    private void insertMessageToLogFile(String message) {
        //TODO Logs con rotacion de archivos, mantener 7
    }
}