package com.heloo.android.osmapp.model;

import java.util.List;

/**
 * Created by Witness on 5/27/21
 * Describe:
 */
public class StoreClassifyBean {

    private String one_id;
    private String name;
    private List<ChildBean> child;

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

    public List<ChildBean> getChild() {
        return child;
    }

    public void setChild(List<ChildBean> child) {
        this.child = child;
    }

    public static class ChildBean {

        private String one_id;
        private String name;
        private String two_id;
        private List<ChildListBean> childList;

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

        public List<ChildListBean> getChildList() {
            return childList;
        }

        public void setChildList(List<ChildListBean> childList) {
            this.childList = childList;
        }

        public static class ChildListBean {

            private String icon;
            private String one_id;
            private String price;
            private String name;
            private String id;
            private String two_id;

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }

            public String getOne_id() {
                return one_id;
            }

            public void setOne_id(String one_id) {
                this.one_id = one_id;
            }

            public String getPrice() {
                return price;
            }

            public void setPrice(String price) {
                this.price = price;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getTwo_id() {
                return two_id;
            }

            public void setTwo_id(String two_id) {
                this.two_id = two_id;
            }
        }
    }
}
