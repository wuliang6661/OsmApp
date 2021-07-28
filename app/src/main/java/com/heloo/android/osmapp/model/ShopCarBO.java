package com.heloo.android.osmapp.model;

import java.util.List;

public class ShopCarBO {


    public String umsMemberUserInfoDO;
    public List<ShopCarInfoDOSBean> shopCarInfoDOS;

    public static class ShopCarInfoDOSBean {
        public String id;
        public String userId;
        public String userName;
        public String goodsId;
        public String goodsName;
        public Double goodsPrice;
        public Integer goodsNum;
        public String goodsImg;
        public Integer coinsNum;
        public String createTime;
        public String updateTime;
        public String status;
    }
}
