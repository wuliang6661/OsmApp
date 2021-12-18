package com.heloo.android.osmapp.model;

import android.os.Parcel;
import android.os.Parcelable;

public class MyAdressBean implements Parcelable {


    public Integer id;
    public String pointName;
    public String address;
    public String pointTime;

    protected MyAdressBean(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        pointName = in.readString();
        address = in.readString();
        pointTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(pointName);
        dest.writeString(address);
        dest.writeString(pointTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MyAdressBean> CREATOR = new Creator<MyAdressBean>() {
        @Override
        public MyAdressBean createFromParcel(Parcel in) {
            return new MyAdressBean(in);
        }

        @Override
        public MyAdressBean[] newArray(int size) {
            return new MyAdressBean[size];
        }
    };
}
