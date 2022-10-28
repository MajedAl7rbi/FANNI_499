package com.app.fanni;

import java.io.Serializable;

public class User implements Serializable {
    public String UID;
    public String username;
    public String email;
    public String phone;
    public String nationalID;
    public String userType;
    public String status;
    public String jobType;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(String UID, String username, String email, String phone, String nationalID, String userType) {
        this.UID = UID;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.nationalID = nationalID;
        this.userType = userType;
    }

    public User(String UID, String username, String email, String phone, String nationalID, String userType, String status, String jobType) {
        this.UID = UID;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.nationalID = nationalID;
        this.userType = userType;
        this.status = status;
        this.jobType = jobType;
    }
}