package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 6/17/21
 * Describe:
 */
public class TeamDetailBean {

    public UserBean user;
    public List<Collect> collect;

    public static class Collect{

        public List<ListDataBean> listData;
        public String Day;

        public static class ListDataBean {
            public String subject;
            public String day;
            public String icon;
            public String articleId;
            public Integer forwardNum;
            public Integer clickNum;
        }
    }



    public static class UserBean {

        private String icon;
        private String realName;
        private String integration;
        private String clickNumber;
        private String forwardnumber;

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getRealName() {
            return realName;
        }

        public void setRealName(String realName) {
            this.realName = realName;
        }

        public String getIntegration() {
            return integration;
        }

        public void setIntegration(String integration) {
            this.integration = integration;
        }

        public String getClickNumber() {
            return clickNumber;
        }

        public void setClickNumber(String clickNumber) {
            this.clickNumber = clickNumber;
        }

        public String getForwardnumber() {
            return forwardnumber;
        }

        public void setForwardnumber(String forwardnumber) {
            this.forwardnumber = forwardnumber;
        }
    }
}
