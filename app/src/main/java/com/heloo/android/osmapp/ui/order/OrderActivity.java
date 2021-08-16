package com.heloo.android.osmapp.ui.order;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityOrderBinding;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 全部订单
 */
public class OrderActivity extends MVPBaseActivity<OrderContract.View, OrderPresenter, ActivityOrderBinding>
        implements OrderContract.View, View.OnClickListener {

    private String tag = "";
    private LGRecycleViewAdapter<OrderBO> adapter;
    private List<OrderBO> data = new ArrayList<>();
    int pageNum = 1;
    int type = 1;

    private String payOrderId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tag = getIntent().getStringExtra("tag");
        initView();
        viewBinding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            pageNum = 1;
            mPresenter.getOrder(pageNum, type);
        });
        viewBinding.refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            pageNum++;
            mPresenter.getOrder(pageNum, type);
        });

    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("全部订单"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("待付款"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("已付款"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("待确认"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("已完成"));
        viewBinding.backBtn.setOnClickListener(this);
        viewBinding.tabLayout.setTabIndicatorFullWidth(false);
        viewBinding.list.setLayoutManager(new LinearLayoutManager(this));
        switch (tag) {
            case "1":
                type = 2;
                viewBinding.tabLayout.getTabAt(1).select();
                break;
            case "2":
                type = 3;
                viewBinding.tabLayout.getTabAt(2).select();
                break;
            case "3":
                viewBinding.tabLayout.getTabAt(3).select();
                type = 4;
                break;
            case "4":
                viewBinding.tabLayout.getTabAt(4).select();
                type = 5;
                break;
            default:
                viewBinding.tabLayout.getTabAt(0).select();
                type = 1;
                break;
        }
        mPresenter.getOrder(pageNum, type);
        viewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pageNum = 1;
                type = tab.getPosition() + 1;
                viewBinding.list.scrollToPosition(0);
                mPresenter.getOrder(pageNum, tab.getPosition() + 1);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        setAdapter();
    }


    private void setAdapter() {
        if (adapter != null) {
            adapter.setData(data);
            return;
        }
        adapter = new LGRecycleViewAdapter<OrderBO>(data) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.order_item_layout;
            }

            @Override
            public void convert(LGViewHolder holder, OrderBO orderBO, int position) {
                holder.setText(R.id.orderNum, "订单号: " + orderBO.orderNo);
                setTextStatus((TextView) holder.getView(R.id.orderStatus), orderBO.status, holder, orderBO);
                if (LocalConfiguration.userInfo.getSourceType() == 1002) {
                    holder.getView(R.id.score).setVisibility(View.VISIBLE);
                    holder.setText(R.id.price, "共" + orderBO.goodsNumber + "件商品合计: " + orderBO.discountnumber);
                } else {
                    holder.setText(R.id.price, "共" + orderBO.goodsNumber + "件商品合计:￥ " + orderBO.totalFee);
                    holder.getView(R.id.score).setVisibility(View.GONE);
                }
                RecyclerView productList = (RecyclerView) holder.getView(R.id.productList);
                productList.setLayoutManager(new LinearLayoutManager(OrderActivity.this));
                productList.setNestedScrollingEnabled(false);
                LGRecycleViewAdapter<OrderBO.OrderItemlistBean> adapter = new LGRecycleViewAdapter<OrderBO.OrderItemlistBean>(
                        orderBO.orderItemlist) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.cart_item_layout;
                    }

                    @Override
                    public void convert(LGViewHolder holder, OrderBO.OrderItemlistBean orderItemlistBean, int position) {
                        holder.setImageUrl(holder.itemView.getContext(), R.id.productImg, orderItemlistBean.icon);
                        holder.setText(R.id.productTitle, orderItemlistBean.name);
                        if (LocalConfiguration.userInfo.getSourceType() == 1002) {
                            holder.getView(R.id.score).setVisibility(View.VISIBLE);
                            holder.setText(R.id.price, orderItemlistBean.discountnumber + "");
                        } else {
                            holder.setText(R.id.price, "¥ " + orderItemlistBean.prize);
                            holder.getView(R.id.score).setVisibility(View.GONE);
                        }
                        holder.setText(R.id.num, "x" + orderItemlistBean.prodNum);
                        holder.setText(R.id.productSecondTitle, "x" + orderItemlistBean.spec);
                        holder.getView(R.id.selectImg).setVisibility(View.GONE);
                        holder.getView(R.id.editBtn).setVisibility(View.INVISIBLE);
                    }

                };
                productList.setAdapter(adapter);
                holder.getView(R.id.orderBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", orderBO.id);
                        gotoActivity(OrderDetailActivity.class, bundle, false);
                    }
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }


    /**
     * 设置订单的状态
     */
    private void setTextStatus(TextView textView, String status, LGViewHolder holder, OrderBO orderBO) {
        switch (status) {
            case "create":  //待支付
                textView.setText("待支付");
                textView.setTextColor(Color.parseColor("#FF5A5A"));
                holder.getView(R.id.buttom_layout).setVisibility(View.VISIBLE);
                holder.getView(R.id.cancelBtn).setVisibility(View.VISIBLE);
                holder.getView(R.id.payBtn).setVisibility(View.VISIBLE);
                holder.getView(R.id.getProductBtn).setVisibility(View.GONE);
                holder.getView(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog(OrderActivity.this).builder().setGone().setMsg("确认取消订单？")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", v -> {
                                    mPresenter.cancleOrder(orderBO.id);
                                }).show();
                    }
                });
                holder.getView(R.id.payBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        payOrderId = orderBO.id;
                        mPresenter.pay(orderBO.id);
                    }
                });
                break;
            case "cancel":  //已取消
                textView.setText("已取消");
                textView.setTextColor(Color.parseColor("#BABABA"));
                holder.getView(R.id.buttom_layout).setVisibility(View.GONE);
                break;
            case "refunded":  //已退款
                textView.setText("已退款");
                textView.setTextColor(Color.parseColor("#BABABA"));
                holder.getView(R.id.buttom_layout).setVisibility(View.GONE);
                break;
            case "pay":    //已支付
                textView.setText("已付款");
                textView.setTextColor(Color.parseColor("#FF5A5A"));
                holder.getView(R.id.buttom_layout).setVisibility(View.VISIBLE);
                holder.getView(R.id.cancelBtn).setVisibility(View.GONE);
                holder.getView(R.id.payBtn).setVisibility(View.GONE);
                holder.getView(R.id.getProductBtn).setVisibility(View.VISIBLE);
                holder.setText(R.id.getProductBtn, "退款");
                holder.getView(R.id.getProductBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog(OrderActivity.this).builder().setGone().setMsg("是否确认退款？")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", v -> {
                                    mPresenter.paRefundy(orderBO.id);
                                }).show();
                    }
                });
                break;
            case "success":  //已完成
                textView.setText("已完成");
                textView.setTextColor(Color.parseColor("#BABABA"));
                holder.getView(R.id.buttom_layout).setVisibility(View.GONE);
                break;
            case "shipments":  //待确认
                textView.setText("待确认");
                textView.setTextColor(Color.parseColor("#D4AB56"));
                holder.getView(R.id.buttom_layout).setVisibility(View.VISIBLE);
                holder.getView(R.id.cancelBtn).setVisibility(View.GONE);
                holder.getView(R.id.payBtn).setVisibility(View.GONE);
                holder.getView(R.id.getProductBtn).setVisibility(View.VISIBLE);
                holder.setText(R.id.getProductBtn, "确认提货");
                holder.getView(R.id.getProductBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new AlertDialog(OrderActivity.this).builder().setGone().setMsg("是否确认已收货？")
                                .setNegativeButton("取消", null)
                                .setPositiveButton("确定", v -> {
                                    mPresenter.comfimOrder(orderBO.id);
                                }).show();
                    }
                });
                break;
        }
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
        viewBinding.refreshLayout.finishRefresh();
        viewBinding.refreshLayout.finishLoadMore();
        ToastUtils.showShortToast(msg);
    }

    @Override
    public void onRequestEnd() {

    }

    @Override
    public void getOrder(List<OrderBO> orderBOS) {
        viewBinding.refreshLayout.finishRefresh();
        viewBinding.refreshLayout.finishLoadMore();
        if (pageNum != 1) {
            this.data.addAll(orderBOS);
        } else {
            this.data = orderBOS;
        }
        setAdapter();
    }

    @Override
    public void cancleSuress() {
        ToastUtils.showShortToast("订单已取消！");
        pageNum = 1;
        mPresenter.getOrder(pageNum, type);
    }

    @Override
    public void comfimOrder() {
        ToastUtils.showShortToast("已确认收货！");
        pageNum = 1;
        mPresenter.getOrder(pageNum, type);
    }

    @Override
    public void getOrderDetails(OrderBO orderBO) {

    }

    @Override
    public void pay(PayBean orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderActivity.this);
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
        pageNum = 1;
        mPresenter.getOrder(pageNum, type);
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
                        bundle.putString("id", payOrderId);
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