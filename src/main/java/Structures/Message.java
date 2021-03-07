package Structures;

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

    @JsonbTransient
    private String receiverIp;
    @JsonbTransient
    private boolean localMessage;

    public Message(String senderName, String receiverIp, String hour, String text) {
        this.senderName = senderName;
        this.receiverIp = receiverIp;
        this.hour = hour;
        this.text = text;
    }

    public Message(String senderName, String receiverIp, String hour, String text, boolean localMessage) {
        this.senderName = senderName;
        this.receiverIp = receiverIp;
        this.hour = hour;
        this.text = text;
        this.localMessage = localMessage;
    }

    public boolean isLocalMessage() {
        return localMessage;
    }

    public void setLocalMessage(boolean localMessage) {
        this.localMessage = localMessage;
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
