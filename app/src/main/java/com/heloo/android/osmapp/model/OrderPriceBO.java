package com.heloo.android.osmapp.model;

import java.util.List;

public class OrderPriceBO {


    public List<ShopOrderModelsBean> shopOrderModels;
    public Double totalPrice;
    public Integer totalScore;
    public Double totalDiscountPrice;

    public static class ShopOrderModelsBean {
        public String id;
        public String name;
        public String icon;
        public Integer number;
        public Double discountprice;
        public Double integralprice;
    }
}
