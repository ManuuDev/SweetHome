package org.shdevelopment.Server;

import org.shdevelopment.ContactManagement.ContactBookInterface;
import org.shdevelopment.Core.Tools;
import org.shdevelopment.Structures.CustomException;
import java.util.logging.Level;
import org.shdevelopment.SysInfo.Log;

import java.net.BindException;
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
                Log.addMessage(ex.getMessage(), Level.WARNING);
            }
        }
    }

    public void reportException(threadType type, String componentName, Exception ex) {

        Log.addMessage("Error en el servicio " + componentName + ex.getMessage(), Level.WARNING);

        if(ex instanceof BindException)
            Tools.exitWithError(new CustomException.PortAlreadyInUse());

        ServerComponent serverComponent = null;

        switch (type) {
            case SERVER:
                serverComponent = new ContactServer(getNewThreadID(), componentName, contactBook, COMPONENT_MANAGER);
                contactServer = (ContactServer) serverComponent;
                break;
            case RDM:
                serverComponent = new MessageReceiver(getNewThreadID(), componentName, contactBook, COMPONENT_MANAGER);
                messageReceiver = (MessageReceiver) serverComponent;
                break;
            case RDA:
                serverComponent = new FileReceiver(getNewThreadID(), componentName, contactBook, COMPONENT_MANAGER);
                fileReceiver = (FileReceiver) serverComponent;
                break;
            case BDD:
                serverComponent = new DeviceFinder(getNewThreadID(), componentName, contactBook, COMPONENT_MANAGER);
                deviceFinder = (DeviceFinder) serverComponent;
                break;
        }

        if (serverComponent == null)
            Tools.exitWithError(new UnsupportedOperationException("Error inesperado, no se reconoce el tipo de servicio"));

        serverComponent.start();
        Log.addMessage("Servicio " + componentName + "reiniciado", Level.INFO);
    }

    private int getNewThreadID() {
        threadCounter++;
        return threadCounter;
    }
}
