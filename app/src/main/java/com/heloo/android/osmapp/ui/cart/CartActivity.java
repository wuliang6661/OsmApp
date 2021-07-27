package com.heloo.android.osmapp.ui.cart;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.databinding.ActivityCartBinding;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.CartContract;
import com.heloo.android.osmapp.mvp.presenter.CartPresenter;
import com.heloo.android.osmapp.ui.confirm.ConfirmActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 购物车
 * @auther WITNESS 2021/4/13
 */
public class CartActivity extends MVPBaseActivity<CartContract.View, CartPresenter, ActivityCartBinding>
    implements CartContract.View, View.OnClickListener {

    private CommonAdapter<String> adapter;
    private List<String> data = new ArrayList<>();
    private int editPosition = -1;//编辑
    private boolean isEdit = false;
    private List<Integer> selectProductList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goBack();
        for (int i=0;i<10;i++){
            data.add("");
        }
        initView();
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.list.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.manageBtn.setOnClickListener(this);
        viewBinding.submitBtn.setOnClickListener(this);
        setAdapter();
    }


    private void setAdapter() {
        if (adapter !=  null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<String>(this,R.layout.cart_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                CheckBox selectImg = holder.getView(R.id.selectImg);
                if (position == editPosition){
                    holder.getView(R.id.normalPart).setVisibility(View.GONE);
                    holder.getView(R.id.editLayout).setVisibility(View.VISIBLE);
                }else {
                    holder.getView(R.id.normalPart).setVisibility(View.VISIBLE);
                    holder.getView(R.id.editLayout).setVisibility(View.GONE);
                }
                if (selectProductList.contains(position)){
                    selectImg.setChecked(true);
                }else {
                    selectImg.setChecked(false);
                }
                holder.getView(R.id.editBtn).setOnClickListener(v -> {// 编辑
                    editPosition = position;
                    notifyDataSetChanged();
                });
                holder.getView(R.id.doneBtn).setOnClickListener(v -> {//完成
                    editPosition = -1;
                    notifyDataSetChanged();
                });
                holder.getView(R.id.txt_delete).setOnClickListener(v -> {// 删除
                    data.remove(position);
                    adapter.notifyItemRemoved(position);
                    notifyItemRangeChanged(0, data.size());
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.manageBtn://管理
                if (isEdit){
                    viewBinding.priceLayout.setVisibility(View.VISIBLE);
                    viewBinding.submitBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.submit_bg,null));
                    viewBinding.submitBtn.setTextColor(Color.parseColor("#ffffff"));
                    viewBinding.submitBtn.setText("结算（1）");
                    isEdit = false;
                }else {
                    viewBinding.priceLayout.setVisibility(View.GONE);
                    viewBinding.submitBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.delete_bg,null));
                    viewBinding.submitBtn.setTextColor(Color.parseColor("#D3A952"));
                    viewBinding.submitBtn.setText("删除");
                    isEdit = true;
                }
                break;
            case R.id.submitBtn:
                startActivity(new Intent(CartActivity.this, ConfirmActivity.class));
                break;
        }
    }
}