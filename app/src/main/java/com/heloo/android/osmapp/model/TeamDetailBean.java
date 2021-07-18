package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 6/17/21
 * Describe:
 */
public class TeamDetailBean {

    private ArticleInfoListBean ArticleInfoList;
    private UserBean user;

    public ArticleInfoListBean getArticleInfoList() {
        return ArticleInfoList;
    }

    public void setArticleInfoList(ArticleInfoListBean ArticleInfoList) {
        this.ArticleInfoList = ArticleInfoList;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public static class ArticleInfoListBean {
        private int pageNum;
        private int pageSize;
        private int totalPage;
        private int total;
        private String status;
        private List<DataBean> data;

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getTotalPage() {
            return totalPage;
        }

        public void setTotalPage(int totalPage) {
            this.totalPage = totalPage;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {


            private String subject;
            private String day;
            private String icon;
            private String articleId;
            private String forwardNum;
            private String clickNum;

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public String getDay() {
                return day;
            }

            public void setDay(String day) {
                this.day = day;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getArticleId() {
                return articleId;
            }

            public void setArticleId(String articleId) {
                this.articleId = articleId;
            }

            public String getForwardNum() {
                return forwardNum;
            }

            public void setForwardNum(String forwardNum) {
                this.forwardNum = forwardNum;
            }

            public String getClickNum() {
                return clickNum;
            }

            public void setClickNum(String clickNum) {
                this.clickNum = clickNum;
            }
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
