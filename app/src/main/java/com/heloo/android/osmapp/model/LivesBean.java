package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 5/25/21
 * Describe:
 */
public class LivesBean {

    private List<EndlistBean> endlist;
    private List<DoinglistBean> doinglist;
    private List<NotstartedlistBean> notstartedlist;

    public List<EndlistBean> getEndlist() {
        return endlist;
    }

    public void setEndlist(List<EndlistBean> endlist) {
        this.endlist = endlist;
    }

    public List<DoinglistBean> getDoinglist() {
        return doinglist;
    }

    public void setDoinglist(List<DoinglistBean> doinglist) {
        this.doinglist = doinglist;
    }

    public List<NotstartedlistBean> getNotstartedlist() {
        return notstartedlist;
    }

    public void setNotstartedlist(List<NotstartedlistBean> notstartedlist) {
        this.notstartedlist = notstartedlist;
    }

    public static class EndlistBean {

        private String id;
        private String name;
        private String banner;
        private String commentNum;
        private String praiseNum;
        private String createDate;
        private String modifyDate;
        private String startDate;
        private String endDate;
        private int status;
        private int valid;
        private String viewNum;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(String commentNum) {
            this.commentNum = commentNum;
        }

        public String getPraiseNum() {
            return praiseNum;
        }

        public void setPraiseNum(String praiseNum) {
            this.praiseNum = praiseNum;
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

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }

        public String getViewNum() {
            return viewNum;
        }

        public void setViewNum(String viewNum) {
            this.viewNum = viewNum;
        }
    }

    public static class DoinglistBean {

        private String id;
        private String name;
        private String banner;
        private String commentNum;
        private String praiseNum;
        private String createDate;
        private String modifyDate;
        private String startDate;
        private String endDate;
        private int status;
        private String valid;
        private String viewNum;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(String commentNum) {
            this.commentNum = commentNum;
        }

        public String getPraiseNum() {
            return praiseNum;
        }

        public void setPraiseNum(String praiseNum) {
            this.praiseNum = praiseNum;
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

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }

        public String getViewNum() {
            return viewNum;
        }

        public void setViewNum(String viewNum) {
            this.viewNum = viewNum;
        }
    }

    public static class NotstartedlistBean {

        private String id;
        private String name;
        private String banner;
        private String commentNum;
        private String praiseNum;
        private String createDate;
        private String modifyDate;
        private String startDate;
        private String endDate;
        private int status;
        private int valid;
        private String viewNum;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBanner() {
            return banner;
        }

        public void setBanner(String banner) {
            this.banner = banner;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(String commentNum) {
            this.commentNum = commentNum;
        }

        public String getPraiseNum() {
            return praiseNum;
        }

        public void setPraiseNum(String praiseNum) {
            this.praiseNum = praiseNum;
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

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }

        public String getViewNum() {
            return viewNum;
        }

        public void setViewNum(String viewNum) {
            this.viewNum = viewNum;
        }
    }
}
