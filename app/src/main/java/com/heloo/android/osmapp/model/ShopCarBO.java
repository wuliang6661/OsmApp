package com.heloo.android.osmapp.model;

import java.io.Serializable;
import java.util.List;

public class ShopCarBO implements Serializable {


    public String umsMemberUserInfoDO;
    public List<ShopCarInfoDOSBean> shopCarInfoDOS;

    public static class ShopCarInfoDOSBean implements Serializable {
        public String id;
        public String userId;
        public String userName;
        public String goodsId;
        public String goodsName;
        public Double goodsPrice;
        public Integer goodsNum;
        public String goodsImg;
        public Double coinsNum;
        public String createTime;
        public String updateTime;
        public String status;
    }
}
