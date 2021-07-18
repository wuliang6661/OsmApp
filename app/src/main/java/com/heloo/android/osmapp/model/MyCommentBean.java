package com.heloo.android.osmapp.model;

/**
 * Created by Witness on 2020-03-20
 * Describe:
 */
public class MyCommentBean {

    private String id;
    private String postId;
    private int uid;
    private String name;
    private String word;
    private int category;
    private String pId;
    private String pUid;
    private String pName;
    private String replyId;
    private String replyUid;
    private String replyName;
    private String createDate;
    private String modifyDate;
    private String createTime;
    private String postCommentVoList;
    private PostInfoModel postInfoModel;
    private int valid;
    private String header;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getPId() {
        return pId;
    }

    public void setPId(String pId) {
        this.pId = pId;
    }

    public String getPUid() {
        return pUid;
    }

    public void setPUid(String pUid) {
        this.pUid = pUid;
    }

    public String getPName() {
        return pName;
    }

    public void setPName(String pName) {
        this.pName = pName;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyUid() {
        return replyUid;
    }

    public void setReplyUid(String replyUid) {
        this.replyUid = replyUid;
    }

    public String getReplyName() {
        return replyName;
    }

    public void setReplyName(String replyName) {
        this.replyName = replyName;
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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getPostCommentVoList() {
        return postCommentVoList;
    }

    public void setPostCommentVoList(String postCommentVoList) {
        this.postCommentVoList = postCommentVoList;
    }

    public PostInfoModel getPostInfoModel() {
        return postInfoModel;
    }

    public void setPostInfoModel(PostInfoModel postInfoModel) {
        this.postInfoModel = postInfoModel;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public static class PostInfoModel {

        private String id;
        private Object title;
        private String descr;
        private String topicId;
        private String topicName;
        private int status;
        private String pictures;
        private String createDate;
        private String modifyDate;
        private String createTime;
        private String postPraiseVoList;
        private String commentNum;
        private String pointNum;
        private String userName;
        private String uid;
        private String header;
        private String picList;
        private String isPraise;
        private String isComment;
        private String valid;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getTitle() {
            return title;
        }

        public void setTitle(Object title) {
            this.title = title;
        }

        public String getDescr() {
            return descr;
        }

        public void setDescr(String descr) {
            this.descr = descr;
        }

        public String getTopicId() {
            return topicId;
        }

        public void setTopicId(String topicId) {
            this.topicId = topicId;
        }

        public String getTopicName() {
            return topicName;
        }

        public void setTopicName(String topicName) {
            this.topicName = topicName;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getPictures() {
            return pictures;
        }

        public void setPictures(String pictures) {
            this.pictures = pictures;
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

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getPostPraiseVoList() {
            return postPraiseVoList;
        }

        public void setPostPraiseVoList(String postPraiseVoList) {
            this.postPraiseVoList = postPraiseVoList;
        }

        public String getCommentNum() {
            return commentNum;
        }

        public void setCommentNum(String commentNum) {
            this.commentNum = commentNum;
        }

        public String getPointNum() {
            return pointNum;
        }

        public void setPointNum(String pointNum) {
            this.pointNum = pointNum;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public String getPicList() {
            return picList;
        }

        public void setPicList(String picList) {
            this.picList = picList;
        }

        public String getIsPraise() {
            return isPraise;
        }

        public void setIsPraise(String isPraise) {
            this.isPraise = isPraise;
        }

        public String getIsComment() {
            return isComment;
        }

        public void setIsComment(String isComment) {
            this.isComment = isComment;
        }

        public String getValid() {
            return valid;
        }

        public void setValid(String valid) {
            this.valid = valid;
        }
    }
}
