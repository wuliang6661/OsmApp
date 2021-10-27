package com.heloo.android.osmapp.model;

public class VersionBO {


    public AndroidDTO android;
    public String updateMessage;
    public String apple;
    public Integer show;
    public Integer isForce;

    public static class AndroidDTO {
        public Integer versionCode;
        public String versionName;
        public String updateMessage;
        public Integer minVersionCode;
        public String minVersionName;
        public String url;
    }
}
