package org.shdevelopment.Server;

import org.shdevelopment.ContactManagement.ContactBookInterface;
import org.shdevelopment.Core.Client;
import org.shdevelopment.Core.Tools;
import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class DeviceFinder extends ServerComponent {

    public DeviceFinder(int threadID, String componentName, ContactBookInterface contactBook, ComponentManager componentManager) {
        super(threadID, componentName, contactBook, componentManager);
    }

    @Override
    public void run() {

        List<String> ipList = Tools.getIPsFromLANDevices();

        notifyToComponentManager();

        ExecutorService threadpool = Executors.newFixedThreadPool(5);

        ipList.forEach(ip -> {
            Runnable tryConnection = () -> contactBook.addContact(Client.contactHandshake(ip, 1000));
            threadpool.submit(tryConnection);
        });

        try {
            threadpool.shutdown();
            threadpool.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Log.addMessage(ex.getMessage(), Level.ERROR);
        }

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
