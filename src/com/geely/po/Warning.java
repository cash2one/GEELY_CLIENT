package com.geely.po;

public class Warning extends Message{
    //责任人
    private String solveUsers;

    //说明
    private String explain;

    //预警类别  一级预警  二级预警  三级预警
    private String lb;

    public String getSolveUsers() {
        return solveUsers;
    }

    public void setSolveUsers(String solveUsers) {
        this.solveUsers = solveUsers;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getLb() {
        return lb;
    }

    public void setLb(String lb) {
        this.lb = lb;
    }
}
