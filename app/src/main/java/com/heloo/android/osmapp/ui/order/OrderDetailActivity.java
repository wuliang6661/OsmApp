package com.heloo.android.osmapp.ui.order;


import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.databinding.ActivityOrderDetailBinding;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.OrderDetailContract;
import com.heloo.android.osmapp.mvp.presenter.OrderDetailPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 订单详情
 */
public class OrderDetailActivity extends MVPBaseActivity<OrderDetailContract.View, OrderDetailPresenter, ActivityOrderDetailBinding>
    implements OrderDetailContract.View, View.OnClickListener {

    private CommonAdapter<String> adapter;
    private List<String> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (int i=0;i<3;i++){
            data.add("");
        }
        initView();
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.backBtn.setOnClickListener(this);
        viewBinding.productList.setLayoutManager(new LinearLayoutManager(this));
        setAdapter();
    }

    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<String>(this,R.layout.cart_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                holder.getView(R.id.selectImg).setVisibility(View.INVISIBLE);
                holder.getView(R.id.editBtn).setVisibility(View.INVISIBLE);
            }
        };
        viewBinding.productList.setAdapter(adapter);
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