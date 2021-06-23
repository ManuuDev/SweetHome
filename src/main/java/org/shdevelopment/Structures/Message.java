package org.shdevelopment.Structures;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;
import java.io.Serializable;

public class Message implements Serializable {
    @JsonbProperty("name")
    private String senderName;
    @JsonbProperty("hour")
    private String hour;
    @JsonbProperty("text")
    private String text;
    @JsonbProperty("local")
    private boolean local;

    @JsonbTransient
    private String receiverIp;

    public Message(String senderName, String receiverIp, String hour, String text) {
        this.senderName = senderName;
        this.receiverIp = receiverIp;
        this.hour = hour;
        this.text = text;
        this.local = Boolean.FALSE;
    }

    public Message(String senderName, String receiverIp, String hour, String text, boolean local) {
        this.senderName = senderName;
        this.receiverIp = receiverIp;
        this.hour = hour;
        this.text = text;
        this.local = local;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getReceiverIp() {
        return receiverIp;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
