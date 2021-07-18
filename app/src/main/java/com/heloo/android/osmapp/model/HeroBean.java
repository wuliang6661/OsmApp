package com.heloo.android.osmapp.model;

/**
 * Created by Witness on 6/15/21
 * Describe:
 */
public class HeroBean {


    private String scorenumber;
    private String uid;
    private String nickname;
    private String department;
    private String realName;
    private String icon;

    public String getScorenumber() {
        return scorenumber;
    }

    public void setScorenumber(String scorenumber) {
        this.scorenumber = scorenumber;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
