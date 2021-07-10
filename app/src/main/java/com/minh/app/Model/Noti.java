package com.minh.app.Model;

public class Noti {
    private String Group;
    private String IsRead;
    private String Sent;
    private String Type;
    private String NameGrp;
    private String ID;
    private String Post;
    private String Reject;

    public Noti(String Group, String IsRead, String Sent, String Type, String NameGrp, String ID, String Post, String Reject) {
        this.Group = Group;
        this.IsRead = IsRead;
        this.Sent = Sent;
        this.Type = Type;
        this.NameGrp = NameGrp;
        this.ID = ID;
        this.Post = Post;
        this.Reject = Reject;
    }

    public Noti() {
    }

    public String getReject() {
        return Reject;
    }

    public void setReject(String reject) {
        Reject = reject;
    }

    public String getPost() {
        return Post;
    }

    public void setPost(String post) {
        Post = post;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNameGrp() {
        return NameGrp;
    }

    public void setNameGrp(String nameGrp) {
        NameGrp = nameGrp;
    }

    public String getGroup() {
        return Group;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public String getIsRead() {
        return IsRead;
    }

    public void setIsRead(String isRead) {
        IsRead = isRead;
    }

    public String getSent() {
        return Sent;
    }

    public void setSent(String sent) {
        Sent = sent;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
