package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 6/15/21
 * Describe:
 */
public class TeamBean {

    private List<UserlistBean> userlist;
    private List<DeptlistBean> deptlist;

    public List<UserlistBean> getUserlist() {
        return userlist;
    }

    public void setUserlist(List<UserlistBean> userlist) {
        this.userlist = userlist;
    }

    public List<DeptlistBean> getDeptlist() {
        return deptlist;
    }

    public void setDeptlist(List<DeptlistBean> deptlist) {
        this.deptlist = deptlist;
    }

    public static class UserlistBean {

        private String realName;
        private String uid;
        private String username;
        private String readNumber;
        private String forwardNumber;
        private String icon;

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getReadNumber() {
            return readNumber;
        }

        public void setReadNumber(String readNumber) {
            this.readNumber = readNumber;
        }

        public String getForwardNumber() {
            return forwardNumber;
        }

        public void setForwardNumber(String forwardNumber) {
            this.forwardNumber = forwardNumber;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }

    public static class DeptlistBean {

        private Integer forwardnumber;
        private Integer readnumber;
        private String clickNumber;
        private String userNumber;
        private String name;
        private String deptId;
        private String icon;

        public int getForwardnumber() {
            return forwardnumber == null ? 0 : forwardnumber;
        }

        public void setForwardnumber(Integer forwardnumber) {
            this.forwardnumber = forwardnumber;
        }

        public int getReadnumber() {
            return readnumber == null ? 0 : readnumber;
        }

        public void setReadnumber(Integer readnumber) {
            this.readnumber = readnumber;
        }

        public String getClickNumber() {
            return clickNumber;
        }

        public void setClickNumber(String clickNumber) {
            this.clickNumber = clickNumber;
        }

        public String getUserNumber() {
            return userNumber;
        }

        public void setUserNumber(String userNumber) {
            this.userNumber = userNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }
    }
}
