package com.heloo.android.osmapp.model;

public class VersionBO {


    /**
     * latestVersion : 2
     * downloadUrl : https://shjz.yingjin.pro/api/upload/app/app-debug.apk
     * isForceUpdate : 0
     * content : 新功能上线
     */

    //最新的版本号
    private String latestVersion;
    //apk下载地址
    private String downloadUrl;
    //是否强制更新
    private int isForceUpdate;
    //更新提示
    private String content;

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public int getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(int isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
