package com.heloo.android.osmapp.ui.main.nice;

import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FragmentVideoBinding;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.VideoContract;
import com.heloo.android.osmapp.mvp.presenter.VideoPresenter;
import com.heloo.android.osmapp.utils.webview.WebAppInterface;
import com.heloo.android.osmapp.utils.webview.WebClient;
import com.heloo.android.osmapp.utils.webview.WebViewChromeClient;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

/**
 * 视频
 */
public class VideoFragment extends MVPBaseFragment<VideoContract.View, VideoPresenter, FragmentVideoBinding>{

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        initWebView(viewBinding.videoWebView);
        viewBinding.videoWebView.getSettings().setTextZoom(100);
        if (LocalConfiguration.userInfo != null) {
            viewBinding.videoWebView.loadUrl(HttpInterface.URLS + LocalConfiguration.videoUrl
                    + "?uid=" + LocalConfiguration.userInfo.getUid() + "&username=" + LocalConfiguration.userInfo.getUsername() + "&app=1");
        }else {
            viewBinding.videoWebView.loadUrl(HttpInterface.URLS + LocalConfiguration.videoUrl
                    + "?uid=" + "" + "&username=" + "" + "&app=1");
        }
    }


    /**
     * 初始化webview的实例
     */
    public void initWebView(WebView view) {
        initWebViewSettings(view);
        WebViewChromeClient chromeClient = new WebViewChromeClient(getActivity());
        chromeClient.setOnReceivedListener(new WebViewChromeClient.onReceivedMessage() {   //设置标题
            @Override
            public void getTitle(String title) {

            }
        });
        view.setWebChromeClient(chromeClient);
        view.setWebViewClient(new WebClient(getActivity()));
        view.addJavascriptInterface(new WebAppInterface(getActivity(), view), "Android");
        view.setClickable(true);
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
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setSupportZoom(true);
        webSetting.setTextZoom(100);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUserAgentString("oushiman-android");
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
        webSetting.setAppCachePath(getActivity().getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(getActivity().getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(getActivity().getDir("geolocation", 0)
                .getPath());
        CookieSyncManager.createInstance(getActivity());
        CookieSyncManager.getInstance().sync();
    }


}