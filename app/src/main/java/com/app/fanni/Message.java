package com.app.fanni;

public class Message {
    public String id;
    public String senderID;
    public String message;
    public Message(){}
    public Message(String id, String senderID, String message) {
        this.id = id;
        this.senderID = senderID;
        this.message = message;
    }
}
