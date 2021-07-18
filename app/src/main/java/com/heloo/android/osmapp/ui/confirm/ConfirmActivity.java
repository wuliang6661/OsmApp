package com.heloo.android.osmapp.ui.confirm;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.databinding.ActivityConfirmBinding;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.ConfirmContract;
import com.heloo.android.osmapp.mvp.presenter.ConfirmPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 订单确认
 */
public class ConfirmActivity extends MVPBaseActivity<ConfirmContract.View, ConfirmPresenter, ActivityConfirmBinding>
    implements ConfirmContract.View, View.OnClickListener {

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
        View footView = LayoutInflater.from(this).inflate(R.layout.confirm_foot_layout, null);
        viewBinding.list.addFooterView(footView);
        viewBinding.submitBtn.setOnClickListener(this);
        viewBinding.backBtn.setOnClickListener(this);
        setAdapter();
    }

    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<String>(this,R.layout.cart_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, String item, int position) {
                holder.getView(R.id.selectImg).setVisibility(View.INVISIBLE);
                holder.getView(R.id.editBtn).setVisibility(View.INVISIBLE);
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
            case R.id.submitBtn:
//                payDialog();
                startActivity(new Intent(ConfirmActivity.this,PaySuccessActivity.class));
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }

    /**
     * 支付弹窗
     */
    private void payDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.pay_dialog_layout, null);
        ImageButton close = v.findViewById(R.id.close);
        Button submitBtn = v.findViewById(R.id.submitBtn);

        builder.setView(v);
        builder.setCancelable(true);
        final Dialog noticeDialog = builder.create();
        noticeDialog.getWindow().setGravity(Gravity.BOTTOM);
        noticeDialog.getWindow().setWindowAnimations(R.style.anim_menu_bottombar);
        noticeDialog.show();

        close.setOnClickListener(view -> noticeDialog.dismiss());

        submitBtn.setOnClickListener(v1 -> {
            noticeDialog.dismiss();
            startActivity(new Intent(ConfirmActivity.this,PaySuccessActivity.class));
        });
        WindowManager.LayoutParams layoutParams = noticeDialog.getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        noticeDialog.getWindow().setAttributes(layoutParams);
    }

}