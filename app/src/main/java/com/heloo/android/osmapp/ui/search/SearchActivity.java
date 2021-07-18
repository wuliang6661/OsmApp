package com.heloo.android.osmapp.ui.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivitySearchBinding;
import com.heloo.android.osmapp.databinding.ActivitySettingBinding;
import com.heloo.android.osmapp.model.ArticleBean;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Description : SearchActivity
 *
 * @author WITNESS
 * @date 7/11/21
 */


public class SearchActivity extends BaseActivity {

    private ActivitySearchBinding binding;
    private int pageNo = 1;
    private int pageSize = 20;
    private CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean> adapter;
    private ArticleBean articleBean;
    private List<ArticleBean.ArticleInfoListBean.DataBean> newsData = new ArrayList<>();
    private com.zhy.adapter.recyclerview.CommonAdapter<String> historyAdapter;
    private ArrayList<String> historyData = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        setHeader();
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        binding.historyList.setLayoutManager(linearLayoutManager);
        if (MyApplication.spUtils.getStringSet("HomeHistoryData") != null) {
            historyData.addAll(new ArrayList<>(MyApplication.spUtils.getStringSet("HomeHistoryData")));
        }
        setHistoryAdapter();
        binding.searchView.isIconified();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showProgress("");
                historyData.add(query);
                MyApplication.spUtils.put("HomeHistoryData",new HashSet<>(historyData));
                getData(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });
        binding.cancel.setOnClickListener(v -> finish());
    }

    private void getData(String keyWord){
        HttpInterfaceIml.getArticleList(MyApplication.spUtils.getString("token", ""),pageNo,pageSize,"","",keyWord).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgress();
            }

            @Override
            public void onError(Throwable e) {
                stopProgress();
            }

            @Override
            public void onNext(ResponseBody data) {
                try {
                    String s = new String(data.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String status = jsonObject.optString("status");
                    if (status.equals("success")){
                        articleBean = JSON.parseObject(jsonObject.optString("data"),ArticleBean.class);
                        if (pageNo == 1) {
                            newsData.clear();
                        }
                        newsData.addAll(articleBean.getArticleInfoList().getData());
                        setAdapter();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean>(this, R.layout.news_item_layout,newsData) {
            @Override
            protected void convert(ViewHolder holder, ArticleBean.ArticleInfoListBean.DataBean item, int position) {
                TextView title = holder.getConvertView().findViewById(R.id.title);
                ShapeableImageView imageView = holder.getConvertView().findViewById(R.id.image);
                if (item.getIcon().startsWith("http")){
                    Glide.with(SearchActivity.this).load(item.getIcon()).into(imageView);
                }else {
                    Glide.with(SearchActivity.this).load(HttpInterface.IMG_URL+ item.getIcon()).into(imageView);
                }

                holder.getView(R.id.hotTxt).setVisibility(View.GONE);
                title.setText(item.getSubject());

                holder.setText(R.id.time,item.getCreateDate());
                holder.setText(R.id.glance,item.getViewNum());
                holder.getView(R.id.newItemBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchActivity.this, WebViewActivity.class);
                        if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                            intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId()
                                    + "&uid=" + LocalConfiguration.userInfo.getUid()+ "&username=" + LocalConfiguration.userInfo.getUsername()+"&app=1");
                        }else {
                            intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId()+"&app=1");
                        }
                        startActivity(intent);
                    }
                });
            }
        };
        binding.list.setAdapter(adapter);
    }


    /**
     * 搜索历史
     */
    private void setHistoryAdapter(){
        if (historyAdapter != null){
            historyAdapter.notifyDataSetChanged();
            return;
        }
        historyAdapter = new com.zhy.adapter.recyclerview.CommonAdapter<String>(this,R.layout.sku_item_layout,historyData) {
            @Override
            protected void convert(com.zhy.adapter.recyclerview.base.ViewHolder holder, String item, int position) {
                holder.setText(R.id.text,item);
                holder.getView(R.id.text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgress("");
                        historyData.add(item);
                        MyApplication.spUtils.put("HomeHistoryData",new HashSet<>(historyData));
                        getData(item);
                    }
                });
            }
        };
        binding.historyList.setAdapter(historyAdapter);
    }
}