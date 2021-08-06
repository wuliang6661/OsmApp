package com.heloo.android.osmapp.ui.confirm;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityConfirmBinding;
import com.heloo.android.osmapp.model.AddressBean;
import com.heloo.android.osmapp.model.OrderPriceBO;
import com.heloo.android.osmapp.model.PayBean;
import com.heloo.android.osmapp.model.PayResult;
import com.heloo.android.osmapp.model.ShopAddressList;
import com.heloo.android.osmapp.model.ShopCarBO;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.ConfirmContract;
import com.heloo.android.osmapp.mvp.presenter.ConfirmPresenter;
import com.heloo.android.osmapp.ui.address.AddressActivity;
import com.heloo.android.osmapp.ui.order.OrderDetailActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * 订单确认
 */
public class ConfirmActivity extends MVPBaseActivity<ConfirmContract.View, ConfirmPresenter, ActivityConfirmBinding>
        implements ConfirmContract.View, View.OnClickListener {

    private CommonAdapter<OrderPriceBO.ShopOrderModelsBean> adapter;
    private ArrayList<ShopCarBO.ShopCarInfoDOSBean> data;

    private TextView selectAddress;
    private RelativeLayout addressLayout;
    private TextView name, phone, address, all_inter, edit_remark, zhifu;
    private ImageView editAddress;
    private CheckBox selectImg, zhifu_bao_check;
    private AddressBean addressBean;
    private OrderPriceBO priceBO;
    private RelativeLayout zhifuBao;

    StringBuilder shopIds;
    StringBuilder shopNums;

    private String orderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = (ArrayList<ShopCarBO.ShopCarInfoDOSBean>) getIntent().getExtras().getSerializable("shops");
        initView();
        mPresenter.getUserAdd();
        mPresenter.getUserIntegration();
        shopIds = new StringBuilder();
        shopNums = new StringBuilder();
        for (ShopCarBO.ShopCarInfoDOSBean item : data) {
            shopIds.append(item.goodsId).append(",");
            shopNums.append(item.goodsNum).append(",");
        }
        showProgress(null);
        mPresenter.getshopInter(shopIds.substring(0, shopIds.length() - 1), shopNums.substring(0, shopNums.length() - 1));

        if (LocalConfiguration.userInfo.getSourceType() != 1001) {
            selectImg.setChecked(true);
            selectImg.setEnabled(false);
            zhifu.setVisibility(View.GONE);
            zhifuBao.setVisibility(View.GONE);
            zhifu_bao_check.setChecked(false);
        } else {
            selectImg.setEnabled(true);
        }
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        View footView = LayoutInflater.from(this).inflate(R.layout.confirm_foot_layout, null);
        viewBinding.list.addFooterView(footView);
        viewBinding.submitBtn.setOnClickListener(this);
        viewBinding.backBtn.setOnClickListener(this);
        selectAddress = footView.findViewById(R.id.select_address);
        addressLayout = footView.findViewById(R.id.address_layout);
        all_inter = footView.findViewById(R.id.all_inter);
        edit_remark = footView.findViewById(R.id.edit_remark);
        name = footView.findViewById(R.id.name);
        phone = footView.findViewById(R.id.phone);
        address = footView.findViewById(R.id.address);
        editAddress = footView.findViewById(R.id.editAddress);
        zhifu = footView.findViewById(R.id.zhifu_text);
        zhifuBao = footView.findViewById(R.id.zhifu_bao);
        zhifu_bao_check = footView.findViewById(R.id.zhifu_bao_check);
        selectImg = footView.findViewById(R.id.selectImg);
        int price = 0;
        for (ShopCarBO.ShopCarInfoDOSBean item : data) {
            price += (item.goodsPrice * item.goodsNum);
        }
        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmActivity.this, AddressActivity.class);
                intent.putExtra("type", true);
                startActivityForResult(intent, 0x11);
            }
        });
        selectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmActivity.this, AddressActivity.class);
                intent.putExtra("type", true);
                startActivityForResult(intent, 0x11);
            }
        });
        selectImg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    viewBinding.price.setText("￥ " + priceBO.totalDiscountPrice + "  + " + priceBO.totalScore + "珍币");
                } else {
                    viewBinding.price.setText("￥ " + priceBO.totalPrice);
                }
            }
        });
    }

    private void setAdapter() {
        adapter = new CommonAdapter<OrderPriceBO.ShopOrderModelsBean>(this, R.layout.cart_item_layout, priceBO.shopOrderModels) {
            @Override
            protected void convert(ViewHolder holder, OrderPriceBO.ShopOrderModelsBean s, int position) {
                holder.getView(R.id.selectImg).setVisibility(View.GONE);
                holder.getView(R.id.editBtn).setVisibility(View.INVISIBLE);
                holder.setText(R.id.productTitle, s.name);
                holder.setText(R.id.editTitle, s.name);
                holder.setText(R.id.price, "¥ " + s.discountprice);
                holder.setText(R.id.num, "x " + s.number);
                holder.setText(R.id.editNum, s.number + "");
                if (!s.icon.startsWith("http")) {
                    s.icon = HttpInterface.IMG_URL + s.icon;
                }
                Glide.with(ConfirmActivity.this).load(s.icon)
                        .placeholder(R.drawable.default_head)
                        .error(R.drawable.default_head)
                        .into((ImageView) holder.getView(R.id.productImg));

            }
        };
        viewBinding.list.setAdapter(adapter);
    }


    @Override
    public void onRequestError(String msg) {
        stopProgress();
        ToastUtils.showShortToast(msg);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitBtn:
//                payDialog();
                if (LocalConfiguration.userInfo.getSourceType() != 1001) {
                    new com.heloo.android.osmapp.widget.AlertDialog(ConfirmActivity.this).builder().setGone().setMsg("是否确认支付？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", v1 -> {
                                createOrder();
                            }).show();
                } else {
                    createOrder();
                }
                break;
            case R.id.backBtn:
                finish();
                break;
        }
    }


    /**
     * 生成订单
     */
    private void createOrder() {
        String remark = edit_remark.getText().toString().trim();
        //会员
        if (LocalConfiguration.userInfo.getSourceType() != 1001) {
            if (addressBean == null) {
                ToastUtils.showShortToast("请选择收货地址！");
                return;
            }
        }
        mPresenter.createOrder(addressBean == null ? null : addressBean.getId(), shopIds.substring(0, shopIds.length() - 1),
                shopNums.substring(0, shopNums.length() - 1), remark, selectImg.isChecked() ? 1 : 0);
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
            startActivity(new Intent(ConfirmActivity.this, PaySuccessActivity.class));
        });
        WindowManager.LayoutParams layoutParams = noticeDialog.getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        noticeDialog.getWindow().setAttributes(layoutParams);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        switch (resultCode) {
            case 0x11:
                addressBean = (AddressBean) data.getSerializableExtra("address");
                selectAddress.setVisibility(View.GONE);
                addressLayout.setVisibility(View.VISIBLE);
                name.setText(addressBean.getName());
                phone.setText(addressBean.getPhone());
                this.address.setText(String.format("%s%s%s%s", addressBean.getProvince(), addressBean.getCity(), addressBean.getArea(), addressBean.getAddress()));
                break;
        }
    }

    @Override
    public void getAddress(ShopAddressList address) {
        if (address.list.isEmpty()) {
            selectAddress.setVisibility(View.VISIBLE);
            addressLayout.setVisibility(View.GONE);
            return;
        }
        for (AddressBean item : address.list) {
            if (item.getStatus() == 1) {
                addressBean = item;
            }
        }
        if (addressBean == null && !address.list.isEmpty()) {
            addressBean = address.list.get(0);
        }
        if (addressBean == null) {
            selectAddress.setVisibility(View.VISIBLE);
            addressLayout.setVisibility(View.GONE);
            return;
        }
        selectAddress.setVisibility(View.GONE);
        addressLayout.setVisibility(View.VISIBLE);
        name.setText(addressBean.getName());
        phone.setText(addressBean.getPhone());
        this.address.setText(String.format("%s%s%s%s", addressBean.getProvince(), addressBean.getCity(), addressBean.getArea(), addressBean.getAddress()));
    }

    @Override
    public void getshopInter(OrderPriceBO priceBO) {
        stopProgress();
        this.priceBO = priceBO;
        if (LocalConfiguration.userInfo.getSourceType() != 1001) {
            viewBinding.price.setText(priceBO.totalDiscountPrice + "");
            viewBinding.zhenbiImg.setVisibility(View.VISIBLE);
        } else {
            if (selectImg.isChecked()) {
                viewBinding.price.setText("￥ " + priceBO.totalDiscountPrice + "  + " + priceBO.totalScore + "珍币");
            } else {
                viewBinding.price.setText("￥ " + priceBO.totalPrice);
            }
        }
        String text = "使用珍币（本单最多可使用珍币" + (priceBO == null ? 0 : priceBO.totalScore) + "，当前用户剩余珍币" + LocalConfiguration.userInfo.getIntegration() + "）";
        all_inter.setText(text);
        setAdapter();
    }

    @Override
    public void getAllInter(UserInfo info) {
        LocalConfiguration.userInfo = info;
        String text = "使用珍币（本单最多可使用珍币" + (priceBO == null ? 0 : priceBO.totalScore) + "，当前用户剩余珍币" + info.getIntegration() + "）";
        all_inter.setText(text);
    }


    public void pay(PayBean orderInfo, String order) {
        if (LocalConfiguration.userInfo.getSourceType() != 1001) {
            Bundle bundle = new Bundle();
            bundle.putString("id", order);
            gotoActivity(PaySuccessActivity.class, bundle, true);
            return;
        }
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                orderId = order;
                PayTask alipay = new PayTask(ConfirmActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo.body, true);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    private final Handler mHandler = new Handler() {

        @SuppressLint("HandlerLeak")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    @SuppressLint("HandlerLeak")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Bundle bundle = new Bundle();
                        bundle.putString("id", orderId);
                        gotoActivity(PaySuccessActivity.class, bundle, true);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Bundle bundle = new Bundle();
                        bundle.putString("id", orderId);
                        gotoActivity(OrderDetailActivity.class, bundle, true);
                    }
                    break;
                default:
                    break;
            }
        }
    };
}