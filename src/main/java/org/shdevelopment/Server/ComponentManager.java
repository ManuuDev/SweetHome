package org.shdevelopment.Server;

import org.shdevelopment.ContactManagement.ContactBookInterface;
import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

import java.util.LinkedList;
import java.util.Queue;


public class ComponentManager {

    private final ComponentManager COMPONENT_MANAGER = this;
    private final ContactBookInterface contactBook;

    public ComponentManager(ContactBookInterface contactBook) {
        this.contactBook = contactBook;
    }

    private int threadCounter = 0;

    private ContactServer contactServer;
    private MessageReceiver messageReceiver;
    private FileReceiver fileReceiver;
    private DeviceFinder deviceFinder;

    enum threadType {
        SERVER,
        IDE,
        RDM,
        RDA,
        BDD,
        RDI
    }

    public void initThreads() {

        contactServer = new ContactServer(getNewThreadID(), "Servidor principal", contactBook, COMPONENT_MANAGER);
        messageReceiver = new MessageReceiver(getNewThreadID(), "Receptor de mensajes", contactBook, COMPONENT_MANAGER);
        fileReceiver = new FileReceiver(getNewThreadID(), "Receptor de archivos", contactBook, COMPONENT_MANAGER);
        deviceFinder = new DeviceFinder(getNewThreadID(), "Rastreador de dispositivos", contactBook, COMPONENT_MANAGER);

        //TODO Testear sincronizacion | Falta manejar errores.
        Queue<Thread> threadsQueue = new LinkedList<>();

        threadsQueue.add(contactServer);
        threadsQueue.add(messageReceiver);
        threadsQueue.add(fileReceiver);
        threadsQueue.add(deviceFinder);

        while (!threadsQueue.isEmpty()) {

            Thread nextThread = threadsQueue.poll();

            nextThread.start();

            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException ex) {
                Log.addMessage(ex.getMessage(), Level.ERROR);
            }
        }
    }

    public void reportException(threadType type) {
        switch (type) {
            //TODO Throw custom exception para c/thread e infomar por consola.
            case SERVER:
                contactServer = new ContactServer(getNewThreadID(), "Servidor principal", contactBook, COMPONENT_MANAGER);
                contactServer.start();
                Log.addMessage("Servicio servidor reiniciado", Level.INFO);
                break;
            case RDM:
                messageReceiver = new MessageReceiver(getNewThreadID(), "Receptor de mensajes", contactBook, COMPONENT_MANAGER);
                messageReceiver.start();
                Log.addMessage("Servicio receptor de mensajes reiniciado", Level.INFO);
                break;
            case RDA:
                fileReceiver = new FileReceiver(getNewThreadID(), "Receptor de archivos", contactBook, COMPONENT_MANAGER);
                fileReceiver.start();
                Log.addMessage("Servicio receptor de archivos reiniciado", Level.INFO);
                break;
            case BDD:
                deviceFinder = new DeviceFinder(getNewThreadID(), "Rastreador de dispositivos", contactBook, COMPONENT_MANAGER);
                deviceFinder.start();
                Log.addMessage("Servicio rastreador de dispositivos reiniciado", Level.INFO);
                break;
        }
    }

    private int getNewThreadID() {
        threadCounter++;
        return threadCounter;
    }
}
