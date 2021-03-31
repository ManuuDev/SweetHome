package Controllers;

import Core.Client;
import Core.Contacts;
import Core.Main;
import Structures.Contact;
import Structures.Message;
import SysInfo.Level;
import SysInfo.Log;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.json.bind.JsonbBuilder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static Core.Tools.getDownloadsFolder;

public class Controller {

    private final ObservableList<Contact> CONTACT_LIST = FXCollections.observableArrayList();
    private WebEngine webEngine;
    private Stage mainStage;
    private Scene mainScene;
    private Stage consoleStage;

    @FXML
    private ListView contactList;
    @FXML
    private WebView webView;
    @FXML
    private TextField input;
    @FXML
    private ImageView dragAndDropImage;
    @FXML
    private Pane mainPane;

    @SuppressWarnings({"unchecked"})
    public void initConfig(){
        webView.setContextMenuEnabled(false);
        webEngine = webView.getEngine();
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader().getResource("interface/chat.html")).toString());

        contactList.setCellFactory(param -> new ListCell<Contact>(){
            @Override
            protected void updateItem(Contact item, boolean empty) {
                super.updateItem(item, empty);
                if(item != null)
                    setText(item.getName());
                else
                    setText(null);
            }
        });

        contactList.setItems(CONTACT_LIST);

        contactList.setOnMouseClicked(mouseEvent -> loadChatHistory());

        configureDragAndDrop();
    }

    private void configureDragAndDrop(){
        dragAndDropImage.setVisible(false);

        mainPane.setOnDragEntered(event -> {
            dragAndDropImage.setVisible(true);
            event.consume();
        });

        dragAndDropImage.setOnDragOver(event-> {
            if (event.getGestureSource() != dragAndDropImage && event.getDragboard().hasFiles())
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);

            event.consume();
        });

        dragAndDropImage.setOnDragDropped(event -> {
            Dragboard dragboard = event.getDragboard();
            var success = false;

            if (dragboard.hasFiles()) {
                success = true;
                List<File> fileList = dragboard.getFiles();
                Runnable runnable = () -> Client.fileHandler(getContactSelected().getIp(), fileList);
                executeTask(runnable);
            }

            dragAndDropImage.setVisible(false);
            event.setDropCompleted(success);
            event.consume();
        });

        dragAndDropImage.setOnDragExited(event -> { dragAndDropImage.setVisible(false); event.consume(); });
    }

    private void finishConfig(){
        Contact contactTest01 = new Contact("0.0.0.0", "Contacto de prueba 1", null);
        contactTest01.addMessageToHistory(new Message("","","00:00:00","Mensaje contacto 1"));
        Contact contactTest02 = new Contact("0.0.0.1", "Contacto de prueba 2", null);
        contactTest02.addMessageToHistory(new Message("","","00:00:00","Mensaje contacto 2"));
        CONTACT_LIST.add(contactTest01);
        CONTACT_LIST.add(contactTest02);
    }

    public void setConsoleRoot(Parent root){
        consoleStage = new Stage();
        consoleStage.setScene(new Scene(root));
        consoleStage.setAlwaysOnTop(true);
        consoleStage.setTitle("Consola");
    }

    public void sendMessage(KeyEvent keyEvent){
        if (keyEvent.getCode() == KeyCode.ENTER && !input.getText().isBlank()) {
            Message localMessage = Client.sendMessage(getContactSelected(), input.getText());
            addMessage(localMessage, getContactSelected());
            input.setText("");
        }
    }

    public void addMessage(Message message, Contact contact) {

        contact.addMessageToHistory(message);

        if(isSelectedContact(contact)) {
            webEngine.executeScript("addMessage(" + objectToJSonString(message) + ")");
        }
    }

    private void loadChatHistory(){

        Contact contact = getContactSelected();
        if(contact == null)
            return;

        var messageHistory = contact.getChatHistory();

        if(messageHistory == null || messageHistory.isEmpty()) {
            webEngine.executeScript("clearChatHistory()");
            return;
        }

        //TODO Almacenar los mensajes directamente como JSonArray
        final StringBuilder jsonArray = new StringBuilder("[");
        messageHistory.forEach(message -> jsonArray.append(objectToJSonString(message)).append(","));
        jsonArray.append("]");
        webEngine.executeScript("loadMessageHistory(" + jsonArray + ")");
    }

    private String objectToJSonString(Object object) {
        return JsonbBuilder.create().toJson(object);
    }

    public void setMainSceneAndStage(Stage stage, Scene scene) {
        this.mainStage = stage;
        this.mainScene = scene;
        finishConfig();
    }

    private Contact getContactSelected(){
        return (Contact) contactList.getSelectionModel().getSelectedItem();
    }

    private boolean isSelectedContact(Contact contact) {
        Contact selected = getContactSelected();

        if(selected == null)
            return false;
        else
            return contact.getIp().equals(selected.getIp());
    }

    public void updateContactList(ArrayList<Contact> newContactList) {
        newContactList.stream().filter(contact -> !CONTACT_LIST.contains(contact)).forEach(CONTACT_LIST::add);
    }

    public void openDownloadsFolder() {
        try {
            Desktop.getDesktop().open(getDownloadsFolder());
            Main.minimizeApp();
        } catch (IOException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
        }
    }

    public void openConsole() { consoleStage.show(); }

    //TODO Si hay recepción de archivos entrantes generar mensaje con avisando que si se cierra el programa se interrumpira la transferencia
    public void exitProgram() { System.exit(0); }

    public void sendFiles() {
        if(getContactSelected() == null)
            return;

        List<File> fileList = generateFileChooser();

        if (fileList != null) {
            Runnable runnable = () -> Client.fileHandler(getContactSelected().getIp(), fileList);
            executeTask(runnable);
        }
    }

    public void sendFilesToAllContacts() {

        List<File> fileList = generateFileChooser();

        if (fileList != null) {
            Runnable runnable = () -> Contacts.CONTACT_LIST.forEach((contact) -> Client.fileHandler(contact.getIp(), fileList));
            executeTask(runnable);
        }
    }

    //TODO PollThread
    private void executeTask(Runnable runnable){
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                runnable.run();
                return null;
            }
        };

        new Thread(task).start();
    }

    private List<File> generateFileChooser() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona uno o varios archivos para enviar");
        return fileChooser.showOpenMultipleDialog(mainStage);
    }
}
