package org.shdevelopment.SysInfo;

import org.shdevelopment.Core.Main;
import org.shdevelopment.Core.Tools;

import java.io.IOException;
import java.util.Date;
import java.util.logging.*;

public class Log {

    private static final int FILE_SIZE = 1024;
    private static final Logger logger = Logger.getLogger("SweetHome-log");

    static {

        FileHandler fileHandler;
        CustomConsoleHandler consoleHandler;

        try {
            fileHandler = new FileHandler(Tools.getDownloadsFolder() + "/SweetHome.log", FILE_SIZE, 3, true);
            setNewHandler(fileHandler);

            consoleHandler = new CustomConsoleHandler();
            setNewHandler(consoleHandler);

            logger.setUseParentHandlers(false);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void setNewHandler(Handler handler) {
        handler.setFormatter(new CustomFormatter());
        handler.setLevel(Level.ALL);
        logger.addHandler(handler);
    }

    public static void addMessage(String message, Level level) {
        logger.log(level, message);
    }
}

class CustomConsoleHandler extends Handler {

    @Override
    public void publish(LogRecord logRecord) {
        Main.console.insertMessage(this.getFormatter().format(logRecord));
    }

    @Override
    public void flush() {}

    @Override
    public void close() throws SecurityException {}
}

class CustomFormatter extends SimpleFormatter {
    private static final String format = "[%1$tF %1$tT] [%2$-7s] %3$s %n";

    @Override
    public synchronized String format(LogRecord logRecord) {
        return String.format(format,
                new Date(logRecord.getMillis()),
                logRecord.getLevel().getLocalizedName(),
                logRecord.getMessage()
        );
    }
}