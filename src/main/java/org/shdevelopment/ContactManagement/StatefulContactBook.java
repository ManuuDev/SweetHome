package org.shdevelopment.ContactManagement;

import org.shdevelopment.Database.DBContactManager;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.SysInfo.Log;

import java.net.Socket;
import java.util.List;
import java.util.logging.Level;

public class StatefulContactBook implements ContactBookInterface {

    private static final StatefulContactBook INSTANCE = new StatefulContactBook();

    public static StatefulContactBook getInstance() {
        return INSTANCE;
    }

    public void addContact(Contact contact) {
        DBContactManager.getDbContactManager().createEntity(contact);
        updateContactList();
        Log.addMessage("Contacto agregado: " + contact.getName(), Level.INFO);
    }

    public void removeContact(Contact contact) {
        DBContactManager.getDbContactManager().deleteEntity(contact);
    }


    public Contact searchContact(String ip) {
        return DBContactManager.getDbContactManager().readEntity(0);
    }

    public Contact searchContact(Socket socket) {
        return null;
    }


    public boolean existContact(String ip) {
        return false;
    }


    public boolean existContact(Socket socket) {
        return false;
    }


    public String getContactName(String ip) {
        return null;
    }


    public String getContactName(Socket socket) {
        return null;
    }


    public void updateContactList() {

    }

    public List<Contact> getCopyOfContactBook() {
        return null;
    }
}
