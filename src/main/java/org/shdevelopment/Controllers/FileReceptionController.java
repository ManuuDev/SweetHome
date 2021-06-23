package org.shdevelopment.Controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.shdevelopment.Structures.FileInfo;

public class FileReceptionController {
    @FXML
    TableView metadataTable;

    private final Object loopKey = new Object();

    @FXML
    @SuppressWarnings({"unchecked"})
    protected void initialize(){
        TableColumn<FileInfo,String> firstCol = new TableColumn<>("Nombre del archivo");
        firstCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        firstCol.setPrefWidth(390);
        TableColumn<FileInfo,String> secondCol = new TableColumn<>("Tamaño");
        secondCol.setCellValueFactory(new PropertyValueFactory<>("size"));
        secondCol.setPrefWidth(200);

        metadataTable.getColumns().setAll(firstCol, secondCol);
    }

    public TableView getTableView(){
        return metadataTable;
    }

    public boolean getAnswer() {
        return (boolean)  Platform.enterNestedEventLoop(loopKey);
    }

    public void accept() {
        Platform.exitNestedEventLoop(loopKey, true);
    }

    public void decline() {
        Platform.exitNestedEventLoop(loopKey, false);
    }

}
