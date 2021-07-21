package com.heloo.android.osmapp.ui.main.nice;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FragmentVideoBinding;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.VideoContract;
import com.heloo.android.osmapp.mvp.presenter.VideoPresenter;
import com.heloo.android.osmapp.utils.webview.WebAppInterface;
import com.heloo.android.osmapp.utils.webview.WebClient;
import com.heloo.android.osmapp.utils.webview.WebViewChromeClient;
import com.heloo.android.osmapp.utils.webview.X5ChromeClient;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

import static android.app.Activity.RESULT_OK;

/**
 * 视频
 */
public class VideoFragment extends MVPBaseFragment<VideoContract.View, VideoPresenter, FragmentVideoBinding>{

    private X5ChromeClient chromeClient;

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
         chromeClient = new X5ChromeClient(getActivity());
        chromeClient.setData(viewBinding.webLayout, viewBinding.videoWebView);
        chromeClient.setOnReceivedListener(new X5ChromeClient.onReceivedMessage() {   //设置标题
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


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == X5ChromeClient.REQUEST_CODE) {
            // 经过上边(1)、(2)两个赋值操作，此处即可根据其值是否为空来决定采用哪种处理方法
            if (chromeClient.mUploadMessage != null) {
                chromeClient.chooseBelow(resultCode, data);
            } else if (chromeClient.mUploadCallbackAboveL != null) {
                chromeClient.chooseAbove(resultCode, data);
            } else {
            }
        } else if (requestCode == X5ChromeClient.VIDEO_REQUEST) {
            if (null == chromeClient.mUploadMessage && null == chromeClient.mUploadCallbackAboveL)
                return;

            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (chromeClient.mUploadCallbackAboveL != null) {
                if (resultCode == RESULT_OK) {
                    chromeClient.mUploadCallbackAboveL.onReceiveValue(new Uri[]{result});
                    chromeClient.mUploadCallbackAboveL = null;
                } else {
                    chromeClient.mUploadCallbackAboveL.onReceiveValue(new Uri[]{});
                    chromeClient.mUploadCallbackAboveL = null;
                }

            } else if (chromeClient.mUploadMessage != null) {
                if (resultCode == RESULT_OK) {
                    chromeClient.mUploadMessage.onReceiveValue(result);
                    chromeClient.mUploadMessage = null;
                } else {
                    chromeClient.mUploadMessage.onReceiveValue(Uri.EMPTY);
                    chromeClient.mUploadMessage = null;
                }

            }
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        viewBinding.videoWebView.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
        viewBinding.videoWebView.onPause();
    }


    @Override
    public void onDestroy() {
        try {
            if (viewBinding.videoWebView != null) {
//                webView.stopLoading();
//                webView.removeAllViewsInLayout();
//                webView.removeAllViews();
//                webView.setWebViewClient(null);
//                CookieSyncManager.createInstance(this).stopSync();
                viewBinding.videoWebView.destroy();
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            super.onDestroy();
        }
    }


    /**
     * 如果网页还有上一层，则返回上一层网页
     *
     * @param keyCode
     * @param event
     * @return
     */
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        switch (keyCode) {
//            case KeyEvent.KEYCODE_BACK:
//                /** 回退键 事件处理 优先级:视频播放全屏-网页回退-关闭页面 */
//                if (chromeClient.customView != null) {
//                    chromeClient.onHideCustomView();
//                } else if (viewBinding.videoWebView.canGoBack()) {
//                    viewBinding.videoWebView.goBack();
//                }
//                return true;
//        }
//    }

}