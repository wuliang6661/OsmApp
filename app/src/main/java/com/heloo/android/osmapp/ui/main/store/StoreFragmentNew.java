package com.heloo.android.osmapp.ui.main.store;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FraStoreBinding;
import com.heloo.android.osmapp.model.ShopBannarBO;
import com.heloo.android.osmapp.model.ShopListBO;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.StoreContract;
import com.heloo.android.osmapp.mvp.presenter.StorePresenter;
import com.heloo.android.osmapp.ui.SearchShopActivity;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.ui.cart.CartActivity;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGViewHolder;
import com.stx.xhb.androidx.XBanner;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StoreFragmentNew extends MVPBaseFragment<StoreContract.View, StorePresenter, FraStoreBinding>
        implements StoreContract.View {

    private ShopBannarBO bannarBO;
    private ShopListBO shopListBO;
    private final List<String> bannerData = new ArrayList<>();
    private int selectLeft = 0;

    @Override
    public void onViewCreated(View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        mPresenter.getBanner();
        mPresenter.getClassify();
        getNumCar();
        showProgress("");
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.getClassify();
            mPresenter.getBanner();
            getNumCar();
        }
    }


    private void initViews() {
        viewBinding.leftRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        viewBinding.rightRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        ViewGroup.LayoutParams paramsBanner1 = viewBinding.banner.getLayoutParams();
        paramsBanner1.height = (int) (ScreenUtils.getScreenWidth() * 0.53);
        viewBinding.banner.setLayoutParams(paramsBanner1);
        //初始化banner
        initBanner(viewBinding.banner);
        viewBinding.banner.setAutoPlayAble(bannerData.size() > 1);
        viewBinding.banner.setPointsIsVisible(true);
        viewBinding.banner.setData(R.layout.home_banner_layout, bannerData, null);
        viewBinding.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goLogin()) {
                    gotoActivity(CartActivity.class, false);
                }
            }
        });
        viewBinding.searchLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoActivity(SearchShopActivity.class, false);
            }
        });
    }


    /**
     * 轮播图
     *
     * @param banner
     */
    private void initBanner(XBanner banner) {
        //设置广告图片点击事件
        banner.setOnItemClickListener((banner12, model, view, position) -> {
            if (bannarBO == null) {
                return;
            }
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.putExtra("url", bannarBO.banner.get(position).jumpUrl);
            startActivity(intent);
        });
        //加载广告图片
        banner.loadImage((banner1, model, view, position) -> {
            ImageView image = view.findViewById(R.id.image);
            Glide.with(getActivity()).load(bannerData.get(position)).into(image);
        });

    }


    @Override
    public void getClassify(ShopListBO body) {
        stopProgress();
        shopListBO = body;
        setLeftAdapter();
    }

    @Override
    public void getBanner(ShopBannarBO body) {
        stopProgress();
        bannarBO = body;
        if (body == null) {
            return;
        }
        for (ShopBannarBO.BannerBean item : body.banner) {
            bannerData.add(item.imgurl);
        }
        viewBinding.banner.setData(R.layout.home_banner_layout, bannerData, null);
    }


    private void setLeftAdapter() {
        LGRecycleViewAdapter<ShopListBO.ListBean> adapter = new LGRecycleViewAdapter<ShopListBO.ListBean>(shopListBO.list) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.item;
            }

            @Override
            public void convert(LGViewHolder holder, ShopListBO.ListBean listBean, int position) {
                TextView tv = (TextView) holder.getView(R.id.tv);
                tv.setText(listBean.name);
                if (selectLeft == position) {
                    tv.setTextColor(Color.parseColor("#D3A952"));
                    tv.setBackgroundResource(R.color.colorfff);
                } else {
                    tv.setTextColor(Color.parseColor("#333333"));
                    tv.setBackgroundResource(R.color.colorF8);
                }
            }
        };
        adapter.setOnItemClickListener(R.id.tv, (view, position) -> {
            selectLeft = position;
            adapter.notifyDataSetChanged();
            setRightAdapter(position);
        });
        setRightAdapter(0);
        viewBinding.leftRecycle.setAdapter(adapter);
    }


    private void setRightAdapter(int position) {
        LGRecycleViewAdapter<ShopListBO.ListBean.TypeGoodsMoelsBean> adapter =
                new LGRecycleViewAdapter<ShopListBO.ListBean.TypeGoodsMoelsBean>(shopListBO.list.get(position).typeGoodsMoels) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.item_goods;
                    }

                    @Override
                    public void convert(LGViewHolder holder, ShopListBO.ListBean.TypeGoodsMoelsBean typeGoodsMoelsBean, int position) {
                        RecyclerView productLayout = (RecyclerView) holder.getView(R.id.productLayout);
                        if (StringUtils.isEmpty(typeGoodsMoelsBean.name)) {
                            holder.getView(R.id.stick_header).setVisibility(View.GONE);
                        } else {
                            holder.getView(R.id.stick_header).setVisibility(View.VISIBLE);
                            holder.setText(R.id.tvHeader, typeGoodsMoelsBean.name);
                        }
                        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
                        productLayout.setLayoutManager(manager);
                        productLayout.setNestedScrollingEnabled(false);
                        productLayout.setAdapter(showShopAdapter(typeGoodsMoelsBean.goodsMoels));
                    }
                };
        viewBinding.rightRecycle.setAdapter(adapter);
    }


    private LGRecycleViewAdapter<ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean> showShopAdapter(List<ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean> shops) {
        LGRecycleViewAdapter<ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean> adapter =
                new LGRecycleViewAdapter<ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean>(shops) {
                    @Override
                    public int getLayoutId(int viewType) {
                        return R.layout.product_item_layout;
                    }

                    @Override
                    public void convert(LGViewHolder holder, ShopListBO.ListBean.TypeGoodsMoelsBean.GoodsMoelsBean goodsMoelsBean, int position) {
                        holder.setText(R.id.tvName, goodsMoelsBean.name);
                        holder.setText(R.id.tvPrice, String.format("￥%s", goodsMoelsBean.integralPrice));
                        if (goodsMoelsBean.icon.startsWith("http")) {
                            holder.setImageUrl(getActivity(), R.id.productImg, goodsMoelsBean.icon);
                        } else {
                            holder.setImageUrl(getActivity(), R.id.productImg, HttpInterface.URL + goodsMoelsBean.icon);
                        }
                    }
                };
        adapter.setOnItemClickListener(R.id.item_layout, new LGRecycleViewAdapter.ItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
//                        addCart(productImg);
                Intent intent = new Intent(getActivity(), StoreDetailActivity.class);
                intent.putExtra("id", shops.get(position).id);
                startActivity(intent);
            }
        });
        return adapter;
    }


    @Override
    public void onRequestError(String msg) {
        stopProgress();
        ToastUtils.showShortToast(msg);
    }


    public void getNumCar() {
        if (LocalConfiguration.userInfo == null) {
            return;
        }
        HttpInterfaceIml.getNumCar(LocalConfiguration.userInfo.getId() + "").subscribe(new HttpResultSubscriber<String>() {
            @Override
            public void onSuccess(String s) {
                if (viewBinding != null) {
                    if (Integer.parseInt(s) <= 0) {
                        viewBinding.cartNum.setVisibility(View.GONE);
                        return;
                    }
                    viewBinding.cartNum.setVisibility(View.VISIBLE);
                    viewBinding.cartNum.setText(s);
                }
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShortToast(message);
            }
        });
    }

}
