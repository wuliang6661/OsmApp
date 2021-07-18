package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 5/11/21
 * Describe:
 */
public class TitleBean {

    private String id;
    private String categoryId;
    private String name;
    private String icon;
    private String dataType;
    private String articleAudit;
    private String showClients;
    private String commentPermission;
    private int lvl;
    private String upId;
    private String status;
    private String statusDate;
    private String createDate;
    private String modifyDate;
    private List<ChildrenBeanX> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getArticleAudit() {
        return articleAudit;
    }

    public void setArticleAudit(String articleAudit) {
        this.articleAudit = articleAudit;
    }

    public String getShowClients() {
        return showClients;
    }

    public void setShowClients(String showClients) {
        this.showClients = showClients;
    }

    public String getCommentPermission() {
        return commentPermission;
    }

    public void setCommentPermission(String commentPermission) {
        this.commentPermission = commentPermission;
    }

    public int getLvl() {
        return lvl;
    }

    public void setLvl(int lvl) {
        this.lvl = lvl;
    }

    public String getUpId() {
        return upId;
    }

    public void setUpId(String upId) {
        this.upId = upId;
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

    public List<ChildrenBeanX> getChildren() {
        return children;
    }

    public void setChildren(List<ChildrenBeanX> children) {
        this.children = children;
    }

    public static class ChildrenBeanX {

        private String id;
        private String categoryId;
        private String name;
        private String icon;
        private String dataType;
        private String articleAudit;
        private String showClients;
        private String commentPermission;
        private int lvl;
        private String upId;
        private String status;
        private String statusDate;
        private String createDate;
        private String modifyDate;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getArticleAudit() {
            return articleAudit;
        }

        public void setArticleAudit(String articleAudit) {
            this.articleAudit = articleAudit;
        }

        public String getShowClients() {
            return showClients;
        }

        public void setShowClients(String showClients) {
            this.showClients = showClients;
        }

        public String getCommentPermission() {
            return commentPermission;
        }

        public void setCommentPermission(String commentPermission) {
            this.commentPermission = commentPermission;
        }

        public int getLvl() {
            return lvl;
        }

        public void setLvl(int lvl) {
            this.lvl = lvl;
        }

        public String getUpId() {
            return upId;
        }

        public void setUpId(String upId) {
            this.upId = upId;
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
