package com.heloo.android.osmapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.databinding.ActivityOrderBinding;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.OrderContract;
import com.heloo.android.osmapp.mvp.presenter.OrderPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 全部订单
 */
public class OrderActivity extends MVPBaseActivity<OrderContract.View, OrderPresenter, ActivityOrderBinding>
    implements OrderContract.View, View.OnClickListener {

    private String tag = "";
    private CommonAdapter<String> adapter;
    private List<String> data = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getIntent().getStringExtra("tag");
        for (int i=0;i<5;i++){
            data.add("");
        }
        initView();
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("全部订单"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("待付款"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("已付款"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("待确认"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("已完成"));
        viewBinding.backBtn.setOnClickListener(this);
        viewBinding.list.setLayoutManager(new LinearLayoutManager(this));
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout,null);
        TextView name = view.findViewById(R.id.name);
        switch (tag){
            case "1":
                name.setText(viewBinding.tabLayout.getTabAt(1).getText());
                viewBinding.tabLayout.getTabAt(1).setCustomView(view);
                viewBinding.tabLayout.getTabAt(1).select();
                break;
            case "2":
                name.setText(viewBinding.tabLayout.getTabAt(2).getText());
                viewBinding.tabLayout.getTabAt(2).setCustomView(view);
                viewBinding.tabLayout.getTabAt(2).select();
                break;
            case "3":
                name.setText(viewBinding.tabLayout.getTabAt(3).getText());
                viewBinding.tabLayout.getTabAt(3).setCustomView(view);
                viewBinding.tabLayout.getTabAt(3).select();
                break;
            case "4":
                name.setText(viewBinding.tabLayout.getTabAt(4).getText());
                viewBinding.tabLayout.getTabAt(4).setCustomView(view);
                viewBinding.tabLayout.getTabAt(4).select();
                break;
            default:
                name.setText(viewBinding.tabLayout.getTabAt(0).getText());
                viewBinding.tabLayout.getTabAt(0).setCustomView(view);
                viewBinding.tabLayout.getTabAt(0).select();
                break;
        }
        viewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null){
                    tab.setCustomView(null);
                }
                View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout,null);
                TextView name = view.findViewById(R.id.name);
                name.setText(tab.getText());
                tab.setCustomView(view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setAdapter();
    }


    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<String>(this,R.layout.order_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                RecyclerView productList = holder.getConvertView().findViewById(R.id.productList);
                productList.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                CommonAdapter<String> adapter = new CommonAdapter<String>(OrderActivity.this,R.layout.cart_item_layout,data) {
                    @Override
                    protected void convert(ViewHolder holder, String s, int position) {
                        holder.getView(R.id.selectImg).setVisibility(View.INVISIBLE);
                        holder.getView(R.id.editBtn).setVisibility(View.INVISIBLE);
                    }
                };
                productList.setAdapter(adapter);

                holder.getView(R.id.orderBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(OrderActivity.this, OrderDetailActivity.class));
                    }
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.backBtn:
                finish();
                break;
        }
    }

    @Override
    public void getAddResult(ResponseBody addResult) throws JSONException, IOException {

    }

    @Override
    public void onRequestError(String msg) {

    }

    @Override
    public void onRequestEnd() {

    }
}