package org.shdevelopment.Structures;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


public class Contact extends ContactData {

    private SecretKey aes;

    transient private List<Message> chatHistory = new ArrayList<>();

    public Contact(String ip, String name, PublicKey publicKey) {
        super(ip, name, publicKey);
    }

    public Contact(ContactData contactData) {
        super(contactData.getIp(), contactData.getName(), contactData.getPublicKey());
    }
    public Contact(ContactData contactData, SecretKey aes) {
        super(contactData.getIp(), contactData.getName(), contactData.getPublicKey());
        this.aes = aes;
    }

    public synchronized void addMessageToHistory(Message message) {
        chatHistory.add(message);
    }

    public List<Message> getChatHistory() {
        return chatHistory;
    }

    public SecretKey getAes() {
        return aes;
    }

    public void setAes(SecretKey aes) {
        this.aes = aes;
    }
}
