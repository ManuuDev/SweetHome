package org.shdevelopment.Server;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.shdevelopment.ContactManagement.ContactBookInterface;
import org.shdevelopment.Controllers.FileReceptionController;
import org.shdevelopment.Core.Tools;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.Structures.FileInfo;
import org.shdevelopment.Structures.Message;
import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.shdevelopment.Constant.Network.*;
import static org.shdevelopment.Core.Tools.*;

class FileReceiver extends ServerComponent {

    /* TODO Multithreading para recepción. Generar una carpeta por usuario para recibir archivos */

    private final File directory = createMainDirectory();
    private final String separator = System.getProperty("file.separator");

    public FileReceiver(int threadID, String componentName, ContactBookInterface contactBook, ComponentManager componentManager) {
        super(threadID, componentName, contactBook, componentManager);
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public void run() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ServerSocket requestServerSocket;

        try {

            requestServerSocket = new ServerSocket(FILE_REQUEST_PORT);
            ServerSocket fileServerSocket = new ServerSocket(FILE_PORT);
            fileServerSocket.setReceiveBufferSize(FILE_BUFFER);

            notifyToComponentManager();

            while (!Thread.currentThread().isInterrupted()) {

                Socket socket;
                socket = requestServerSocket.accept();

                if (contactBook.existContact(socket)) {

                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();

                    ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
                    List<FileInfo> metadataList = (List<FileInfo>) objectInputStream.readObject();

                    boolean answer = generateMetadataViewer(metadataList, contactBook.getContactName(socket));
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
            Log.addMessage(ex.getMessage(), Level.CRITIC);
            componentManager.reportException(ComponentManager.threadType.RDA);
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
                    metadataViewLoader.setLocation(ContactServer.class.getResource("/fxml/tableOfMetadata.fxml"));
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
                    Log.addMessage("No se pudo recibir la informacion de los archivos", Level.INFO);
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
            Log.addMessage(ex.getMessage(), Level.ERROR);
        }
    }

    private void notifyUserInChat(Socket socket, String fileName) {
        Contact contact = contactBook.searchContact(socket);
        String text = ("\n" + "|| Se ha recibido: " + fileName + " ||" + "\n");
        Message message = new Message("Sistema", contact.getIp(), Tools.getSystemTime(), text);
        addMessageWithUIThread(message, contact);
    }

    private static FileInfo receiveFileData(ObjectInputStream input) {
        try {
            return (FileInfo) input.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
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
