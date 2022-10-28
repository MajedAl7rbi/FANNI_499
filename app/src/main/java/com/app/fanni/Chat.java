package com.app.fanni;

import java.io.Serializable;

public class Chat implements Serializable {
    public String id;
    public String citizenID;
    public String workerID;
    public String requestID;
    public Chat(){}
    public Chat(String id, String citizenID, String workerID, String requestID) {
        this.id = id;
        this.citizenID = citizenID;
        this.workerID = workerID;
        this.requestID = requestID;
    }
}
