package com.heloo.android.osmapp.model;

public class ShopDetailsBO {


    public ListBean list;
    public ShopProdExtInfoDOWithBLOBsBean shopProdExtInfoDOWithBLOBs;

    public static class ListBean {
        public String id;
        public String viewId;
        public String prodTypeId;
        public String prodChildTypeId;
        public String name;
        public String subTitle;
        public String totalNum;
        public Integer freeNum;
        public String saleNum;
        public String unitType;
        public String fakeBaseNum;
        public String pv;
        public String uv;
        public String commentNum;
        public String tags;
        public String status;
        public String statusDate;
        public String createUid;
        public String createDate;
        public String modifyDate;
        public String areaId;
        public Double price;
        public Double preferentialPrice;
        public String ticketType;
        public String linkSpots;
        public String pos;
        public String prodCode;
        public String outProdCode;
        public String longitude;
        public String latitude;
        public String linkMan;
        public String linkMobile;
        public String address;
        public String icon;
        public String preDay;
        public String salesDay;
        public String outTypeId;
        public String typeId;
        public String upId;
        public String lvl;
        public String nameType;
        public String description;
        public String shoporderInfoDO;
        public String prodId;
        public String picId;
        public String url;
        public Integer integralPrice;


    }

    public static class ShopProdExtInfoDOWithBLOBsBean {
        public String id;
        public String prodId;
        public String mobileNum;
        public String mobileDays;
        public String idcardNum;
        public String idcardDays;
        public String createDate;
        public String paramVal;
        public String description;
        public String content;
        public String costExplain;
        public String bookedExplain;
        public String othersExplain;
    }
}
