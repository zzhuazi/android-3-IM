package com.ljh.im.model.bean;

/**
 * Created by Administrator on 2017/12/13.
 * 群信息bean类
 */

public class GroupInfo {
    private String groupNmae; //群名称
    private String groupID;   //群ID
    private String invatePerson; //邀请人

    public GroupInfo() {
    }

    public GroupInfo(String groupNmae, String groupID, String invatePerson) {
        this.groupNmae = groupNmae;
        this.groupID = groupID;
        this.invatePerson = invatePerson;
    }

    public String getGroupNmae() {
        return groupNmae;
    }

    public void setGroupNmae(String groupNmae) {
        this.groupNmae = groupNmae;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getInvatePerson() {
        return invatePerson;
    }

    public void setInvatePerson(String invatePerson) {
        this.invatePerson = invatePerson;
    }

    @Override
    public String toString() {
        return "GroupInfo{" +
                "groupNmae='" + groupNmae + '\'' +
                ", groupID='" + groupID + '\'' +
                ", invatePerson='" + invatePerson + '\'' +
                '}';
    }
}
