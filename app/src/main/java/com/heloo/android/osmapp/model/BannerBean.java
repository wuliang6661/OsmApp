package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 5/25/21
 * Describe:
 */
public class BannerBean {

    private AnnouncementBean announcement;
    private List<ArticlespecialBean> articlespecial;
    private List<BannerBean2> banner;

    public AnnouncementBean getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(AnnouncementBean announcement) {
        this.announcement = announcement;
    }

    public List<ArticlespecialBean> getArticlespecial() {
        return articlespecial;
    }

    public void setArticlespecial(List<ArticlespecialBean> articlespecial) {
        this.articlespecial = articlespecial;
    }

    public List<BannerBean2> getBanner() {
        return banner;
    }

    public void setBanner(List<BannerBean2> banner) {
        this.banner = banner;
    }

    public static class AnnouncementBean {

        private String id;
        private String content;
        private int status;
        private String createDate;
        private String modifyDate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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

    public static class ArticlespecialBean {

        private String id;
        private Object categoryIds;
        private String specialId;
        private String subject;
        private String icon;
        private String url;
        private String isRecommend;
        private int viewNum;
        private int pos;
        private String status;
        private String statusDate;
        private String createUid;
        private String beginDate;
        private String endDate;
        private String createDate;
        private String modifyDate;
        private String content;
        private String description;
        private String webUrl;

        public String getWebUrl() {
            return webUrl;
        }

        public void setWebUrl(String webUrl) {
            this.webUrl = webUrl;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getCategoryIds() {
            return categoryIds;
        }

        public void setCategoryIds(Object categoryIds) {
            this.categoryIds = categoryIds;
        }

        public String getSpecialId() {
            return specialId;
        }

        public void setSpecialId(String specialId) {
            this.specialId = specialId;
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

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getIsRecommend() {
            return isRecommend;
        }

        public void setIsRecommend(String isRecommend) {
            this.isRecommend = isRecommend;
        }

        public int getViewNum() {
            return viewNum;
        }

        public void setViewNum(int viewNum) {
            this.viewNum = viewNum;
        }

        public int getPos() {
            return pos;
        }

        public void setPos(int pos) {
            this.pos = pos;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getStatusDate() {
            return statusDate;
        }

        public void setStatusDate(String statusDate) {
            this.statusDate = statusDate;
        }

        public String getCreateUid() {
            return createUid;
        }

        public void setCreateUid(String createUid) {
            this.createUid = createUid;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
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

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class BannerBean2 {


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
