package com.geely.po;


/**
 *
 *
 * <p>Title: 好爸妈客户端</p>
 * <p>Description:频道信息对应的PO类</p>
 * <p>创建日期:2013-1-11</p>
 * @author ZhouChao
 * @version 1.0
 * <p>湖南家校圈科技有限公司</p>
 */
public class Channel {
    private Integer id;

    //添加到主页的排序号
    private Integer num;

    // 标题
    private String title;

    // 列表图片
    private String icon;
    private boolean group;

    public Channel() {
        super();
    }

    public Channel(Integer id, Integer num, String title, String icon) {
        super();
        this.id = id;
        this.num = num;
        this.title = title;
        this.icon = icon;
    }

    public Channel(Integer id,String title, boolean group) {
        super();
        this.id = id;
        this.title = title;
        this.group = group;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }
}
