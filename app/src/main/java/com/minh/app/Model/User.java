package com.minh.app.Model;

import java.util.List;

public class User {
    private String username;
    public String status;
    private String email;
    private String id;
    private String imageURL;
    private String backgorund_imgURL;
    private String Online;

    public User(String id, String username, String email, String imageURL, String status, String backgorund_imgURL, String Online) {
        this.username = username;
        this.email = email;
        this.imageURL = imageURL;
        this.status = status;
        this.id = id;
        this.backgorund_imgURL = backgorund_imgURL;
        this.Online = Online;
    }

    public User() {
    }
    public String getOnline(){
        return  Online;
    }
    public void setOnline(String Online){
        this.Online = Online;
    }
    public String getBackgorund_imgURL() {
        return backgorund_imgURL;
    }

    public void setBackgorund_imgURL(String backgorund_imgURL) {
        this.backgorund_imgURL = backgorund_imgURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
