package Core;

import Structures.Contact;
import SysInfo.Log;
import SysInfo.Nivel;
import javafx.application.Platform;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static Core.Tools.runTaskInUIThread;

public class Contacts {

    public static ArrayList<Contact> CONTACT_LIST = new ArrayList<>();

    public static synchronized void addNewContact(Contact contact) {

        if (contact != null && !Contacts.isContact(contact)) {
            CONTACT_LIST.add(contact);
            updateContactList();

            Log.addMessage("Contacto agregado: " + contact.getName(), Nivel.INFO);
        }
    }

    public static synchronized void updateContactList() {
        runTaskInUIThread(() -> Main.getMainController().updateContactList(CONTACT_LIST));
    }

    public static String getContactName(Socket socket) {
        return findContact(socket).getName();
    }

    public static String getContactName(String ip) {
        return findContact(ip).getName();
    }

    public static Contact findContact(String ip) {
        return CONTACT_LIST.stream().filter(x -> x.getIp().equals(ip)).findFirst().orElse(null);
    }

    public static Contact findContact(Socket socket) {
        String ip = Tools.getSocketIp(socket);
        return findContact(ip);
    }

    public static boolean isContact(String ip) {
        return CONTACT_LIST.stream().anyMatch(x -> x.getIp().equals(ip));
    }

    public static boolean isContact(Contact contact) {
        return isContact(contact.getIp());
    }

    public static boolean isContact(Socket socket) {
        String ip = Tools.getSocketIp(socket);
        return isContact(ip);
    }
}