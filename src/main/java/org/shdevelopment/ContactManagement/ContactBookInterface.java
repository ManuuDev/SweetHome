package org.shdevelopment.ContactManagement;

import org.shdevelopment.Structures.Contact;

import java.net.Socket;
import java.util.List;

public interface ContactBookInterface {

    void addContact(Contact contact);

    void removeContact(Contact contact);

    Contact searchContact(String ip);

    Contact searchContact(Socket socket);

    boolean existContact(String ip);

    boolean existContact(Socket socket);

    String getContactName(String ip);

    String getContactName(Socket socket);

    void updateContactList();

    List<Contact> getCopyOfContactBook();
}
