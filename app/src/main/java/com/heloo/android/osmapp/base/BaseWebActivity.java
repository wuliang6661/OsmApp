package com.heloo.android.osmapp.base;

import android.os.Build;
import android.os.Bundle;
import android.webkit.WebSettings;

import androidx.annotation.Nullable;

import com.heloo.android.osmapp.utils.webview.WebAppInterface;
import com.heloo.android.osmapp.utils.webview.WebClient;
import com.heloo.android.osmapp.utils.webview.WebViewChromeClient;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;


/**
 * Created by witness on 2017/4/11.
 * <p>
 * 初始化webview的activity父类
 */

public abstract class BaseWebActivity extends BaseActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * 初始化webview的实例
     */
    public void initWebView(WebView view, int tag) {
        initWebViewSettings(view);
        WebViewChromeClient chromeClient = new WebViewChromeClient(this);
        chromeClient.setOnReceivedListener(new WebViewChromeClient.onReceivedMessage() {   //设置标题
            @Override
            public void getTitle(String title) {
                if (tag == 2) {
                    setTitle(title);
                }
            }
        });
        view.setWebChromeClient(chromeClient);
        view.setWebViewClient(new WebClient(this));
        view.addJavascriptInterface(new WebAppInterface(this, view), "Android");
//        view.setClickable(true);
        view.setHorizontalScrollBarEnabled(false);
        view.setVerticalScrollBarEnabled(false);
        //下面方法去掉
//        IX5WebViewExtension ix5 = view.getX5WebViewExtension();
//        if (null != ix5) {
//            ix5.setScrollBarFadingEnabled(false);
//        }
    }

    /**
     * 初始化webview的各种属性
     */
    private void initWebViewSettings(WebView mWebView) {
        com.tencent.smtt.sdk.WebSettings settings = mWebView.getSettings();
        //支持获取手势焦点
        mWebView.requestFocusFromTouch();
        //支持Js
        settings.setJavaScriptEnabled(true);
        //设置适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        //支持缩放
        settings.setSupportZoom(true);
//设置出现缩放工具
        settings.setBuiltInZoomControls(true);
//设定缩放控件隐藏
        settings.setDisplayZoomControls(false);

        //隐藏原生的缩放控件
        settings.setDisplayZoomControls(false);
        settings.setSupportMultipleWindows(true);
        settings.supportMultipleWindows();
        //设置缓存模式
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setCacheMode(android.webkit.WebSettings.LOAD_DEFAULT);
        settings.setAppCacheEnabled(true);
        settings.setAppCachePath(mWebView.getContext().getCacheDir().getAbsolutePath());
        //设置可访问文件
        settings.setAllowFileAccess(true);
        //当webView调用requestFocus时为webview设置节点
        settings.setNeedInitialFocus(true);
        //设置支持自动加载图片
        if (Build.VERSION.SDK_INT >= 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
        }
        //设置编码格式
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setAppCachePath(this.getDir("appcache", 0).getPath());
        settings.setDatabasePath(this.getDir("databases", 0).getPath());
        settings.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }
}
