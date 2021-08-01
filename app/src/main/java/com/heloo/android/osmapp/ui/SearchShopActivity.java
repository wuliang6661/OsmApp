package com.heloo.android.osmapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivitySearchBinding;
import com.heloo.android.osmapp.model.SearchShopBO;
import com.heloo.android.osmapp.ui.main.store.StoreDetailActivity;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGViewHolder;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 搜索商品界面
 */
public class SearchShopActivity extends BaseActivity {

    private ActivitySearchBinding binding;
    private ArrayList<String> historyData = new ArrayList<>();
    private CommonAdapter<String> historyAdapter;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
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
        int id = binding.searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        //获取到TextView的控件
        TextView textView = (TextView) binding.searchView.findViewById(id);
        //设置字体大小为14sp
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);//14sp
        //设置字体颜色
        textView.setTextColor(Color.parseColor("#333333"));
        //设置提示文字颜色
        textView.setHintTextColor(Color.parseColor("#888888"));
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
        MyApplication.spUtils.put("ShopHistoryData", gson.toJson(historyData));
    }


    /**
     * 设置历史记录适配器
     */
    private void setHistory() {
        String history = MyApplication.spUtils.getString("ShopHistoryData");
        if (!StringUtils.isEmpty(history)) {
            Gson gson = new Gson();
            historyData = gson.fromJson(history, new TypeToken<List<String>>() {
            }.getType());
        }
        setHistoryAdapter();
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
                        saveHistory(item);
                        getData(item);
                    }
                });
            }
        };
        binding.historyList.setAdapter(historyAdapter);
    }


    private void getData(String keyWord) {
        HttpInterfaceIml.getSearch(keyWord).subscribe(new HttpResultSubscriber<List<SearchShopBO>>() {

            @Override
            public void onSuccess(List<SearchShopBO> s) {
                stopProgress();
                showShopAdapter(s);
                setHistory();
            }

            @Override
            public void onFiled(String message) {
                stopProgress();
                ToastUtils.showShortToast(message);
            }
        });
    }


    private void showShopAdapter(List<SearchShopBO> shops) {
        LGRecycleViewAdapter<SearchShopBO> adapter =
                new LGRecycleViewAdapter<SearchShopBO>(shops) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.product_item_layout;
                    }

                    @Override
                    public void convert(LGViewHolder holder, SearchShopBO goodsMoelsBean, int position) {
                        holder.setText(R.id.tvName, goodsMoelsBean.name);
                        holder.setText(R.id.tvPrice, String.format("￥%s", goodsMoelsBean.preferentialPrice));
                        if (goodsMoelsBean.icon.startsWith("http")) {
                            holder.setImageUrl(SearchShopActivity.this, R.id.productImg, goodsMoelsBean.icon);
                        } else {
                            holder.setImageUrl(SearchShopActivity.this, R.id.productImg, HttpInterface.URL + goodsMoelsBean.icon);
                        }
                    }
                };
        adapter.setOnItemClickListener(R.id.item_layout, new LGRecycleViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
//                        addCart(productImg);
                Intent intent = new Intent(SearchShopActivity.this, StoreDetailActivity.class);
                intent.putExtra("id", shops.get(position).id);
                startActivity(intent);
            }
        });
        binding.list.setAdapter(adapter);
    }
}
