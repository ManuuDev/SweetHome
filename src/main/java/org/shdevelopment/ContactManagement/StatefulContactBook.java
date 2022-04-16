package org.shdevelopment.ContactManagement;

import org.shdevelopment.Core.Tools;
import org.shdevelopment.Database.DBContactManager;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.SysInfo.Log;

import java.net.Socket;
import java.sql.SQLException;
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
        //TODO Fix return null
        try {
            return DBContactManager.getDbContactManager().readEntity(ip);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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

    public void updateContactList() {}

    public List<Contact> getCopyOfContactBook() {
        //TODO Fix return null con Optional o que tome exitWithError como fin de programa
        try {
            List<Contact> list = DBContactManager.getDbContactManager().readAllContacts();
            return list;
        } catch (SQLException ex){
            Tools.exitWithError(ex);
            return null;
        }
    }
}
