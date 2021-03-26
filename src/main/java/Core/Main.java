package Core;

import Constant.Network;
import Constant.SysInfo;
import Controllers.Controller;
import Crypto.Crypto;
import SysInfo.Console;
import SysInfo.Log;
import SysInfo.Nivel;
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
    //TODO Descargar jQuery y Bootstrap en local, crear archivo para script y css del chat.
    //TODO 1) Eliminar chequeo ipv4 null.
    //TODO 2) Si la ipv4 no se puede obtener, se debe enviar un mensaje de informacion y no iniciar el programa.
    //TODO 3) Barra de progreso para el envio de archivos.

    //TODO Branch 1) Cada contacto debe tener un html asociado que funcionara como almacenamiento.

    //TODO Exception handler con custom exceptions
    //TODO Mostrar scrollbar siempre en ul

    /*TODO
       Si ocurre un error, por ejemplo puerto en uso,
       el objeto threadmanager corre en el hilo principal y bloquea el programa.
       Pensar una buena solucion ya que sin esos servicios el programa no serviria por lo tanto hay que
       informar el error con lenguaje natural y dar la opcion re relanzar el programa.
    */
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

        SysInfo.generateInformation();

        if (SysInfo.getIPV4() == null) {
            String message = "No se ha podido iniciar la aplicacion ya que no pudimos acceder a una red, porfavor compruebe su conexion de red local o antivirus";
            //TODO Arreglar para que, si hay un error al obtener la ip, se logee esa excepcion generada
            Log.addMessage(message, Nivel.INFO);
            createDialog(message);
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
