package org.shdevelopment.Core;

import org.shdevelopment.SysInfo.Level;
import org.shdevelopment.SysInfo.Log;

import java.util.LinkedList;
import java.util.Queue;


class ThreadManager {

    public static final ThreadManager threadManager = new ThreadManager();

    private static Server SERVER = new Server();
    private static MessageReceiver MESSAGE_RECEIVER = new MessageReceiver();
    private static FileReceiver FILE_RECEIVER = new FileReceiver();
    private static DeviceFinder DEVICE_FINDER = new DeviceFinder();

    enum threadType {
        SERVER,
        IDE,
        RDM,
        RDA,
        BDD,
        RDI
    }

    public void initThreads() {
        //TODO Testear sincronizacion | Falta manejar errores.
        Queue<Thread> threadsQueue = new LinkedList<>();

        threadsQueue.add(SERVER);
        threadsQueue.add(MESSAGE_RECEIVER);
        threadsQueue.add(FILE_RECEIVER);
        threadsQueue.add(DEVICE_FINDER);

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

    public static void reportException(threadType type) {
        switch (type) {
            //TODO Throw custom exception para c/thread e infomar por consola.
            case SERVER:
                SERVER = new Server();
                SERVER.start();
                Log.addMessage("Servicio servidor reiniciado", Level.INFO);
                break;
            case RDM:
                MESSAGE_RECEIVER = new MessageReceiver();
                MESSAGE_RECEIVER.start();
                Log.addMessage("Servicio receptor de mensajes reiniciado", Level.INFO);
                break;
            case RDA:
                FILE_RECEIVER = new FileReceiver();
                FILE_RECEIVER.start();
                Log.addMessage("Servicio receptor de archivos reiniciado", Level.INFO);
                break;
            case BDD:
                DEVICE_FINDER = new DeviceFinder();
                DEVICE_FINDER.start();
                Log.addMessage("Servicio buscador de dispositivos reiniciado", Level.INFO);
                break;
        }
    }

}