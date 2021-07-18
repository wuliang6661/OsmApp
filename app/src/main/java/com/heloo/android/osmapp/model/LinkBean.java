package com.heloo.android.osmapp.model;

import java.util.List;

public class LinkBean {
    public List<ItemL> itemLS;
    public List<Item> itemS;

    public static class ItemL{
        private String title;
        private String one_id;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getOne_id() {
            return one_id;
        }

        public void setOne_id(String one_id) {
            this.one_id = one_id;
        }
    }

    public static class Item{
        private String one_id;
        private String name;
        private String two_id;
        private List<StoreClassifyBean.ChildBean.ChildListBean> childList;

        public String getOne_id() {
            return one_id;
        }

        public void setOne_id(String one_id) {
            this.one_id = one_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTwo_id() {
            return two_id;
        }

        public void setTwo_id(String two_id) {
            this.two_id = two_id;
        }

        public List<StoreClassifyBean.ChildBean.ChildListBean> getChildList() {
            return childList;
        }

        public void setChildList(List<StoreClassifyBean.ChildBean.ChildListBean> childList) {
            this.childList = childList;
        }
    }


}
