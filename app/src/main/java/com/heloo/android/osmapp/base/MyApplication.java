package com.heloo.android.osmapp.base;

import android.app.Application;
import android.content.Context;

import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.utils.SPUtils;
import com.heloo.android.osmapp.utils.Utils;
import com.tencent.smtt.export.external.TbsCoreSettings;
import com.tencent.smtt.sdk.QbSdk;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.HashMap;

import cn.jpush.android.api.JPushInterface;


/**
 * <p>
 * 程序的application
 */

public class MyApplication extends Application {

    public static ConditionEnum isLogin = ConditionEnum.NOLOGIN;   //默认未登陆
    public static SPUtils spUtils;   //缓存类型
    public static String SESSIONID;    //保存的持久化sessionId，用于H5的session同步
    private static MyApplication instance;

    public static MyApplication getBaseAppContext() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /***初始化工具类*/
        Utils.init(this);
//        WXUtils.registerWX(this);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        UMShareAPI.get(this);
        PlatformConfig.setWeixin(LocalConfiguration.WECHAT_APP_ID, LocalConfiguration.WECHAT_APP_SECRET);
        PlatformConfig.setQQZone(LocalConfiguration.QQ_APP_ID, LocalConfiguration.QQ_APP_SECRET);
        PlatformConfig.setSinaWeibo(LocalConfiguration.SINA_APPKEY, LocalConfiguration.SINA_APPSECRET, "http://sns.whalecloud.com/");
        /**初始化SharedPreferences缓存*/
        spUtils = new SPUtils(LocalConfiguration.SP_NAME);
        instance = this;
        UMShareAPI.get(this);
        UMConfigure.init(this,"5f0bf5e79540fd07a29dabec"
                ,"umeng",UMConfigure.DEVICE_TYPE_PHONE,"");

        // 在调用TBS初始化、创建WebView之前进行如下配置
        HashMap map = new HashMap();
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER, true);
        map.put(TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE, true);
        QbSdk.initTbsSettings(map);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}
