package com.heloo.android.osmapp.ui.order;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.databinding.ActivityOrderDetailBinding;
import com.heloo.android.osmapp.model.OrderBO;
import com.heloo.android.osmapp.model.PayBean;
import com.heloo.android.osmapp.model.PayResult;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.OrderContract;
import com.heloo.android.osmapp.mvp.presenter.OrderPresenter;
import com.heloo.android.osmapp.ui.confirm.PaySuccessActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.AlertDialog;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGViewHolder;

import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 订单详情
 */
public class OrderDetailActivity extends MVPBaseActivity<OrderContract.View, OrderPresenter, ActivityOrderDetailBinding>
        implements OrderContract.View, View.OnClickListener {

    private LGRecycleViewAdapter<OrderBO.OrderItemlistBean> adapter;
    private OrderBO orderBO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getOrderDetails(getIntent().getExtras().getString("id"));
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.backBtn.setOnClickListener(this);
        viewBinding.productList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new LGRecycleViewAdapter<OrderBO.OrderItemlistBean>(orderBO.orderItemlist) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.cart_item_layout;
            }

            @Override
            public void convert(LGViewHolder holder, OrderBO.OrderItemlistBean orderItemlistBean, int position) {
                holder.setImageUrl(holder.itemView.getContext(), R.id.productImg, orderItemlistBean.icon);
                holder.setText(R.id.productTitle, orderItemlistBean.name);
                holder.setText(R.id.price, "￥ " + orderItemlistBean.prize);
                holder.setText(R.id.num, "x" + orderItemlistBean.prodNum);
                holder.setText(R.id.productSecondTitle, "x" + orderItemlistBean.spec);
                holder.getView(R.id.selectImg).setVisibility(View.GONE);
                holder.getView(R.id.editBtn).setVisibility(View.INVISIBLE);
            }
        };
        viewBinding.productList.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backBtn:
                finish();
                break;
        }
    }


    @Override
    public void onRequestError(String msg) {
        ToastUtils.showShortToast(msg);
    }

    @Override
    public void getOrder(List<OrderBO> orderBOS) {

    }

    @Override
    public void cancleSuress() {
        ToastUtils.showShortToast("订单已取消！");
        mPresenter.getOrderDetails(orderBO.id);
    }

    @Override
    public void comfimOrder() {
        ToastUtils.showShortToast("已确认收货！");
        mPresenter.getOrderDetails(orderBO.id);
    }

    @Override
    public void getOrderDetails(OrderBO orderBO) {
        this.orderBO = orderBO;
        viewBinding.orderNum.setText("订单号: " + orderBO.orderNo);
        viewBinding.oldPrice.setText("¥ " + orderBO.totalPrice);
        viewBinding.delPrice.setText("-¥ " + orderBO.discountFee);
        viewBinding.nowPrice.setText("¥ " + orderBO.payFee);
        viewBinding.totalPrice.setText("¥ " + orderBO.totalFee);
        viewBinding.orderNum2.setText("订单编号：" + orderBO.orderNo);
        viewBinding.orderTime.setText("提交时间：" + orderBO.createDate);
        switch (orderBO.status) {
            case "create":  //待支付
                viewBinding.orderStatus.setText("待支付");
                viewBinding.orderStatus.setTextColor(Color.parseColor("#FF5A5A"));
                viewBinding.btn1.setVisibility(View.VISIBLE);
                viewBinding.btn2.setVisibility(View.VISIBLE);
                viewBinding.btn1.setText("取消订单");
                viewBinding.btn2.setText("付款");
                viewBinding.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog(OrderDetailActivity.this).builder().setGone().setMsg("确认取消订单？")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", v -> {
                                    mPresenter.cancleOrder(orderBO.id);
                                }).show();
                    }
                });
                viewBinding.btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.pay(orderBO.id);
                    }
                });
                break;
            case "cancel":  //已取消
                viewBinding.orderStatus.setText("已取消");
                viewBinding.orderStatus.setTextColor(Color.parseColor("#BABABA"));
                viewBinding.buttomLayout.setVisibility(View.GONE);
                break;
            case "refunded":  //已退款
                viewBinding.orderStatus.setText("已退款");
                viewBinding.orderStatus.setTextColor(Color.parseColor("#BABABA"));
                viewBinding.buttomLayout.setVisibility(View.GONE);
                break;
            case "pay":    //已支付
                viewBinding.orderStatus.setText("已付款");
                viewBinding.orderStatus.setTextColor(Color.parseColor("#FF5A5A"));
                viewBinding.btn1.setVisibility(View.VISIBLE);
                viewBinding.btn2.setVisibility(View.GONE);
                viewBinding.btn1.setText("退款");
                viewBinding.btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog(OrderDetailActivity.this).builder().setGone().setMsg("是否确认退款？")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", v -> {
                                    mPresenter.paRefundy(orderBO.id);
                                }).show();
                    }
                });
                break;
            case "success":  //已完成
                viewBinding.orderStatus.setText("已完成");
                viewBinding.orderStatus.setTextColor(Color.parseColor("#BABABA"));
                viewBinding.buttomLayout.setVisibility(View.GONE);
                break;
            case "shipments":  //待确认
                viewBinding.orderStatus.setText("待确认");
                viewBinding.orderStatus.setTextColor(Color.parseColor("#D4AB56"));
                viewBinding.btn1.setVisibility(View.GONE);
                viewBinding.btn2.setVisibility(View.VISIBLE);
                viewBinding.btn2.setText("确认提货");
                viewBinding.btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog(OrderDetailActivity.this).builder().setGone().setMsg("是否确认已收货？")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", v -> {
                                    mPresenter.comfimOrder(orderBO.id);
                                }).show();
                    }
                });
                break;
        }
        setAdapter();
    }

    @Override
    public void pay(PayBean orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderDetailActivity.this);
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

    @Override
    public void unRelase() {
        ToastUtils.showShortToast("已成功申请退款！");
        mPresenter.getOrderDetails(orderBO.id);
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
                        bundle.putString("id", getIntent().getExtras().getString("id"));
                        gotoActivity(PaySuccessActivity.class, bundle, false);
                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        ToastUtils.showShortToast("支付失败！");
                    }
                    break;
                default:
                    break;
            }
        }
    };

}