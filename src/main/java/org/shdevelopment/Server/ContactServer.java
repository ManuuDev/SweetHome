package org.shdevelopment.Server;

import org.shdevelopment.Constant.SysInfo;
import org.shdevelopment.ContactManagement.ContactBookInterface;
import org.shdevelopment.Crypto.Crypto;
import org.shdevelopment.Server.ComponentManager.threadType;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.Structures.ContactData;
import java.util.logging.Level;
import org.shdevelopment.SysInfo.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.shdevelopment.Constant.Network.SERVER_PORT;

public class ContactServer extends ServerComponent {

    public ContactServer(int threadID, String componentName, ContactBookInterface contactBook, ComponentManager componentManager) {
        super(threadID, componentName, contactBook, componentManager);
    }

    @Override
    public void run() {

        ServerSocket serverSocket = null;

        try {

            serverSocket = new ServerSocket(SERVER_PORT);

            notifyToComponentManager();

            while (!Thread.currentThread().isInterrupted()) {

                Socket socket = serverSocket.accept();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                ContactData contactData = (ContactData) inputStream.readObject();

                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(SysInfo.LOCAL_CONTACT);

                byte[] contactAESKey = (byte[]) inputStream.readObject();

                contactBook.addContact(new Contact(contactData, Crypto.decryptSymmetricKey(contactAESKey)));
            }

        } catch (IOException | ClassNotFoundException ex) {

            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException ex1) {
                Log.addMessage("No se pudo cerrar el socket servidor: " + ex1.getMessage(), Level.WARNING);
            }

            componentManager.reportException(threadType.SERVER, componentName , ex);
        }

    }
}

