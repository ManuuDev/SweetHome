package SysInfo;

import Core.Main;
import Core.Tools;

import static Core.Tools.runTaskInUIThread;

public class Log {
    
    public static void addMessage(String mensaje, Nivel nivel) {

        String level;
        
        switch (nivel) {
            case INFO:  level = "[INFO]";
                break;
            case ERROR: level = "[ERROR]";
                break;
            case CRITICO: level = "[ERROR CRITICO]";
                break;
            default: level = "";
                break;
        }
        
        String log = String.format("%s [%s] || %s", level, Tools.getSystemTime(), mensaje);
        System.out.println(log);
        runTaskInUIThread(() -> Main.console.insertMessage(log));
    }
}
