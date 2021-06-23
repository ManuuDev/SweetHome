package org.shdevelopment.Constant;

public class Network {

    public final static int SERVER_PORT;
    public final static int ECO_PORT;
    public final static int MESSAGES_PORT;
    public final static int FILE_REQUEST_PORT;
    public final static int FILE_PORT;
    public final static int FILE_BUFFER;

    static {
        SERVER_PORT = 37201;
        ECO_PORT = 36100;
        MESSAGES_PORT = 30107;
        FILE_PORT = 30187;
        FILE_REQUEST_PORT = 29187;
        FILE_BUFFER = 64 * 1024;
    }
}
