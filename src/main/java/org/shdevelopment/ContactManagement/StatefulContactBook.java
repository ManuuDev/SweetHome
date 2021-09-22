package org.shdevelopment.ContactManagement;

import org.shdevelopment.Structures.Contact;

import java.net.Socket;
import java.util.List;

public class StatefulContactBook implements ContactBookInterface {

    public void addContact(Contact contact) {

    }

    public void removeContact(Contact contact) {

    }


    public Contact searchContact(String ip) {
        return null;
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
