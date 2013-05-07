package com.geely.po;

public class Message {
    //id
    protected Integer id;

    //用户ID
    protected Integer userId;

    //会议时间
    protected String mtime;

    //会议标题
    protected String title;

    //会议内容
    protected String content;

    //发送人
    protected String sendUser;

    //接受时间
    protected Long receiveTime;

    //是否阅读  0 未读   1已读
    protected Integer readFlag;

    public Message() {
        super();
    }

    public Message(String title, String content, Long receiveTime,
        Integer readFlag) {
        super();
        this.title = title;
        this.content = content;
        this.receiveTime = receiveTime;
        this.readFlag = readFlag;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getMtime() {
        return mtime;
    }

    public void setMtime(String mtime) {
        this.mtime = mtime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public Long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Integer getReadFlag() {
        return readFlag;
    }

    public void setReadFlag(Integer readFlag) {
        this.readFlag = readFlag;
    }
}
