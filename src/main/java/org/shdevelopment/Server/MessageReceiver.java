package org.shdevelopment.Server;

import org.shdevelopment.ContactManagement.ContactBookInterface;
import org.shdevelopment.Core.SyntacticAnalyzer;
import org.shdevelopment.Crypto.Crypto;
import org.shdevelopment.Structures.Contact;
import org.shdevelopment.Structures.Message;
import org.shdevelopment.Structures.MessagePackage;
import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import static org.shdevelopment.Constant.Network.MESSAGES_PORT;
import static org.shdevelopment.Core.Tools.addMessageWithUIThread;

class MessageReceiver extends ServerComponent {

    public MessageReceiver(int threadID, String componentName, ContactBookInterface contactBook, ComponentManager componentManager) {
        super(threadID, componentName, contactBook, componentManager);
    }

    @Override
    public void run() {

        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        try {

            ServerSocket socketServer = new ServerSocket(MESSAGES_PORT);
            notifyToComponentManager();

            while (!Thread.currentThread().isInterrupted()) {

                Socket socket = socketServer.accept();

                if (contactBook.existContact(socket)) {

                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());

                    MessagePackage packet = (MessagePackage) objectInputStream.readObject();

                    if (packet.getMessage() != null) {

                        Contact contact = contactBook.searchContact(socket);

                        String finalMessage = unpackMessage(packet, contact);

                        Message newMessage = new Message(contact.getName(), contact.getIp(), packet.getDate(), finalMessage);

                        addMessageWithUIThread(newMessage, contact);

                    } else {
                        Log.addMessage("Error: El paquete recibido como mensaje es erroneo o esta dañado.", Level.ERROR);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
        }
    }

    public String unpackMessage(MessagePackage packet, Contact contact) {
        String message = Crypto.decryptMessage(packet.getMessage(), contact.getAes());

        return SyntacticAnalyzer.executeAnalyzers(message);
    }

}
