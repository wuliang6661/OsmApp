package com.heloo.android.osmapp.ui.address;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.alibaba.fastjson.JSON;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivityAddressBinding;
import com.heloo.android.osmapp.model.AddressBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.AddressContract;
import com.heloo.android.osmapp.mvp.presenter.AddressPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 地址列表
 */
public class AddressActivity extends MVPBaseActivity<AddressContract.View, AddressPresenter, ActivityAddressBinding>
        implements AddressContract.View, View.OnClickListener {

    private CommonAdapter<AddressBean> adapter;
    private List<AddressBean> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        goBack();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showProgress("");
        mPresenter.getAddress(MyApplication.spUtils.getString("token", ""));
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        View footView = LayoutInflater.from(this).inflate(R.layout.address_foot_layout, null);
        Button addBtn = footView.findViewById(R.id.addBtn);
        addBtn.setOnClickListener(v -> startActivity(new Intent(AddressActivity.this, AddAddressActivity.class)));
        viewBinding.list.addFooterView(footView);
        goBack();
        viewBinding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            mPresenter.getAddress(MyApplication.spUtils.getString("token", ""));
            refreshLayout.finishRefresh(1000);
        });
    }

    private void setAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<AddressBean>(this, R.layout.address_item_layout, data) {
            @Override
            protected void convert(ViewHolder holder, AddressBean item, int position) {
                holder.setText(R.id.name, item.getName());
                holder.setText(R.id.phone, item.getPhone());
                holder.setText(R.id.address, String.format("%s%s%s%s", item.getProvince(), item.getCity(), item.getArea(), item.getAddress()));
                if (item.getStatus() == 1) {
                    holder.getView(R.id.defaultAddress).setVisibility(View.VISIBLE);
                    holder.getView(R.id.selectImg).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.defaultAddress).setVisibility(View.GONE);
                    holder.getView(R.id.selectImg).setVisibility(View.GONE);
                }
                holder.getView(R.id.delAddress).setOnClickListener(v -> {
                    showProgress("");
                    mPresenter.delAddress(MyApplication.spUtils.getString("token", ""), item.getId());
                });

                holder.getView(R.id.modifyAddress).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(AddressActivity.this, AddAddressActivity.class);
                        intent.putExtra("address", item);
                        startActivity(intent);
                    }
                });
                holder.getView(R.id.item_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getIntent().getBooleanExtra("type", false)) {
                            Intent intent = new Intent();
                            intent.putExtra("address", item);
                            setResult(0x11, intent);
                            finish();
                        }
                    }
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
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
    public void getAddress(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
        if (status.equals("success")) {
            data.clear();
            data.addAll(JSON.parseArray(jsonObject1.optString("list"), AddressBean.class));
            setAdapter();
        }
    }

    @Override
    public void delAddress(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")) {
            mPresenter.getAddress(MyApplication.spUtils.getString("token", ""));
        }
    }
}