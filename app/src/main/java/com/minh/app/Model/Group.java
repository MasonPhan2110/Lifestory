package com.minh.app.Model;

public class Group {
    private String Creater;
    private String BackGroundImg;
    private String NameGroup;
    private String Privacy;
    private String GroupID;


    public Group() {
    }

    public Group(String Creater, String BackGroundImg, String NameGroup, String Privacy, String GroupID) {
        this.Creater = Creater;
        this.BackGroundImg = BackGroundImg;
        this.NameGroup = NameGroup;
        this.Privacy = Privacy;
        this.GroupID = GroupID;
    }

    public String getCreater() {
        return Creater;
    }

    public void setCreater(String creater) {
        Creater = creater;
    }

    public String getBackGroundImg() {
        return BackGroundImg;
    }

    public void setBackGroundImg(String backGroundImg) {
        BackGroundImg = backGroundImg;
    }

    public String getNameGroup() {
        return NameGroup;
    }

    public void setNameGroup(String nameGroup) {
        NameGroup = nameGroup;
    }

    public String getPrivacy() {
        return Privacy;
    }

    public void setPrivacy(String privacy) {
        Privacy = privacy;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }
}
