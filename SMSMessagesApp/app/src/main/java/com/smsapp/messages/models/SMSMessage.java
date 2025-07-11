package com.smsapp.messages.models;

public class SMSMessage {
    private long id;
    private String senderNumber;
    private String messageBody;
    private long timestamp;
    private boolean isSent;
    private int type; // 1 = received, 2 = sent

    public SMSMessage() {
    }

    public SMSMessage(long id, String senderNumber, String messageBody, long timestamp, boolean isSent) {
        this.id = id;
        this.senderNumber = senderNumber;
        this.messageBody = messageBody;
        this.timestamp = timestamp;
        this.isSent = isSent;
        this.type = isSent ? 2 : 1;
    }

    public SMSMessage(String senderNumber, String messageBody, long timestamp, boolean isSent) {
        this.senderNumber = senderNumber;
        this.messageBody = messageBody;
        this.timestamp = timestamp;
        this.isSent = isSent;
        this.type = isSent ? 2 : 1;
    }

    // Getters
    public long getId() {
        return id;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isSent() {
        return isSent;
    }

    public int getType() {
        return type;
    }

    // Setters
    public void setId(long id) {
        this.id = id;
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = senderNumber;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setSent(boolean sent) {
        isSent = sent;
        this.type = sent ? 2 : 1;
    }

    public void setType(int type) {
        this.type = type;
        this.isSent = (type == 2);
    }

    @Override
    public String toString() {
        return "SMSMessage{" +
                "id=" + id +
                ", senderNumber='" + senderNumber + '\'' +
                ", messageBody='" + messageBody + '\'' +
                ", timestamp=" + timestamp +
                ", isSent=" + isSent +
                ", type=" + type +
                '}';
    }
}
