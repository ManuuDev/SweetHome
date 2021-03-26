package Core;

import Constant.SysInfo;
import Controllers.FileReceptionController;
import Core.ThreadManager.threadType;
import Crypto.Crypto;
import Structures.*;
import SysInfo.Log;
import SysInfo.Nivel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.*;

import static Constant.Network.*;
import static Core.Contacts.*;
import static Core.Tools.*;

//TODO Clase abstracta ServerModule y objetos con singleton para c/modulo
//TODO DeviceFinder usa codigo de la clase cliente, decidir donde poner todo junto
public class Server extends Thread {

    @Override
    public void run() {

        ServerSocket serverSocket = null;

        try {

            serverSocket = new ServerSocket(SERVER_PORT);

            Log.addMessage("[Receptor iniciado]", Nivel.INFO);

            synchronized (ThreadManager.threadManager) {
                ThreadManager.threadManager.notify();
            }

            while (!Thread.currentThread().isInterrupted()) {

                Socket socket = serverSocket.accept();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ContactData contactData = (ContactData) inputStream.readObject();

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(SysInfo.LOCAL_CONTACT);

                byte[] contactAESKey = (byte[]) inputStream.readObject();

                addNewContact(new Contact(contactData, Crypto.decryptSymmetricKey(contactAESKey)));
            }

        } catch (IOException | ClassNotFoundException ex) {

            Log.addMessage("Error en hilo servidor: " + ex.getMessage(), Nivel.ERROR);

            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException ex1) {
                Log.addMessage("No se pudo cerrar el socket servidor: " + ex1.getMessage(), Nivel.ERROR);
            }

            ThreadManager.reportException(threadType.SERVER);
        }

    }
}

class DeviceFinder extends Thread {

