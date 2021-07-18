package com.heloo.android.osmapp.ui.hero;


import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivityHeroBinding;
import com.heloo.android.osmapp.model.HeroBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.HeroContract;
import com.heloo.android.osmapp.mvp.presenter.HeroPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 英雄榜
 */
public class HeroActivity extends MVPBaseActivity<HeroContract.View, HeroPresenter, ActivityHeroBinding>
    implements HeroContract.View, View.OnClickListener {

    private CommonAdapter<HeroBean> adapter;
    private List<HeroBean> data = new ArrayList<>();
    private String type="1";// 1当日,2本周,3本月,4本年

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        showProgress("");
        mPresenter.getData(MyApplication.spUtils.getString("token", ""),type);
    }


    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        goBack();
        viewBinding.todayBtn.setOnClickListener(this);
        viewBinding.weekBtn.setOnClickListener(this);
        viewBinding.monthBtn.setOnClickListener(this);
        viewBinding.yearBtn.setOnClickListener(this);
        viewBinding.list.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();
        viewBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mPresenter.getData(MyApplication.spUtils.getString("token", ""),type);
                refreshLayout.finishRefresh(1000);
            }
        });
    }


    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<HeroBean>(this,R.layout.hero_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, HeroBean item, int position) {
                ImageView orderByImg = holder.getConvertView().findViewById(R.id.orderByImg);
                ShapeableImageView headerImg = holder.getConvertView().findViewById(R.id.headerImg);
                if (position == 0){
                    orderByImg.setVisibility(View.VISIBLE);
                    holder.getView(R.id.orderByTxt).setVisibility(View.INVISIBLE);
                    orderByImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.first,null));
                }else if (position == 1){
                    orderByImg.setVisibility(View.VISIBLE);
                    holder.getView(R.id.orderByTxt).setVisibility(View.INVISIBLE);
                    orderByImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.second,null));
                }else if (position == 2){
                    orderByImg.setVisibility(View.VISIBLE);
                    holder.getView(R.id.orderByTxt).setVisibility(View.INVISIBLE);
                    orderByImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.third,null));
                }else {
                    holder.getView(R.id.orderByTxt).setVisibility(View.VISIBLE);
                    orderByImg.setVisibility(View.INVISIBLE);
                    holder.setText(R.id.orderByTxt,String.valueOf(position + 1));
                }
                Glide.with(HeroActivity.this).load(item.getIcon()).error(R.drawable.default_head).into(headerImg);
                if (item.getNickname() != null && !item.getNickname().equals("")) {
                    holder.setText(R.id.name, item.getNickname());
                }else {
                    holder.setText(R.id.name, item.getRealName());
                }
                holder.setText(R.id.dep,item.getDepartment());
                holder.setText(R.id.number,item.getScorenumber());
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
        switch (v.getId()){
            case R.id.todayBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.today_article_yes_bg,null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_no_bg,null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_no_bg,null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.year_article_no_bg,null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#FFFFFF"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#D8AE54"));
                type = "1";
                showProgress("");
                mPresenter.getData(MyApplication.spUtils.getString("token", ""),type);
                break;
            case R.id.weekBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.today_article_no_bg,null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_yes_bg,null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_no_bg,null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.year_article_no_bg,null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#ffffff"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#D8AE54"));
                type = "2";
                showProgress("");
                mPresenter.getData(MyApplication.spUtils.getString("token", ""),type);
                break;
            case R.id.monthBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.today_article_no_bg,null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_no_bg,null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_yes_bg,null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.year_article_no_bg,null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#ffffff"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#D8AE54"));
                type = "3";
                showProgress("");
                mPresenter.getData(MyApplication.spUtils.getString("token", ""),type);
                break;
            case R.id.yearBtn:
                viewBinding.todayBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.today_article_no_bg,null));
                viewBinding.weekBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_no_bg,null));
                viewBinding.monthBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.week_article_no_bg,null));
                viewBinding.yearBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.year_article_yes_bg,null));
                viewBinding.todayBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.weekBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.monthBtn.setTextColor(Color.parseColor("#D8AE54"));
                viewBinding.yearBtn.setTextColor(Color.parseColor("#ffffff"));
                type = "4";
                showProgress("");
                mPresenter.getData(MyApplication.spUtils.getString("token", ""),type);
                break;
        }
    }

    @Override
    public void getData(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
            data.clear();
            data.addAll(JSON.parseArray(jsonObject1.optString("list"),HeroBean.class));
            setAdapter();
        }
    }
}