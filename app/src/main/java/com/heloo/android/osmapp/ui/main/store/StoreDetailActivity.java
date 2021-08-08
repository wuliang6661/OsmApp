package com.heloo.android.osmapp.ui.main.store;


import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityStoreDetailBinding;
import com.heloo.android.osmapp.model.ShopCarBO;
import com.heloo.android.osmapp.model.ShopDetailsBO;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.StoreDetailContract;
import com.heloo.android.osmapp.mvp.presenter.StoreDetailPresenter;
import com.heloo.android.osmapp.ui.cart.CartActivity;
import com.heloo.android.osmapp.ui.confirm.ConfirmActivity;
import com.heloo.android.osmapp.utils.HtmlFormat;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.utils.webview.WebAppInterface;
import com.heloo.android.osmapp.utils.webview.WebClient;
import com.heloo.android.osmapp.utils.webview.WebViewChromeClient;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 商品详情
 */
public class StoreDetailActivity extends MVPBaseActivity<StoreDetailContract.View, StoreDetailPresenter, ActivityStoreDetailBinding>
        implements StoreDetailContract.View, View.OnClickListener {

    private List<String> bannerData = new ArrayList<>();
    private ShopDetailsBO.ListBean productDetailBean;
    private Dialog noticeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        goBack();
        viewBinding.cartBtn.setOnClickListener(this);
        viewBinding.addCart.setOnClickListener(this);
        viewBinding.buyBtn.setOnClickListener(this);
        viewBinding.selectSKUBtn.setOnClickListener(this);
        ViewGroup.LayoutParams paramsBanner1 = viewBinding.banner.getLayoutParams();
        paramsBanner1.height = ScreenUtils.getScreenWidth();
        viewBinding.banner.setLayoutParams(paramsBanner1);
        initWebView(viewBinding.webView);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getDetail(getIntent().getStringExtra("id"));
        getNumCar();
    }


    /**
     * 初始化webview的实例
     */
    public void initWebView(WebView view) {
        initWebViewSettings(view);
        WebViewChromeClient chromeClient = new WebViewChromeClient(this);
        view.setWebChromeClient(chromeClient);
        view.setWebViewClient(new WebClient(this));
        view.addJavascriptInterface(new WebAppInterface(this, view), "Android");
        view.setClickable(true);
        view.setHorizontalScrollBarEnabled(false);
        view.setVerticalScrollBarEnabled(false);
        view.getSettings().setTextZoom(100);
    }

    /**
     * 初始化webview的各种属性
     */
    private void initWebViewSettings(WebView mWebView) {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
        webSetting.setAppCachePath(this.getDir("appcache", 0).getPath());
        webSetting.setDatabasePath(this.getDir("databases", 0).getPath());
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .getPath());
        CookieSyncManager.createInstance(this);
        CookieSyncManager.getInstance().sync();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cartBtn:
                gotoActivity(CartActivity.class, false);
                break;
            case R.id.addCart:
            case R.id.buyBtn:
            case R.id.selectSKUBtn:
                skuDialog(bannerData);
                break;
        }
    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
        ToastUtils.showShortToast(msg);
    }

    @Override
    public void onRequestEnd() {
        stopProgress();
    }

    /**
     * sku
     */
    private int selectPosition = -1;
    private int productNum = 1;

    private void skuDialog(List<String> data) {
        selectPosition = -1;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialog);
        final LayoutInflater inflater = LayoutInflater.from(this);
        View v = inflater.inflate(R.layout.sku_dialog_layout, null);
        ImageButton close = v.findViewById(R.id.closeBtn);
        ImageButton delBtn = v.findViewById(R.id.delBtn);
        ImageButton addBtn = v.findViewById(R.id.addBtn);
        Button addCart = v.findViewById(R.id.addCart);
        Button buyBtn = v.findViewById(R.id.buyBtn);
        TextView productName = v.findViewById(R.id.productName);
        TextView price = v.findViewById(R.id.price);
        TextView leftNum = v.findViewById(R.id.leftNum);
        TextView numTxt = v.findViewById(R.id.numTxt);
        ImageView productImg = v.findViewById(R.id.productImg);
        if (productDetailBean != null) {
            productName.setText(productDetailBean.name);
            price.setText(String.format("¥%s", productDetailBean.preferentialPrice));
            leftNum.setText(String.format("库存%s件", productDetailBean.freeNum));
            if (!productDetailBean.icon.startsWith("http")) {
                productDetailBean.icon = HttpInterface.IMG_URL + productDetailBean.icon;
            }
            Glide.with(this).load(productDetailBean.icon).placeholder(R.drawable.default_head)
                    .error(R.drawable.default_head).into(productImg);
            if (productDetailBean.freeNum <= 0) {
                addCart.setEnabled(false);
                buyBtn.setEnabled(false);
            }
        }
        productNum = 1;
        numTxt.setText(String.valueOf(productNum));
        RecyclerView list = v.findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(StoreDetailActivity.this));
        CommonAdapter<String> adapter = new CommonAdapter<String>(this, R.layout.sku_item_layout, data) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView text = holder.getConvertView().findViewById(R.id.text);
                if (selectPosition == position) {
                    text.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.submit_bg, null));
                    text.setTextColor(Color.parseColor("#FFFFFF"));
                } else {
                    text.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.cancel_order_bg, null));
                    text.setTextColor(Color.parseColor("#454545"));
                }
                text.setOnClickListener(v12 -> {
                    selectPosition = position;
                    notifyDataSetChanged();
                });
            }
        };
        list.setAdapter(adapter);

        builder.setView(v);
        builder.setCancelable(true);
        noticeDialog = builder.create();
        noticeDialog.getWindow().setGravity(Gravity.BOTTOM);
        noticeDialog.getWindow().setWindowAnimations(R.style.anim_menu_bottombar);
        noticeDialog.show();

        close.setOnClickListener(view -> noticeDialog.dismiss());

        delBtn.setOnClickListener(v14 -> {//减少
            if (productNum > 1) {
                productNum--;
                numTxt.setText(String.valueOf(productNum));
            }
        });

        addBtn.setOnClickListener(v13 -> {//增加
            productNum++;
            numTxt.setText(String.valueOf(productNum));
        });

        buyBtn.setOnClickListener(v1 -> {
            noticeDialog.dismiss();
            Bundle bundle = new Bundle();
            ArrayList<ShopCarBO.ShopCarInfoDOSBean> shops = new ArrayList<>();
            ShopCarBO.ShopCarInfoDOSBean item = new ShopCarBO.ShopCarInfoDOSBean();
            item.goodsNum = productNum;
            item.goodsPrice = productDetailBean.preferentialPrice;
            item.goodsId = getIntent().getStringExtra("id");
            item.goodsImg = productDetailBean.icon;
            item.goodsName = productDetailBean.name;
            item.id = getIntent().getStringExtra("id");
            shops.add(item);
            bundle.putSerializable("shops", shops);
            gotoActivity(ConfirmActivity.class, bundle, false);
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress("");
                if (goLogin()) {
                    mPresenter.addCart(getIntent().getStringExtra("id"), LocalConfiguration.userInfo.getId() + "", String.valueOf(productNum));
                }
            }
        });
        WindowManager.LayoutParams layoutParams = noticeDialog.getWindow().getAttributes();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        noticeDialog.getWindow().setAttributes(layoutParams);
    }

    @Override
    public void getDetail(ShopDetailsBO body) {
        productDetailBean = body.list;
        if (productDetailBean != null) {
            viewBinding.title.setText(productDetailBean.name);
            viewBinding.price.setText(String.format("￥%s", productDetailBean.preferentialPrice));
            viewBinding.shichangLayout.setVisibility(productDetailBean.price == 0 ? View.INVISIBLE : View.VISIBLE);
            viewBinding.oldPrice.setText(String.format("￥%s", productDetailBean.price));
            viewBinding.oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //中划线
//            bannerData.add(productDetailBean.icon);
            if (!productDetailBean.icon.startsWith("http")) {
                productDetailBean.icon = HttpInterface.IMG_URL + productDetailBean.icon;
            }
            Glide.with(this)
                    .load(productDetailBean.icon)
                    .placeholder(R.drawable.default_head)
                    .error(R.drawable.default_head)
                    .into(viewBinding.banner);
            //初始化banner
//            initBanner(viewBinding.banner);
            String detailUrl = null;
            try {
                if (!StringUtils.isEmpty(productDetailBean.description)) {
                    detailUrl = new String(Base64.decode(productDetailBean.description.getBytes("UTF-8"), Base64.DEFAULT));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            viewBinding.webView.loadDataWithBaseURL(null, HtmlFormat.getNewContent(detailUrl), "text/html", "utf-8", null);//加载html数据

        }
    }

    @Override
    public void getAddCart(String body) {
        stopProgress();
        if (noticeDialog != null)
            noticeDialog.dismiss();
        ToastUtils.showShortToast("已加入购物车！");
        getNumCar();
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
                        viewBinding.carNum.setVisibility(View.GONE);
                        return;
                    }
                    viewBinding.carNum.setVisibility(View.VISIBLE);
                    viewBinding.carNum.setText(s);
                }
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShortToast(message);
            }
        });
    }
}