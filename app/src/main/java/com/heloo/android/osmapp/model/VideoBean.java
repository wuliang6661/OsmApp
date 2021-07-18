package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 5/25/21
 * Describe:
 */
public class VideoBean {

    private ListBean list;

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public static class ListBean {

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
            private String articleId;
            private String subject;
            private String icon;
            private Object publishDate;
            private String createDate;
            private String dateTimeStr;
            private String pageView;
            private int isTop;
            private String url;

            public String getArticleId() {
                return articleId;
            }

            public void setArticleId(String articleId) {
                this.articleId = articleId;
            }

            public String getSubject() {
                return subject;
            }

            public void setSubject(String subject) {
                this.subject = subject;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public Object getPublishDate() {
                return publishDate;
            }

            public void setPublishDate(Object publishDate) {
                this.publishDate = publishDate;
            }

            public String getCreateDate() {
                return createDate;
            }

            public void setCreateDate(String createDate) {
                this.createDate = createDate;
            }

            public String getDateTimeStr() {
                return dateTimeStr;
            }

            public void setDateTimeStr(String dateTimeStr) {
                this.dateTimeStr = dateTimeStr;
            }

            public String getPageView() {
                return pageView;
            }

            public void setPageView(String pageView) {
                this.pageView = pageView;
            }

            public int getIsTop() {
                return isTop;
            }

            public void setIsTop(int isTop) {
                this.isTop = isTop;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
