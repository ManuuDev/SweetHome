package Core;

import Constant.SysInfo;
import Structures.Contact;
import Structures.CustomException;
import Structures.Message;
import Structures.Unit;
import javafx.application.Platform;
import javafx.scene.control.Alert;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;
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
            String ipAddress = null;

            for (Enumeration enumeration = NetworkInterface.getNetworkInterfaces(); enumeration.hasMoreElements();) {

                NetworkInterface networkInterface = (NetworkInterface) enumeration.nextElement();

                for (Enumeration enumIpAddr = networkInterface.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                    InetAddress inetAddress = (InetAddress) enumIpAddr.nextElement();

                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ipAddress = inetAddress.getHostAddress();
                    }
                }
            }

            if (ipAddress == null)
                throw new CustomException.NoIPV4();

            return ipAddress;
        } catch (SocketException | CustomException.NoIPV4 ex) {
            throw new CustomException.NoIPV4();
        }
    }

    public static List<String> getAllPossibleIPs() {
        List<String> ipList = new ArrayList<>();
        String ipv4;
        ipv4 = SysInfo.getIPV4();

        final String ipIterator = ipv4.substring(0, ipv4.lastIndexOf(".") + 1);;
        int[] numbers = IntStream.range(1, 255).toArray();
        Arrays.stream(numbers).forEach(number -> ipList.add(String.format("%s%d", ipIterator, number)));

        return ipList.stream().filter(x -> !x.equals(ipv4)).collect(Collectors.toList());
    }

    public static void runTaskInUIThread(Runnable runnable){
        Platform.runLater(runnable);
    }

    public static void addMessageWithUIThread(Message message, String ip){
        Platform.runLater(() -> Main.getMainController().addMessage(message,Contacts.findContact(ip)));
    }
    public static void addMessageWithUIThread(Message message, Contact contact){
        Platform.runLater(() -> Main.getMainController().addMessage(message,contact));
    }
    
    public static long totalSizeInBytes(List<File> files) {
        return files.stream().mapToLong(x -> x.length()).sum();
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

    public static int kilobytesToMegabytes(int kb) {
        return kb / 1024;
    }

    public static File getDownloadsFolder() { return new File(System.getProperty("user.home"), "Archivos de SweetHome"); }

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
            Logger.getLogger(Tools.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
}
