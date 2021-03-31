package Core;

import Constant.Network;
import Constant.SysInfo;
import Controllers.Controller;
import Crypto.Crypto;
import Structures.CustomException;
import SysInfo.Console;
import SysInfo.Level;
import SysInfo.Log;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.SocketPermission;
import java.util.ArrayList;

import static Core.Tools.createDialog;

public class Main extends Application {

    /*
      TODO
       Si ocurre un error, por ejemplo puerto en uso,
       el objeto threadmanager corre en el hilo principal y bloquea el programa.
       Pensar una buena solucion ya que sin esos servicios el programa no serviria por lo tanto hay que
       informar el error con lenguaje natural y dar la opcion re relanzar el programa.
    */

    //TODO Shared folders

    //TODO Un WebView por contacto, para permitir barras de progreso p/archivo y p/usuario embebidas.
    //TODO Barra de progreso para el envio de archivos.

    //TODO Descargar jQuery y Bootstrap en local, crear archivo para script y css del chat.
    //TODO Mostrar scrollbar siempre en ul


    public static FXMLLoader mainLoader;
    public static Console console;

    private static Stage mainStage;
    private Parent mainRoot;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        mainStage = stage;

        createMainInterface();
        createConsoleInterface();
        setAppIcon(stage);
        requestPorts();
        setInitialConfig();
        finishViewConfig(stage);

        ThreadManager.threadManager.initThreads();
    }

    public static Controller getMainController(){
        return mainLoader.getController();
    }

    public static void minimizeApp(){ mainStage.setIconified(true); }

    private void createMainInterface() throws IOException {
        mainLoader = new FXMLLoader();
        mainLoader.setLocation(getClass().getClassLoader().getResource("interface/main.fxml"));
        mainRoot = mainLoader.load();

        Controller controller = mainLoader.getController();
        controller.initConfig();
    }

    private void createConsoleInterface() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("interface/console.fxml"));
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
            Log.addMessage(ex.getMessage(), Level.INFO);
            createDialog(ex.getUserInfoMessage());
            System.exit(0);
        }
    }

    private void finishViewConfig(Stage stage){
        Scene scene = new Scene(mainRoot, 960, 640);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("SweetHome");
        getMainController().setMainSceneAndStage(stage, scene);

        stage.show();
    }

    private void setAppIcon(Stage stage) {
        Image icon = new Image(String.valueOf(getClass().getClassLoader().getResource("icons/main-icon.png")));
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
