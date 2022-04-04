package org.shdevelopment.Core;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.shdevelopment.Constant.Network;
import org.shdevelopment.Constant.SysInfo;
import org.shdevelopment.ContactManagement.StatefulContactBook;
import org.shdevelopment.ContactManagement.StatelessContactBook;
import org.shdevelopment.Controllers.Controller;
import org.shdevelopment.Crypto.Crypto;
import org.shdevelopment.Database.Driver;
import org.shdevelopment.Server.ComponentManager;
import org.shdevelopment.Structures.CustomException;
import org.shdevelopment.SysInfo.Console;

import java.io.IOException;
import java.net.SocketPermission;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class Main extends Application {

    //General
    //TODO Script para compilar en una plataforma
    //TODO Poder configurar stateless o stateful db

    //UI
    //TODO Mostrar scrollbar siempre en ul
    //TODO Estructura p/usuario para frontend

    public static FXMLLoader mainLoader;
    public static Console console;

    private static Stage mainStage;
    private Parent mainRoot;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        mainStage = stage;

        startDatabase();
        createMainInterface();
        createConsoleInterface();
        setAppIcon(stage);
        requestPorts();
        setInitialConfig();
        finishViewConfig(stage);

        ComponentManager componentManager = new ComponentManager(StatefulContactBook.getInstance());
        componentManager.initThreads();
    }

    private void startDatabase() {
        try {
            Connection connection = Driver.getConnection();
            Statement statement = connection.createStatement();

            final String tables = """
                    CREATE TABLE IF NOT EXISTS bookmark
                    (ip varchar(30) PRIMARY KEY, name varchar(30));
                    
                    CREATE TABLE IF NOT EXISTS messages
                    (msg_id int PRIMARY KEY AUTO_INCREMENT, 
                     senderName varchar(30),
                     date varchar(30),
                     msg_text text,
                     is_local bit,
                     sender_ip varchar(30));
                    """;

            statement.execute(tables);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Controller getMainController() {
        return mainLoader.getController();
    }

    public static void minimizeApp() {
        mainStage.setIconified(true);
    }

    private void createMainInterface() throws IOException {
        mainLoader = new FXMLLoader(Main.class.getResource("/fxml/main.fxml"));
        mainRoot = mainLoader.load();

        Controller controller = mainLoader.getController();
        controller.initConfig();
    }

    private void createConsoleInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/console.fxml"));
            Parent root = loader.load();
            console = loader.getController();
            getMainController().setConsoleRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setInitialConfig() {

        Crypto.init();

        try {
            SysInfo.generateInformation();
        } catch (CustomException.NoIPV4 ex) {
            Tools.exitWithError(ex);
        }
    }

    private void finishViewConfig(Stage stage) {
        Scene scene = new Scene(mainRoot, 960, 640);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("SweetHome");
        getMainController().setMainSceneAndStage(stage, scene);

        stage.show();
    }

    private void setAppIcon(Stage stage) {
        Image icon = new Image(String.valueOf(Main.class.getResource("/icons/main-icon.png")));
        stage.getIcons().add(icon);
    }

    private void requestPorts() {

        ArrayList<Integer> portsList = new ArrayList<>();

        portsList.add(Network.SERVER_PORT);
        portsList.add(Network.ECO_PORT);
        portsList.add(Network.MESSAGES_PORT);
        portsList.add(Network.FILE_REQUEST_PORT);
        portsList.add(Network.FILE_PORT);

        portsList.forEach(puerto -> new SocketPermission("127.0.0.1:" + puerto, "accept,connect,listen"));
    }
}
