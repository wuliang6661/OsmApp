package com.heloo.android.osmapp.ui;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.annotation.Nullable;

import com.heloo.android.osmapp.base.BaseWebActivity;
import com.heloo.android.osmapp.databinding.WebviewLayoutBinding;
import com.heloo.android.osmapp.utils.BubbleUtils;

import java.util.HashMap;

/**
 * <p>
 * 跳转H5的界面
 */

public class WebViewActivity extends BaseWebActivity {

    String url;
    AudioManager mAudioManager;

    private HashMap<String, String> extraHeaders;
    private String mTitle = "";
    private WebviewLayoutBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = WebviewLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        setListener();
        binding.headLayout2.post(() -> binding.headLayout2.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        url = getIntent().getExtras().getString("url");
        mTitle = getIntent().getStringExtra("title");
        setTitle(mTitle);
        if (getIntent().getStringExtra("tag") != null){
            initWebView(binding.webview,1);
            binding.webview.getSettings().setTextZoom(100);
            binding.webview.loadDataWithBaseURL(null, getHtmlData(url), "text/html", "utf-8", null);//加载html数据
        }else {
            initWebView(binding.webview,2);
            extraHeaders = new HashMap<String, String>();
//            extraHeaders.put("accessType", "app_android");
            binding.webview.loadUrl(url);
        }
    }


    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:100%; height:auto;}*{margin:0px;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }


    /**
     * 如果网页还有上一层，则返回上一层网页
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && binding.webview.canGoBack()) {
            String back = getIntent().getExtras().getString("back");
            if (back == null) {
                binding.webview.goBack();
                return true;
            } else {

            }


        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (binding.webview != null) {

            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = binding.webview.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(binding.webview);
            }

            binding.webview.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            binding.webview.getSettings().setJavaScriptEnabled(false);
            binding.webview.clearHistory();
            binding.webview.clearView();
            binding.webview.removeAllViews();
            binding.webview.destroy();

        }
        super.onDestroy();
    }

    private void setListener(){
        binding.backBtn.setOnClickListener(v -> {
            if (binding.webview.canGoBack()) {
                String back = getIntent().getExtras().getString("back");
                if (back == null) {
                    binding.webview.goBack();

                } else {
                    finish();
                }
            } else {
                finish();
            }
        });
//        goBack();
    }
}
