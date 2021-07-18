package com.heloo.android.osmapp.model;

/**
 * Created by Witness on 6/15/21
 * Describe:
 */
public class HotArticleBean {

    private String heatsumber;
    private String articleId;
    private String icon;
    private String subject;
    private String createDate;

    public String getHeatsumber() {
        return heatsumber;
    }

    public void setHeatsumber(String heatsumber) {
        this.heatsumber = heatsumber;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
