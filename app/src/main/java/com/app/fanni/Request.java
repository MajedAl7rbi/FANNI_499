package com.app.fanni;

import java.io.Serializable;

public class Request implements Serializable {
    public String id;
    public String title;
    public String details;
    public String phone;
    public String jobType;
    public String creatorID;
    public Request(){}
    public Request(String id, String title, String details, String phone, String jobType, String creatorID) {
        this.id = id;
        this.title = title;
        this.details = details;
        this.phone = phone;
        this.jobType = jobType;
        this.creatorID = creatorID;
    }
}
