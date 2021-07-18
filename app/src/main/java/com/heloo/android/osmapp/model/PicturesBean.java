package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 5/25/21
 * Describe:
 */
public class PicturesBean {

    private ListBean list;
    private List<BannerBean> banner;

    public ListBean getList() {
        return list;
    }

    public void setList(ListBean list) {
        this.list = list;
    }

    public List<BannerBean> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerBean> banner) {
        this.banner = banner;
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
            private String publishDate;
            private String createDate;
            private String dateTimeStr;
            private int pageView;
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

            public String getPublishDate() {
                return publishDate;
            }

            public void setPublishDate(String publishDate) {
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

            public int getPageView() {
                return pageView;
            }

            public void setPageView(int pageView) {
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

    public static class BannerBean {

        private String id;
        private int bannerType;
        private int jumpType;
        private String jumpId;
        private String jumpUrl;
        private String imgurl;
        private String createDate;
        private String modifyDate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getBannerType() {
            return bannerType;
        }

        public void setBannerType(int bannerType) {
            this.bannerType = bannerType;
        }

        public int getJumpType() {
            return jumpType;
        }

        public void setJumpType(int jumpType) {
            this.jumpType = jumpType;
        }

        public String getJumpId() {
            return jumpId;
        }

        public void setJumpId(String jumpId) {
            this.jumpId = jumpId;
        }

        public String getJumpUrl() {
            return jumpUrl;
        }

        public void setJumpUrl(String jumpUrl) {
            this.jumpUrl = jumpUrl;
        }

        public String getImgurl() {
            return imgurl;
        }

        public void setImgurl(String imgurl) {
            this.imgurl = imgurl;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(String modifyDate) {
            this.modifyDate = modifyDate;
        }
    }
}
