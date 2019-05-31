package com.example.instamaterial.Models;

public class User {
    private String uid,name,email,password,dp_url;
    public User(){}

    public User(String uid, String name, String email, String password, String dp_url) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.password = password;
        this.dp_url = dp_url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDp_url() {
        return dp_url;
    }

    public void setDp_url(String dp_url) {
        this.dp_url = dp_url;
    }
}
