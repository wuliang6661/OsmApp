package com.heloo.android.osmapp.model;

import java.util.List;

public class ShopListBO {


    public List<ListBean> list;

    public static class ListBean {
        public String name;
        public List<TypeGoodsMoelsBean> typeGoodsMoels;
        public String goodsMoels;

        public static class TypeGoodsMoelsBean {
            public String name;
            public List<GoodsMoelsBean> goodsMoels;

            public static class GoodsMoelsBean {
                public String id;
                public String name;
                public String icon;
                public Integer preferentialPrice;
                public Integer integralPrice;
            }
        }
    }
}
