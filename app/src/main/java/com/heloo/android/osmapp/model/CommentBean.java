package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 2020-03-18
 * Describe:
 */
public class CommentBean {
    private String id;
    private String postId;
    private String uid;
    private String name;
    private String word;
    private String category;
    private String pId;
    private String pUid;
    private String pName;
    private String replyId;
    private String replyUid;
    private String replyName;
    private String createDate;
    private String modifyDate;
    private String createTime;
    private List<CommentBean> postCommentModelList;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
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

    public List<CommentBean> getPostCommentModelList() {
        return postCommentModelList;
    }

    public void setPostCommentModelList(List<CommentBean> postCommentModelList) {
        this.postCommentModelList = postCommentModelList;
    }
}
