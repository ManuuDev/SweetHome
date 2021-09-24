package org.shdevelopment.Core;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.shdevelopment.Constant.SysInfo;
import org.shdevelopment.ContactManagement.StatelessContactBook;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.Structures.CustomException;
import org.shdevelopment.Structures.Message;
import org.shdevelopment.Structures.Unit;
import java.util.logging.Level;
import org.shdevelopment.SysInfo.Log;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Tools {

    public static String getSystemTime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(date);
    }

    public static String getDeviceName() {

        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException ex) {
            System.out.println("Error al obtener nombre del dispositivo: " + ex.getMessage());
            System.out.println("Se va a usar el nombre del usuario actual en el sistema como nombre del dispositivo.");
            return System.getProperty("user.name");
        }
    }

    public static String getSocketIp(Socket socket) {
        String ip = socket.getInetAddress().toString();
        return ip.substring(1);
    }

    public static String getSystemIPV4() throws CustomException.NoIPV4 {

        try {

            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {

                NetworkInterface networkInterface = networkInterfaces.nextElement();

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {

                    InetAddress inetAddress = addresses.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }

            throw new CustomException.NoIPV4();

        } catch (SocketException | CustomException.NoIPV4 ex) {
            throw new CustomException.NoIPV4();
        }
    }

    public static List<String> getIPsFromLANDevices() {
        List<String> ipList = new ArrayList<>();
        String ipv4 = SysInfo.getIPV4();

        final String ipIterator = ipv4.substring(0, ipv4.lastIndexOf(".") + 1);
        int[] numbers = IntStream.range(1, 255).toArray();
        Arrays.stream(numbers).forEach(number -> ipList.add(String.format("%s%d", ipIterator, number)));

        return ipList.stream().filter(x -> !x.equals(ipv4)).collect(Collectors.toList());
    }

    public static void runTaskOnUIThread(Runnable runnable) {
        Platform.runLater(runnable);
    }

    public static void addMessageWithUIThread(Message message, String ip) {
        Platform.runLater(() -> Main.getMainController().addMessage(message, StatelessContactBook.getInstance().searchContact(ip)));
    }

    public static void addMessageWithUIThread(Message message, Contact contact) {
        Platform.runLater(() -> Main.getMainController().addMessage(message, contact));
    }

    public static long totalSizeInBytes(List<File> files) {
        return files.stream().mapToLong(File::length).sum();
    }

    public static double fileSize(File file, Unit unit) {

        double bytes = file.length();

        switch (unit) {
            case KB:
                return (bytes / 1024);
            case MB:
                return ((bytes / 1024) / 1024);
            case GB:
                return (((bytes / 1024) / 1024) / 1024);
            default:
                return bytes;
        }
    }

    public static File getDownloadsFolder() {
        return new File(System.getProperty("user.home"), "Archivos de SweetHome");
    }

    public static File createFile(String fileName) {
        String separator = System.getProperty("file.separator");
        File directory = getDownloadsFolder();

        if (!directory.exists())
            directory.mkdir();

        File file = new File(directory.getAbsolutePath() + separator + fileName);

        try {
            if (!file.exists())
                file.createNewFile();
        } catch (IOException ex) {
            Log.addMessage(ex.getMessage(), Level.WARNING);
        }

        return file;
    }

    public static void createDialog(String message) {
        createDialog(message, Alert.AlertType.INFORMATION);
    }

    public static void createDialog(String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle("Informacion de la aplicacion SweetHome");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void exitWithError(CustomException ex) {
        Log.addMessage(ex.getMessage(), Level.INFO);
        createDialog(ex.getUserInfoMessage());
        System.exit(0);
    }

    public static void exitWithError(Exception ex) {
        Log.addMessage(ex.getMessage(), Level.INFO);
        createDialog("Ocurrio un error inesperado en el sistema, verifique los logs.");
        System.exit(0);
    }
}
