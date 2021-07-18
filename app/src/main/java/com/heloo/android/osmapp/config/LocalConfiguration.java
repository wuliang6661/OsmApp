package com.heloo.android.osmapp.config;


import com.heloo.android.osmapp.model.TitleBean;
import com.heloo.android.osmapp.model.TokenBean;
import com.heloo.android.osmapp.model.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuliang on 2017/3/6.
 * <p>
 * 程序的公共环境配置，或公共字段存放
 */

public class LocalConfiguration {

    //app的SharedPreferences缓存名字
    public static final String SP_NAME = "CINEMA_SP";
    public static String SESSION ="";
    /**
     * 版本下载到手机的位置
     */
    public static final String DOWNLOAD_PATH = "/data/data/com.myp.shcinema/download";
    public static final String appFileName = "cinema.apk";  //版本
    public static int isVoucher = -1;
    public static TokenBean tokenBean;
    public static UserInfo userInfo;//用户信息
    public static List<TitleBean> titleBeanList = new ArrayList<>();
    public static String newsDetailUrl = "/articleInfo/getArticleInfo";
    public static String videoUrl = "/articleInfo/getvideolist";
    public static String rewordUrl = "/draw/todraw";
    public static String signUrl = "/signin/tosignin";
    public static String liveUrl = "/picarticlelive/getpicarticlelive";


    public static final String QQ_APP_ID = "1110578145";
    public static final String QQ_APP_SECRET = "8koVnO62RJqCsVbB";
    public static final String WECHAT_APP_ID = "wxe579d07fde00bf3f";
    public static final String WECHAT_APP_SECRET = "6cfd5c792ffba447bd2ce0fafb1badd4";
    public static final String SINA_APPKEY = "2973785456";
    public static final String SINA_APPSECRET = "3c171b0ceccb8abaae268b1b758f5be2";

}
