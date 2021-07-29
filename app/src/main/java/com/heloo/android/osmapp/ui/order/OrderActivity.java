package com.heloo.android.osmapp.ui.order;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.databinding.ActivityOrderBinding;
import com.heloo.android.osmapp.model.OrderBO;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.OrderContract;
import com.heloo.android.osmapp.mvp.presenter.OrderPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.AlertDialog;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGViewHolder;

import java.util.ArrayList;
import java.util.List;

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
                holder.setText(R.id.price, "共" + orderBO.goodsNumber + "件商品合计:￥ " + orderBO.totalFee);
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
                        holder.setText(R.id.price, "￥ " + orderItemlistBean.prize);
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
}