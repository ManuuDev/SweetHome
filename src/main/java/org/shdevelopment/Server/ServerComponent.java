package org.shdevelopment.Server;

import org.shdevelopment.ContactManagement.ContactBookInterface;
import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

public abstract class ServerComponent extends Thread {

    int threadID;

    String componentName;

    ContactBookInterface contactBook;

    ComponentManager componentManager;

    public ServerComponent(int threadID, String componentName, ContactBookInterface contactBook, ComponentManager componentManager) {
        this.threadID = threadID;
        this.componentName = componentName;
        this.contactBook = contactBook;
        this.componentManager = componentManager;
    }

    public void notifyToComponentManager() {
        synchronized (componentManager) {
            Log.addMessage(String.format("[%s iniciado]", componentName), Level.INFO);
            componentManager.notify();
        }
    }

}
