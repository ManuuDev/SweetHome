package org.shdevelopment.SysInfo;

import org.shdevelopment.Core.Main;
import org.shdevelopment.Core.Tools;

import static org.shdevelopment.Core.Tools.runTaskInUIThread;

public class Log {
    
    public static void addMessage(String message, Level level) {

        String levelString;
        
        switch (level) {
            case INFO:  levelString = "[INFO]";
                break;
            case ERROR: levelString = "[ERROR]";
                break;
            case CRITIC: levelString = "[ERROR CRITICO]";
                break;
            default: levelString = "";
                break;
        }
        
        String log = String.format("%s [%s] || %s", levelString, Tools.getSystemTime(), message);
        System.out.println(log);
        runTaskInUIThread(() -> Main.console.insertMessage(log));
    }
}
