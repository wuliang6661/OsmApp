package com.heloo.android.osmapp.model;

import java.util.List;

public class OrderBO {


    public String id;
    public String orderNo;
    public Double totalPrice;
    public Double discountFee;
    public Integer discountnumber;
    public Double totalFee;
    public Double payFee;
    public String status;
    public Integer createUid;
    public String createDate;
    public Integer goodsNumber;
    public Integer integralPrice;

    public List<OrderItemlistBean> orderItemlist;
    public Integer discounttotalFee;
    public AddressBean umsUserAddressDO;
    public MyAdressBean sincePointDO;
    public Integer distribution;
    public String addressId;

    public static class OrderItemlistBean {
        public Integer prodNum;
        public String spec;
        public String goosId;
        public Double prize;
        public Double discountPrice;
        public Double totalPrice;
        public String name;
        public String icon;
        public Integer integralPrice;
        public Integer discountnumber;
    }

}
