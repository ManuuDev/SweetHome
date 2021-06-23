package org.shdevelopment.Structures;

import java.io.Serializable;

public class FileInfo implements Serializable {

    private final String name;
    private final double size;
    
    public FileInfo(String name, double size){
        this.name = name;
        this.size = size;
    }
    
    public double getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}
