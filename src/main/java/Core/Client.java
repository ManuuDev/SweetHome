package Core;

import Constant.SysInfo;
import Crypto.Crypto;
import Structures.*;
import SysInfo.Log;
import SysInfo.Nivel;
import javafx.application.Platform;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static Constant.Network.*;
import static Core.Tools.addMessageWithUIThread;
import static Core.Tools.fileSize;

public class Client {

    public static Contact contactHandshake(String targetIp, int timeout) {

        SocketAddress remoteDeviceAddress = new InetSocketAddress(targetIp, SERVER_PORT);
        Socket socket = new Socket();

        try {
            socket.connect(remoteDeviceAddress, timeout);

            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(SysInfo.LOCAL_CONTACT);

            ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
            ContactData contactData = (ContactData) inputStream.readObject();

            SecretKey secretKey = Crypto.generateSymmetricKey();

            byte[] contactKey = Crypto.encryptWithRSA(contactData.getPublicKey(), secretKey);
            outputStream.writeObject(contactKey);

            return new Contact(contactData, secretKey);
        } catch (IOException | ClassNotFoundException ex) {
            //Timeout's
        }

        try {
            socket.close();
        } catch (IOException ex) {
            Log.addMessage(ex.getMessage(), Nivel.ERROR);
        }

        return null;
    }

    public static Message sendMessage(Contact contact, String message) {

        try {

            SocketAddress address = new InetSocketAddress(contact.getIp(), MESSAGES_PORT);
            MessagePackage messagePackage;

            try (Socket socket = new Socket()) {

                socket.connect(address, 3000);
                messagePackage = new MessagePackage();

                messagePackage.setMessage(Crypto.encryptMessage(message, contact));
                messagePackage.setDate(Tools.getSystemTime());

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(messagePackage);
            }

            String finalText = SyntacticAnalyzer.executeAnalyzers(message);

            return new Message(SysInfo.LOCAL_CONTACT.getName(), contact.getIp(), messagePackage.getDate(), finalText, true);
        } catch (IOException ex) {
            return new Message("Sistema", "local", Tools.getSystemTime(), "! Error al enviar el mensaje ! ");
        }
    }

    public static void fileHandler(String ip, List<File> files) {

        try {

            Socket socket = new Socket(ip, FILE_REQUEST_PORT);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            List<FileInfo> metadataList = generateMetadata(files);

            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(metadataList);

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            boolean answer = (boolean) objectInputStream.readObject();

            if (answer) {

                long totalSize = Tools.totalSizeInBytes(files); //TODO Barra de progreso

                Socket s = new Socket(ip, FILE_PORT);

                BufferedOutputStream bos = new BufferedOutputStream(s.getOutputStream());

                ObjectOutputStream oos = new ObjectOutputStream(bos);

                files.stream().forEach(file -> {

                    String title = String.format("Archivo %s de %s", files.indexOf(file) + 1, files.size());

                    boolean sent = sendFile(bos, oos, file);

                    notifyLocalUser(ip, file, sent);
                });

                bos.close();
                oos.close();
                s.close();

            } else {//TODO Informar en chat
                Log.addMessage("El usuario rechazo la solicutid.", Nivel.INFO);
            }

        } catch (IOException ex) {
            Log.addMessage(ex.getMessage(), Nivel.ERROR);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void notifyLocalUser(String ip, File file, boolean sent) {
        String text;
        if (sent)
            text = "|| El archivo " + file.getName() + " se envio correctamente ||";
        else
            text = "|| Error al enviar " + file.getName() + " ||";

        Message message = new Message("Sistema", ip, Tools.getSystemTime(), text);
        addMessageWithUIThread(message, ip);
    }

    private static List<FileInfo> generateMetadata(List<File> files) {

        List<FileInfo> metadataList = new ArrayList<>();
        files.forEach((file) -> {
            metadataList.add(new FileInfo(file.getName(), fileSize(file, Unit.MB)));
        });

        return metadataList;
    }

    public static boolean sendFile(BufferedOutputStream bos, ObjectOutputStream oos, File file) {

        try {

            sendMetadata(oos, file);

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));

            byte[] buffer = new byte[FILE_BUFFER];

            int read;

            while ((read = bis.read(buffer, 0, (int) Math.min(buffer.length, file.length()))) != -1) {
                bos.write(buffer, 0, read);
                bos.flush();
            }

            return true;

        } catch (IOException ex) {
            System.out.println("Error al enviar archivos. " + ex.getMessage());
            return false;
        }
    }

    private static void sendMetadata(ObjectOutputStream output, File file) {

        FileInfo fileData = new FileInfo(file.getName(), file.length());

        try {
            output.writeObject(fileData);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

