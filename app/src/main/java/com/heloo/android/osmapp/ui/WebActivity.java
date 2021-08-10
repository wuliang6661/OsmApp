package com.heloo.android.osmapp.ui;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;

import com.heloo.android.osmapp.base.BaseWebActivity;
import com.heloo.android.osmapp.databinding.ActWebBinding;
import com.heloo.android.osmapp.utils.HtmlFormat;

import androidx.annotation.Nullable;

public class WebActivity extends BaseWebActivity {

    private ActWebBinding binding;


    String url;
    AudioManager mAudioManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActWebBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        goBack();
        setHeader();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        url = getIntent().getExtras().getString("url");
        String mTitle = getIntent().getStringExtra("title");
        setTitle(mTitle);
        if (getIntent().getStringExtra("tag") != null) {
            initWebView(binding.webView, 1);
            binding.webView.getSettings().setTextZoom(100);
            binding.webView.loadDataWithBaseURL(null, HtmlFormat.getNewContent(url), "text/html", "utf-8", null);//加载html数据
        } else {
            initWebView(binding.webView, 2);
            binding.webView.loadUrl(url);
        }
    }

}
