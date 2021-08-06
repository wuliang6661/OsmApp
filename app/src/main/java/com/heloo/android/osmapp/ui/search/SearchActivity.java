package com.heloo.android.osmapp.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivitySearchBinding;
import com.heloo.android.osmapp.model.ArticleBean;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.utils.StringUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
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
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        setHistory();
        binding.searchView.isIconified();
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                showProgress("");
                if (!StringUtils.isEmpty(query)) {
                    saveHistory(query);
                }
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


    /**
     * 保存历史记录
     */
    private void saveHistory(String query) {
        for (int i = 0; i < historyData.size(); i++) {
            if (historyData.get(i).equals(query)) {
                historyData.remove(i);
            }
        }
        historyData.add(0, query);
        Gson gson = new Gson();
        MyApplication.spUtils.put("HomeHistoryData", gson.toJson(historyData));
    }


    /**
     * 设置历史记录适配器
     */
    private void setHistory() {
        String history = MyApplication.spUtils.getString("HomeHistoryData");
        if (!StringUtils.isEmpty(history)) {
            Gson gson = new Gson();
            historyData = gson.fromJson(history, new TypeToken<List<String>>() {
            }.getType());
        }
        setHistoryAdapter();
    }


    private void getData(String keyWord) {
        HttpInterfaceIml.getArticleList(MyApplication.spUtils.getString("token", ""), pageNo, pageSize, "", "", keyWord).subscribe(new Subscriber<ResponseBody>() {
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
                    if (status.equals("success")) {
                        articleBean = JSON.parseObject(jsonObject.optString("data"), ArticleBean.class);
                        if (pageNo == 1) {
                            newsData.clear();
                        }
                        newsData.addAll(articleBean.getArticleInfoList().getData());
                        setAdapter();
                        setHistory();
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
        adapter = new CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean>(this, R.layout.news_item_layout, newsData) {
            @Override
            protected void convert(ViewHolder holder, ArticleBean.ArticleInfoListBean.DataBean item, int position) {
                TextView title = holder.getConvertView().findViewById(R.id.title);
                ShapeableImageView imageView = holder.getConvertView().findViewById(R.id.image);
                if (item.getIcon().startsWith("http")) {
                    Glide.with(SearchActivity.this).load(item.getIcon()).into(imageView);
                } else {
                    Glide.with(SearchActivity.this).load(HttpInterface.IMG_URL + item.getIcon()).into(imageView);
                }

                holder.getView(R.id.hotTxt).setVisibility(View.GONE);
                title.setText(item.getSubject());

                holder.setText(R.id.time, item.getCreateDate());
                holder.setText(R.id.glance, item.getViewNum());
                holder.getView(R.id.newItemBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SearchActivity.this, WebViewActivity.class);
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
        binding.list.setAdapter(adapter);
    }


    /**
     * 搜索历史
     */
    private void setHistoryAdapter() {
        historyAdapter = new CommonAdapter<String>(this, R.layout.sku_item_layout, historyData) {
            @Override
            protected void convert(ViewHolder holder, String item, int position) {
                holder.setText(R.id.text, item);
                holder.getView(R.id.text).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showProgress("");
                        binding.searchView.setQuery(item,true);
                        saveHistory(item);
//                        getData(item);
                    }
                });
            }
        };
        binding.historyList.setAdapter(historyAdapter);
    }
}