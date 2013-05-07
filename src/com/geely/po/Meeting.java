package com.geely.po;

public class Meeting extends Message {
    //会议地点
    private String address;

    //参会人员
    private String joinUsers;

    //要求
    private String require;

    //会议类型 01 一般会议
    private String meeting_type;

    public Meeting() {
        super();
    }
    
    public Meeting(String title, String content, Long receiveTime,
        Integer readFlag) {
        super(title, content, receiveTime, readFlag);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJoinUsers() {
        return joinUsers;
    }

    public void setJoinUsers(String joinUsers) {
        this.joinUsers = joinUsers;
    }

    public String getRequire() {
        return require;
    }

    public void setRequire(String require) {
        this.require = require;
    }

    public String getMeeting_type() {
        return meeting_type;
    }

    public void setMeeting_type(String meeting_type) {
        this.meeting_type = meeting_type;
    }
}
