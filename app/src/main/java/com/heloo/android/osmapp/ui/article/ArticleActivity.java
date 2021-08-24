package com.heloo.android.osmapp.ui.article;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityArticleBinding;
import com.heloo.android.osmapp.model.HotArticleBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.ArticleContract;
import com.heloo.android.osmapp.mvp.presenter.ArticlePresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.StringUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.shts.android.library.TriangleLabelView;
import okhttp3.ResponseBody;

/**
 * 热门文章
 */
public class ArticleActivity extends MVPBaseActivity<ArticleContract.View, ArticlePresenter, ActivityArticleBinding>
        implements ArticleContract.View, View.OnClickListener {

    private CommonAdapter<HotArticleBean> adapter;
    private List<HotArticleBean> data = new ArrayList<>();
    private String type = "1";//1当日,2本周,3本月,4本年

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        mPresenter.getData(MyApplication.spUtils.getString("token", ""), type);
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        goBack();
        viewBinding.todayBtn.setOnClickListener(this);
        viewBinding.weekBtn.setOnClickListener(this);
        viewBinding.monthBtn.setOnClickListener(this);
        viewBinding.yearBtn.setOnClickListener(this);
        viewBinding.list.setLayoutManager(new LinearLayoutManager(this));

        viewBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getData(MyApplication.spUtils.getString("token", ""), type);
                refreshLayout.finishRefresh(1000);
            }
        });
    }

    private void setAdapter() {
        adapter = new CommonAdapter<HotArticleBean>(this, R.layout.article_item_layout, data) {
            @Override
            protected void convert(ViewHolder holder, HotArticleBean item, int position) {
                ImageView articleImg = holder.getConvertView().findViewById(R.id.articleImg);
                TriangleLabelView labeled = holder.getView(R.id.labeled);
                if (!StringUtils.isEmpty(item.getIcon())) {
                    if (item.getIcon().startsWith("http")) {
                        Glide.with(ArticleActivity.this).load(item.getIcon()).into(articleImg);
                    } else {
                        Glide.with(ArticleActivity.this).load(HttpInterface.IMG_URL + item.getIcon()).into(articleImg);
                    }
                } else {
                    Glide.with(ArticleActivity.this).load(item.getIcon()).error(R.drawable.glide_ploce).into(articleImg);
                }
                switch (position) {
                    case 0:
                        labeled.setTriangleBackgroundColor(Color.parseColor("#FF0404"));
                        break;
                    case 1:
                        labeled.setTriangleBackgroundColor(Color.parseColor("#FE9602"));
                        break;
                    case 2:
                        labeled.setTriangleBackgroundColor(Color.parseColor("#FFD155"));
                        break;
                    default:
                        labeled.setTriangleBackgroundColor(Color.parseColor("#C2C2C2"));
                        break;
                }
                if (position > 9) {
                    labeled.setVisibility(View.GONE);
                } else {
                    labeled.setVisibility(View.VISIBLE);
                }
                labeled.setSecondaryText("NO." + (position + 1));
                holder.setText(R.id.title, item.getSubject());
                holder.setText(R.id.time, item.getCreateDate());
                holder.setText(R.id.number, item.getHeatsumber());
                holder.getView(R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ArticleActivity.this, WebViewActivity.class);
                        if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                            intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId()
                                    + "&uid=" + LocalConfiguration.userInfo.getUid() + "&username=" + LocalConfiguration.userInfo.getUsername() + "&app=1");
                        } else {
                            intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId() + "&app=1");
                        }
                        startActivity(intent);
                    }
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
    }

    @Override
    public void onRequestEnd() {
        stopProgress();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.todayBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.today_article_yes_bg, null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_no_bg, null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_no_bg, null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.year_article_no_bg, null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#FFFFFF"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#D8AE54"));
                type = "1";
                mPresenter.getData(MyApplication.spUtils.getString("token", ""), type);
                break;
            case R.id.weekBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.today_article_no_bg, null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_yes_bg, null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_no_bg, null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.year_article_no_bg, null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#ffffff"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#D8AE54"));
                type = "2";
                mPresenter.getData(MyApplication.spUtils.getString("token", ""), type);
                break;
            case R.id.monthBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.today_article_no_bg, null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_no_bg, null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_yes_bg, null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.year_article_no_bg, null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#ffffff"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#D8AE54"));
                type = "3";
                mPresenter.getData(MyApplication.spUtils.getString("token", ""), type);
                break;
            case R.id.yearBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.today_article_no_bg, null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_no_bg, null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.week_article_no_bg, null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.year_article_yes_bg, null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#ffffff"));
                type = "4";
                mPresenter.getData(MyApplication.spUtils.getString("token", ""), type);
                break;
        }
    }

    @Override
    public void getData(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")) {
            JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
            data.clear();
            data.addAll(JSON.parseArray(jsonObject1.optString("list"), HotArticleBean.class));
            setAdapter();
        }
    }
}