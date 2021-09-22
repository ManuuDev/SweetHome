package org.shdevelopment.ContactManagement;

import org.shdevelopment.Core.Main;
import org.shdevelopment.Core.Tools;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.shdevelopment.Core.Tools.runTaskInUIThread;

public class StatelessContactBook implements ContactBookInterface {

    private static final StatelessContactBook INSTANCE = new StatelessContactBook();

    StatelessContactBook() {
        this.contactList = new ArrayList<>();
    }

    public static StatelessContactBook getInstance() {
        return INSTANCE;
    }

    /*------------------------------------------------------------------------------------------------*/

    private final ArrayList<Contact> contactList;

    public synchronized void addContact(Contact contact) {

        if (contact != null && !existContact(contact)) {
            contactList.add(contact);
            updateContactList();

            Log.addMessage("Contacto agregado: " + contact.getName(), Level.INFO);
        }
    }

    public void removeContact(String ip) {
        contactList.remove(searchContact(ip));
    }

    public void removeContact(Contact contact) {
        contactList.remove(contact);
    }

    public void clearContactBook() {
        contactList.clear();
    }

    public synchronized void updateContactList() {
        runTaskInUIThread(() -> Main.getMainController().updateContactList(contactList));
    }

    public List<Contact> getCopyOfContactBook() {
        return Collections.unmodifiableList(contactList);
    }

    public String getContactName(Socket socket) {
        return searchContact(socket).getName();
    }

    public String getContactName(String ip) {
        return searchContact(ip).getName();
    }

    public Contact searchContact(String ip) {
        return contactList.stream().filter(x -> x.getIp().equals(ip)).findFirst().orElse(null);
    }

    public Contact searchContact(Socket socket) {
        String ip = Tools.getSocketIp(socket);
        return searchContact(ip);
    }

    public boolean existContact(String ip) {
        return contactList.stream().anyMatch(x -> x.getIp().equals(ip));
    }

    public boolean existContact(Contact contact) {
        return existContact(contact.getIp());
    }

    public boolean existContact(Socket socket) {
        String ip = Tools.getSocketIp(socket);
        return existContact(ip);
    }
}