    @Override
    public void run() {

        List<String> ipList = Tools.getAllPossibleIPs();

        Log.addMessage("[Buscador de dispositivos iniciado]", Nivel.INFO);

        synchronized (ThreadManager.threadManager) {
            ThreadManager.threadManager.notify();
        }

        ExecutorService threadpool = Executors.newFixedThreadPool(5);

        ipList.forEach(ip -> {
            Runnable tryConnection = () -> Contacts.addNewContact(Client.contactHandshake(ip, 1000));
            threadpool.submit(tryConnection);
        });

        try {
            threadpool.shutdown();
            threadpool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Log.addMessage(ex.getMessage(), Nivel.ERROR);
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class MessageReceiver extends Thread {

    @Override
    public void run() {

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        try {

            ServerSocket socketServer = new ServerSocket(MESSAGES_PORT);
            Log.addMessage("[Receptor de mensajes iniciado]", Nivel.INFO);

            synchronized (ThreadManager.threadManager) {
                ThreadManager.threadManager.notify();
            }

            while (!Thread.currentThread().isInterrupted()) {

                Socket socket = socketServer.accept();

                if (isContact(socket)) {

                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    MessagePackage packet = (MessagePackage) objectInputStream.readObject();

                    if (packet.getMessage() != null) {

                        Contact contact = Contacts.findContact(socket);

                        String finalMessage = unpackMessage(packet, contact);

                        Message newMessage = new Message(contact.getName(),contact.getIp(), packet.getDate(),finalMessage);

                        addMessageWithUIThread(newMessage, contact);

                    } else {
                        Log.addMessage("Error: El paquete recibido como mensaje es erroneo o esta dañado.", Nivel.ERROR);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Log.addMessage(ex.getMessage(), Nivel.ERROR);
        }
    }

    public String unpackMessage(MessagePackage packet, Contact contact) {
        String message = Crypto.decryptMessage(packet.getMessage(), contact.getAes());

        return SyntacticAnalyzer.executeAnalyzers(message);
    }

}

class FileReceiver extends Thread {

    /* TODO Multithreading para recepción. Generar una carpeta por usuario para recibir archivos */

    private final File directory = createMainDirectory();
    private final String separator = System.getProperty("file.separator");

    @Override
    @SuppressWarnings({"unchecked"})
    public void run() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ServerSocket requestServerSocket;

        try {

            requestServerSocket = new ServerSocket(FILE_REQUEST_PORT);
            ServerSocket fileServerSocket = new ServerSocket(FILE_PORT);
            fileServerSocket.setReceiveBufferSize(FILE_BUFFER);

            Log.addMessage("[Receptor de archivos iniciado]", Nivel.INFO);

            synchronized (ThreadManager.threadManager) {
                ThreadManager.threadManager.notify();
            }

            while (!Thread.currentThread().isInterrupted()) {

                Socket socket;
                socket = requestServerSocket.accept();

                if (isContact(socket)) {

                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    List<FileInfo> metadataList = (List<FileInfo>) objectInputStream.readObject();

                    boolean answer = generateMetadataViewer(metadataList, getContactName(socket));
                    //TODO!!! Finalizar metodo aca, luego en la UI si acepta crear una Task que llame a una nueva funcion usando el output stream, submit Task en threadpool
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
                    objectOutputStream.writeObject(answer);

                    if (answer) {
                        receiveFiles(fileServerSocket, metadataList.size());

                        inputStream.close();
                        outputStream.close();
                        socket.close();
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Log.addMessage(ex.getMessage(), Nivel.CRITICO);
            ThreadManager.reportException(threadType.RDA);
        } catch (InterruptedException | ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    public boolean generateMetadataViewer(List<FileInfo> metadataList, String contactName) throws ExecutionException, InterruptedException {
        final FutureTask<Boolean> query = new FutureTask<>(new Callable<>() {
            @Override
            @SuppressWarnings({"unchecked"})
            public Boolean call() {
                try {
                    FXMLLoader metadataViewLoader = new FXMLLoader();
                    metadataViewLoader.setLocation(getClass().getClassLoader().getResource("interface/tableOfMetadata.fxml"));
                    Parent parent = metadataViewLoader.load();
                    Stage stage = new Stage();
                    stage.setScene(new Scene(parent));
                    stage.setTitle("Solicitud recibida de " + contactName);
                    stage.show();

                    FileReceptionController controller = metadataViewLoader.getController();
                    TableView tableView = controller.getTableView();
                    ObservableList<FileInfo> observableList = FXCollections.observableList(metadataList);
                    tableView.setItems(observableList);

                    boolean answer = controller.getAnswer();
                    stage.close();
                    return answer;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        runTaskInUIThread(query);
        return query.get();
    }

    private void receiveFiles(ServerSocket serverSocket, int fileCounter) {
        try {
            Socket socket = serverSocket.accept();

            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            ObjectInputStream ois = new ObjectInputStream(bis);

            BufferedOutputStream bos;

            while (fileCounter > 0) {

                fileCounter--;
                FileInfo fileInfo = receiveFileData(ois);

                if (fileInfo == null) {
                    Log.addMessage("No se pudo recibir la informacion de los archivos", Nivel.INFO);
                    return;
                }

                long fileSize = (long) fileInfo.getSize();

                byte[] buffer = new byte[FILE_BUFFER];
                socket.setReceiveBufferSize(buffer.length);

                String fileDirectory = String.format("%s%s%s", directory, separator, fileInfo.getName());

                bos = new BufferedOutputStream(new FileOutputStream(fileDirectory));

                int read;

                while (fileSize > 0 && (read = bis.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                    bos.write(buffer, 0, read);
                    fileSize -= read;
                    bos.flush();
                }

                bos.close();
                notifyUserInChat(socket, fileInfo.getName());
            }

            bis.close();
            ois.close();
            socket.close();
        } catch (IOException ex) {
            Log.addMessage(ex.getMessage(), Nivel.ERROR);
        }
    }

    private void notifyUserInChat(Socket socket, String fileName) {
        Contact contact = Contacts.findContact(socket);
        String text = ("\n" + "|| Se ha recibido: " + fileName + " ||" + "\n");
        Message message = new Message("Sistema", contact.getIp(), Tools.getSystemTime(), text);
        addMessageWithUIThread(message, contact);
    }

    private static FileInfo receiveFileData(ObjectInputStream input) {
        try {
            return (FileInfo) input.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Log.addMessage(ex.getMessage(), Nivel.ERROR);
            return null;
        }
    }

    private File createMainDirectory() {
        File mainDirectory = getDownloadsFolder();

        if (!mainDirectory.exists())
            mainDirectory.mkdir();

        mainDirectory.setWritable(true, false);

        return mainDirectory;
    }
}
