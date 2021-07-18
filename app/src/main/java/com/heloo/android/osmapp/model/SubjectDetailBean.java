package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 7/4/21
 * Describe:
 */
public class SubjectDetailBean {

    private SpecialBean special;
    private List<ArticlelistBean> articlelist;

    public SpecialBean getSpecial() {
        return special;
    }

    public void setSpecial(SpecialBean special) {
        this.special = special;
    }

    public List<ArticlelistBean> getArticlelist() {
        return articlelist;
    }

    public void setArticlelist(List<ArticlelistBean> articlelist) {
        this.articlelist = articlelist;
    }

    public static class SpecialBean {

        private String id;
        private Object categoryIds;
        private String specialId;
        private String subject;
        private String icon;
        private String url;
        private String isRecommend;
        private String viewNum;
        private String pos;
        private String status;
        private Object statusDate;
        private Object createUid;
        private Object beginDate;
        private Object endDate;
        private String createDate;
        private String modifyDate;
        private String content;
        private String description;

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

        public String getViewNum() {
            return viewNum;
        }

        public void setViewNum(String viewNum) {
            this.viewNum = viewNum;
        }

        public String getPos() {
            return pos;
        }

        public void setPos(String pos) {
            this.pos = pos;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getStatusDate() {
            return statusDate;
        }

        public void setStatusDate(Object statusDate) {
            this.statusDate = statusDate;
        }

        public Object getCreateUid() {
            return createUid;
        }

        public void setCreateUid(Object createUid) {
            this.createUid = createUid;
        }

        public Object getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(Object beginDate) {
            this.beginDate = beginDate;
        }

        public Object getEndDate() {
            return endDate;
        }

        public void setEndDate(Object endDate) {
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

    public static class ArticlelistBean {

        private String article_id;
        private String read_num;
        private String subject;
        private String specialSubject;
        private String icon;
        private String create_date;
        private String specailIcon;
        private String publish_date;

        public String getArticle_id() {
            return article_id;
        }

        public void setArticle_id(String article_id) {
            this.article_id = article_id;
        }

        public String getRead_num() {
            return read_num;
        }

        public void setRead_num(String read_num) {
            this.read_num = read_num;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getSpecialSubject() {
            return specialSubject;
        }

        public void setSpecialSubject(String specialSubject) {
            this.specialSubject = specialSubject;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getCreate_date() {
            return create_date;
        }

        public void setCreate_date(String create_date) {
            this.create_date = create_date;
        }

        public String getSpecailIcon() {
            return specailIcon;
        }

        public void setSpecailIcon(String specailIcon) {
            this.specailIcon = specailIcon;
        }

        public String getPublish_date() {
            return publish_date;
        }

        public void setPublish_date(String publish_date) {
            this.publish_date = publish_date;
        }
    }
}
