package com.heloo.android.osmapp.model;

public class SignBO {


    public String msg;
    public String date;
    public String code;

    public boolean isSourcess() {
        return "success".equals(code);
    }
}
