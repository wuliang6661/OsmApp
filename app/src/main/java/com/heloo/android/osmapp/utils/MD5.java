package com.heloo.android.osmapp.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 作者 by witness 时间 16/11/25.
 */

public class MD5 {
    public static String strToMd5Low32(String str) {
        StringBuilder builder = new StringBuilder();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(str.getBytes());
            byte[] bytes = md5.digest();
            for (byte b : bytes) {
                int digital = b & 0xff;
                if (digital < 16)
                    builder.append(0);
                builder.append(Integer.toHexString(digital));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return builder.toString().toLowerCase();
    }

    /**
     * 接口参数mac生成（签名）
     * @param apiName
     * @return
     */
    public static String sign(String apiName, String time){
        String sign = "jzkj20200401zhuanzhuanzhu" + apiName + "a2dfaebaed54d63aa6b4ee0a5f106210" + time;
        return strToMd5Low32(sign);
    }
}
