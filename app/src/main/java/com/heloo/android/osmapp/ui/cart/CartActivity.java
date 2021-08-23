package com.heloo.android.osmapp.ui.cart;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityCartBinding;
import com.heloo.android.osmapp.model.ShopCarBO;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.CartContract;
import com.heloo.android.osmapp.mvp.presenter.CartPresenter;
import com.heloo.android.osmapp.ui.confirm.ConfirmActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.AlertDialog;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

/**
 * 购物车
 *
 * @auther WITNESS 2021/4/13
 */
public class CartActivity extends MVPBaseActivity<CartContract.View, CartPresenter, ActivityCartBinding>
        implements CartContract.View, View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private CommonAdapter<ShopCarBO.ShopCarInfoDOSBean> adapter;
    private int editPosition = -1;//编辑
    private boolean isEdit = false;
    private ShopCarBO shopCarBO;
    private Map<String, ShopCarBO.ShopCarInfoDOSBean> selectShop = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        goBack();
        mPresenter.getShopCar();
        mPresenter.getUserIntegration();
        initView();
    }

    private void initView() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.list.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.manageBtn.setOnClickListener(this);
        viewBinding.submitBtn.setOnClickListener(this);
        viewBinding.refreshLayout.setOnRefreshListener(refreshLayout -> mPresenter.getShopCar());
        viewBinding.selectAll.setOnCheckedChangeListener(this);
    }


    private void setAdapter() {
        adapter = new CommonAdapter<ShopCarBO.ShopCarInfoDOSBean>(this, R.layout.cart_item_layout, shopCarBO.shopCarInfoDOS) {
            @Override
            protected void convert(ViewHolder holder, ShopCarBO.ShopCarInfoDOSBean s, int position) {
                CheckBox selectImg = holder.getView(R.id.selectImg);
                holder.setText(R.id.productTitle, s.goodsName);
                holder.setText(R.id.editTitle, s.goodsName);
                if (LocalConfiguration.userInfo.getSourceType() == 1002) {
                    holder.getView(R.id.score).setVisibility(View.VISIBLE);
                    holder.setText(R.id.price,  s.integralPrice + "");
                } else {
                    holder.setText(R.id.price, "¥ " + s.goodsPrice);
                }
                holder.setText(R.id.num, "x " + s.goodsNum);
                holder.setText(R.id.editNum, s.goodsNum + "");
                if(!s.goodsImg.startsWith("http")){
                    s.goodsImg = HttpInterface.IMG_URL + s.goodsImg;
                }
                Glide.with(CartActivity.this).load(s.goodsImg)
                        .placeholder(R.drawable.default_head)
                        .error(R.drawable.default_head)
                        .into((ImageView) holder.getView(R.id.productImg));
                EditText editNum = holder.getView(R.id.editNum);
                if (position == editPosition) {
                    holder.getView(R.id.normalPart).setVisibility(View.GONE);
                    holder.getView(R.id.editLayout).setVisibility(View.VISIBLE);
                } else {
                    holder.getView(R.id.normalPart).setVisibility(View.VISIBLE);
                    holder.getView(R.id.editLayout).setVisibility(View.GONE);
                }
                if (selectShop.containsKey(s.id)) {
                    selectImg.setChecked(true);
                } else {
                    selectImg.setChecked(false);
                }
                holder.getView(R.id.delBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (s.goodsNum > 0) {
                            s.goodsNum--;
                            notifyDataSetChanged();
                        }
                    }
                });
                holder.getView(R.id.addBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        s.goodsNum++;
                        notifyDataSetChanged();
                    }
                });
                selectImg.setOnCheckedChangeListener((compoundButton, b) -> {
                    if (b) {
                        selectShop.put(s.id, s);
                    } else {
                        selectShop.remove(s.id);
                    }
                    viewBinding.selectAll.setOnCheckedChangeListener(null);
                    if (shopCarBO.shopCarInfoDOS.size() == selectShop.size()) {
                        viewBinding.selectAll.setChecked(true);
                    } else {
                        viewBinding.selectAll.setChecked(false);
                    }
                    viewBinding.selectAll.setOnCheckedChangeListener(CartActivity.this);
                    showPriceAndNum();
                });
                holder.getView(R.id.editBtn).setOnClickListener(v -> {// 编辑
                    editPosition = position;
                    notifyDataSetChanged();
                });
                holder.getView(R.id.doneBtn).setOnClickListener(v -> {//完成
                    String num = editNum.getText().toString().trim();
                    if (StringUtils.isEmpty(num)) {
                        ToastUtils.showShortToast("商品数量不能为空！");
                        return;
                    }
                    if (Integer.parseInt(num) <= 0) {
                        ToastUtils.showShortToast("商品数量不能小于0！");
                        return;
                    }
                    if (selectShop.containsKey(s.id)) {
                        selectShop.get(s.id).goodsNum = Integer.parseInt(num);
                    }
                    editPosition = -1;
                    notifyDataSetChanged();
                    mPresenter.getUpdateNum(s.id, num + "");
                });
                holder.getView(R.id.txt_delete).setOnClickListener(v -> {// 删除
                    new AlertDialog(CartActivity.this).builder().setGone().setMsg("确认删除商品？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", v1 -> {
                                selectShop.remove(s.id);
                                mPresenter.delCar(s.id);
                            }).show();
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }

    @Override
    public void onRequestError(String msg) {
        super.onRequestError(msg);
        viewBinding.refreshLayout.finishRefresh();
    }

    @Override
    public void getCar(ShopCarBO carBO) {
        viewBinding.refreshLayout.finishRefresh();
        shopCarBO = carBO;
        setAdapter();
    }

    private void showPriceAndNum() {
        if (isEdit) return;
        if (selectShop.size() == 0) {
            viewBinding.totalPrice.setText("¥ 0.00");
        } else {
            double price = 0;
            for (String key : selectShop.keySet()) {
                if (LocalConfiguration.userInfo.getSourceType() == 1002) {
                    price += (selectShop.get(key).integralPrice * selectShop.get(key).goodsNum);
                } else {
                    price += (selectShop.get(key).goodsPrice * selectShop.get(key).goodsNum);
                }
            }
            if (LocalConfiguration.userInfo.getSourceType() == 1002) {
                viewBinding.totalPrice.setText((int) price + "");
                viewBinding.score.setVisibility(View.VISIBLE);
            } else {
                viewBinding.totalPrice.setText("¥ " + price);
                viewBinding.score.setVisibility(View.GONE);
            }
        }
        viewBinding.submitBtn.setText("结算");
    }

    @Override
    public void deleteShopSource() {
        mPresenter.getShopCar();
    }

    @Override
    public void getUserInner(UserInfo info) {
        LocalConfiguration.userInfo = info;
        viewBinding.myPoints.setText("（当前用户可用珍币数" + info.getIntegration() + "）");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manageBtn://管理
                if (isEdit) {
                    viewBinding.manageBtn.setText("管理");
                    viewBinding.priceLayout.setVisibility(View.VISIBLE);
                    viewBinding.submitBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.submit_bg, null));
                    viewBinding.submitBtn.setTextColor(Color.parseColor("#ffffff"));
                    showPriceAndNum();
                    mPresenter.getShopCar();
                    isEdit = false;
                } else {
                    viewBinding.manageBtn.setText("取消");
                    viewBinding.priceLayout.setVisibility(View.GONE);
                    viewBinding.submitBtn.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.delete_bg, null));
                    viewBinding.submitBtn.setTextColor(Color.parseColor("#D3A952"));
                    viewBinding.submitBtn.setText("删除");
                    isEdit = true;
                }
                break;
            case R.id.submitBtn:
                if (selectShop.size() == 0) {
                    ToastUtils.showShortToast("请选择商品！");
                    return;
                }
                if (isEdit) {
                    new AlertDialog(CartActivity.this).builder().setGone().setMsg("确认删除商品？")
                            .setNegativeButton("取消", null)
                            .setPositiveButton("确定", v1 -> {
                                StringBuilder builder = new StringBuilder();
                                for (String key : selectShop.keySet()) {
                                    builder.append(key).append(",");
                                }
                                mPresenter.batchDelete(builder.substring(0, builder.length() - 1));
                            }).show();
                } else {
                    ArrayList<ShopCarBO.ShopCarInfoDOSBean> shops = new ArrayList<>();
                    shops.addAll(selectShop.values());
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("shops", shops);
                    gotoActivity(ConfirmActivity.class, bundle, false);
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
            if (shopCarBO != null) {
                for (ShopCarBO.ShopCarInfoDOSBean item : shopCarBO.shopCarInfoDOS) {
                    selectShop.put(item.id, item);
                }
                setAdapter();
            }
        } else {
            selectShop.clear();
            setAdapter();
        }
        showPriceAndNum();
    }
}