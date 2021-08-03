package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 5/11/21
 * Describe:
 */
public class ArticleBean {


    private ArticleInfoListBean ArticleInfoList;
    private List<BannerBean> banner;
    private RecommendBanner Recommend;

    public RecommendBanner getRecommend() {
        return Recommend;
    }

    public void setRecommend(RecommendBanner recommend) {
        Recommend = recommend;
    }

    public static class RecommendBanner {

        public String id;
        public String icon;
        public String url;
        public String status;
    }


    public ArticleInfoListBean getArticleInfoList() {
        return ArticleInfoList;
    }

    public void setArticleInfoList(ArticleInfoListBean ArticleInfoList) {
        this.ArticleInfoList = ArticleInfoList;
    }

    public List<BannerBean> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerBean> banner) {
        this.banner = banner;
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


            private String articleId;
            private String subject;
            private String icon;
            private Object publishDate;
            private String createDate;
            private String dateTimeStr;
            private String pageView;
            private String isTop;
            private Object url;
            private String viewNum;

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

            public String getIsTop() {
                return isTop;
            }

            public void setIsTop(String isTop) {
                this.isTop = isTop;
            }

            public Object getUrl() {
                return url;
            }

            public void setUrl(Object url) {
                this.url = url;
            }

            public String getViewNum() {
                return viewNum;
            }

            public void setViewNum(String viewNum) {
                this.viewNum = viewNum;
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
        private String subject;

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

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }
    }
}
