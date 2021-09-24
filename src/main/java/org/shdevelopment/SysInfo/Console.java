package org.shdevelopment.SysInfo;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import static org.shdevelopment.Core.Tools.runTaskOnUIThread;

public class Console {

    @FXML
    private TextArea console;

    public void insertMessage(String message) {
        runTaskOnUIThread(() -> console.appendText(message + "\n"));
    }
}