package com.heloo.android.osmapp.utils;

import com.heloo.android.osmapp.api.HttpInterface;

public class HttpImgUtils {


    public static String getImgUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        if (!url.startsWith("http")) {
            return HttpInterface.IMG_URL + url;
        }
        return url;
    }


}
