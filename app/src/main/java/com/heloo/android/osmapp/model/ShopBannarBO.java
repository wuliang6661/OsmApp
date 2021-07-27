package com.heloo.android.osmapp.model;

import java.util.List;

public class ShopBannarBO {


    public List<BannerBean> banner;

    public static class BannerBean {
        public String id;
        public Integer bannerType;
        public Integer jumpType;
        public String jumpId;
        public String jumpUrl;
        public String imgurl;
        public String createDate;
        public String modifyDate;
        public String subject;
    }



}
