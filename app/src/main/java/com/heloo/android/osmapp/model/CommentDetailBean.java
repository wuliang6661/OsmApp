package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 2020-03-24
 * Describe:
 */
public class CommentDetailBean {

    private PostInfoModelBean postInfoModel;
    private PostCommentModelBean postCommentModel;
    private List<PostCommentModelListBean> postCommentModelList;

    public PostInfoModelBean getPostInfoModel() {
        return postInfoModel;
    }

    public void setPostInfoModel(PostInfoModelBean postInfoModel) {
        this.postInfoModel = postInfoModel;
    }

    public PostCommentModelBean getPostCommentModel() {
        return postCommentModel;
    }

    public void setPostCommentModel(PostCommentModelBean postCommentModel) {
        this.postCommentModel = postCommentModel;
    }

    public List<PostCommentModelListBean> getPostCommentModelList() {
        return postCommentModelList;
    }

    public void setPostCommentModelList(List<PostCommentModelListBean> postCommentModelList) {
        this.postCommentModelList = postCommentModelList;
    }

    public static class PostInfoModelBean {

        private String id;
        private String title;
        private String descr;
        private String topicId;
        private String topicName;
        private String status;
        private String pictures;
        private String createDate;
        private String modifyDate;
        private String createTime;
        private String postPraiseModelList;
        private String commentNum;
        private String pointNum;
        private String userName;
        private String uid;
        private String header;
        private String isPraise;
        private String isComment;
        private int valid;
        private int anonymous;
        private List<String> picList;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
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

        public String getPostPraiseModelList() {
            return postPraiseModelList;
        }

        public void setPostPraiseModelList(String postPraiseModelList) {
            this.postPraiseModelList = postPraiseModelList;
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

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }

        public int getAnonymous() {
            return anonymous;
        }

        public void setAnonymous(int anonymous) {
            this.anonymous = anonymous;
        }

        public List<String> getPicList() {
            return picList;
        }

        public void setPicList(List<String> picList) {
            this.picList = picList;
        }
    }

    public static class PostCommentModelBean {

        private String id;
        private String postId;
        private String uid;
        private String name;
        private String word;
        private int category;
        private String replyId;
        private String replyUid;
        private String replyName;
        private String createDate;
        private String modifyDate;
        private String createTime;
        private String postCommentModelList;
        private String postInfoModel;
        private String header;
        private int valid;
        private String pname;
        private String pid;
        private String puid;

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

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
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

        public String getPostCommentModelList() {
            return postCommentModelList;
        }

        public void setPostCommentModelList(String postCommentModelList) {
            this.postCommentModelList = postCommentModelList;
        }

        public String getPostInfoModel() {
            return postInfoModel;
        }

        public void setPostInfoModel(String postInfoModel) {
            this.postInfoModel = postInfoModel;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getPuid() {
            return puid;
        }

        public void setPuid(String puid) {
            this.puid = puid;
        }
    }

    public static class PostCommentModelListBean {

        private String id;
        private String postId;
        private String uid;
        private String name;
        private String word;
        private int category;
        private String replyId;
        private String replyUid;
        private String replyName;
        private String createDate;
        private String modifyDate;
        private String createTime;
        private String postCommentModelList;
        private String postInfoModel;
        private String header;
        private int valid;
        private String pname;
        private String pid;
        private String puid;

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

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
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

        public String getPostCommentModelList() {
            return postCommentModelList;
        }

        public void setPostCommentModelList(String postCommentModelList) {
            this.postCommentModelList = postCommentModelList;
        }

        public String getPostInfoModel() {
            return postInfoModel;
        }

        public void setPostInfoModel(String postInfoModel) {
            this.postInfoModel = postInfoModel;
        }

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        public int getValid() {
            return valid;
        }

        public void setValid(int valid) {
            this.valid = valid;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getPuid() {
            return puid;
        }

        public void setPuid(String puid) {
            this.puid = puid;
        }
    }
}
