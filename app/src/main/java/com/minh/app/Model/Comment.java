package com.minh.app.Model;

public class Comment {
    private String Content;
    private String Creater;
    private String username;
    private String profile_img;
    private String TimePost ;

    public Comment(String Content, String Creater, String TimePost, String username, String profile_img) {
        this.Content = Content;
        this.username = username;
        this.profile_img = profile_img;
        this.Creater = Creater;
        this.TimePost = TimePost;
    }

    public Comment() {
    }


    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }

    public String getCreater() {
        return Creater;
    }

    public void setCreater(String Creater) {
        this.Creater = Creater;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public String getTimePost() {
        return TimePost;
    }

    public void setTimePost(String TimePost) {
        this.TimePost = TimePost;
    }
}
