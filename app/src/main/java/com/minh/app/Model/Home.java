package com.minh.app.Model;

import com.google.firebase.database.DataSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Home {
    private String Img;
    private String id;
    private String Content;
    private String Creater;
    private String username;
    private  String imageURL;
    private String TimePost ;
    private int like_count;
    private String Isshare;
    private String PostShare;
    private String PostToGr;
    private String PostToUser;
    private String NameGrp;
    private String inGroup;
    String Type;

    public Home(String Img, String Content, String Creater, String TimePost, String username, String imageURL, String Isshare, String PostShare, String PostToGr, String PostToUser, String Type) {
        this.Img = Img;
        this.TimePost = TimePost;
        this.Content = Content;
        this.Creater = Creater;
        this.username = username;
        this.imageURL = imageURL;
        this.Isshare = Isshare;
        this.PostShare = PostShare;
        this.PostToGr = PostToGr;
        this.PostToUser = PostToUser;
        this.Type = Type;
    }

    public Home() {
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getInGroup() {
        return inGroup;
    }

    public void setInGroup(String inGroup) {
        this.inGroup = inGroup;
    }

    public String getNameGrp() {
        return NameGrp;
    }

    public void setNameGrp(String nameGrp) {
        NameGrp = nameGrp;
    }

    public String getPostToGr() {
        return PostToGr;
    }

    public void setPostToGr(String postToGr) {
        PostToGr = postToGr;
    }

    public String getPostToUser() {
        return PostToUser;
    }

    public void setPostToUser(String postToUser) {
        PostToUser = postToUser;
    }

    public String getPostShare() {
        return PostShare;
    }

    public void setPostShare(String postShare) {
        PostShare = postShare;
    }

    public String getIsshare() {
        return Isshare;
    }

    public void setIsshare(String isshare) {
        Isshare = isshare;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTimePost() {
        return TimePost;
    }

    public void setTimePost(String timePost) {
        TimePost = timePost;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getCreater() {
        return Creater;
    }

    public void setCreater(String Creater) {
        this.Creater = Creater;
    }

    public String getImg() {
        return Img;
    }

    public void setImg(String Img) {
        this.Img = Img;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

}
