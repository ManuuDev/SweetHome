package org.shdevelopment.Structures;

import java.io.Serializable;
import java.security.PublicKey;

public class ContactData implements Serializable {

    private String ip, name;
    private PublicKey publicKey;

    public ContactData(String ip, String name, PublicKey publicKey) {
        this.ip = ip;
        this.name = name;
        this.publicKey = publicKey;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

}
