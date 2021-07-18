package com.heloo.android.osmapp.ui.main.nice;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FragmentPictureBinding;
import com.heloo.android.osmapp.model.PicturesBean;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.PictureContract;
import com.heloo.android.osmapp.mvp.presenter.PicturePresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.stx.xhb.androidx.XBanner;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 图集
 */
public class PictureFragment extends MVPBaseFragment<PictureContract.View, PicturePresenter, FragmentPictureBinding>
    implements PictureContract.View{

    private CommonAdapter<PicturesBean.ListBean.DataBean> adapter;
    private List<PicturesBean.ListBean.DataBean> data = new ArrayList<>();
    private XBanner banner;
    private List<PicturesBean.BannerBean> bannerData = new ArrayList<>();
    private int pageNo = 1;
    private int pageSize = 20;
    private PicturesBean picturesBean;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mPresenter.getPictures(MyApplication.spUtils.getString("token", ""),pageNo,pageSize);
    }

    private void initView() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.picture_header_layout, null);
        banner = view.findViewById(R.id.banner);
        ViewGroup.LayoutParams paramsBanner1 = banner.getLayoutParams();
        paramsBanner1.height = (int)(ScreenUtils.getScreenWidth() * 0.53);
        banner.setLayoutParams(paramsBanner1);
        viewBinding.list.addHeaderView(view);
        viewBinding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo ++;
                mPresenter.getPictures(MyApplication.spUtils.getString("token", ""),pageNo,pageSize);
                refreshLayout.finishLoadMore(1000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                mPresenter.getPictures(MyApplication.spUtils.getString("token", ""),pageNo,pageSize);
                refreshLayout.finishRefresh(1000);
            }
        });
    }

    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<PicturesBean.ListBean.DataBean>(getActivity(),R.layout.picture_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, PicturesBean.ListBean.DataBean item, int position) {
                ShapeableImageView image = holder.getConvertView().findViewById(R.id.image);
                if (item.getIcon().startsWith("http")) {
                    Glide.with(getActivity()).asBitmap().load(item.getIcon()).into(image);
                }else {
                    Glide.with(getActivity()).asBitmap().load(HttpInterface.IMG_URL+item.getIcon()).into(image);
                }
                holder.setText(R.id.picTitle,item.getSubject());
                image.setOnClickListener(v -> {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId()
                                + "&uid=" + LocalConfiguration.userInfo.getUid()+ "&username=" + LocalConfiguration.userInfo.getUsername()+"&app=1");
                    }else {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId()+"&app=1");
                    }
                    startActivity(intent);
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
    }

    @Override
    public void onRequestEnd() {
        stopProgress();
    }

    /**
     * 轮播图
     * @param banner
     */
    private void initBanner(XBanner banner) {
        //设置广告图片点击事件
        banner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner12, Object model, View view, int position) {
                if (bannerData.get(position).getBannerType() == 1
                        || bannerData.get(position).getBannerType() == 2
                        || bannerData.get(position).getBannerType() == 5){
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + bannerData.get(position).getJumpId()
                                + "&uid=" + LocalConfiguration.userInfo.getUid()+ "&username=" + LocalConfiguration.userInfo.getUsername()+"&app=1");
                    }else {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + bannerData.get(position).getJumpId()+"&app=1");
                    }
                    startActivity(intent);
                }else {
                    if (bannerData.get(position).getJumpUrl() != null
                            && bannerData.get(position).getJumpUrl().length() > 10) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra("url", bannerData.get(position).getJumpUrl());
                        startActivity(intent);
                    }
                }
            }
        });
        //加载广告图片
        banner.loadImage((banner1, model, view, position) -> {
            ImageView image = view.findViewById(R.id.image);
            Glide.with(getActivity()).load(bannerData.get(position).getImgurl()).into(image);
        });

    }

    @Override
    public void getPictures(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            picturesBean = JSON.parseObject(jsonObject.optString("data"),PicturesBean.class);
            if (pageNo == 1) {
                bannerData.clear();
                bannerData.addAll(picturesBean.getBanner());
                data.clear();
                data.addAll(picturesBean.getList().getData());
                //初始化banner
                initBanner(banner);
                banner.setAutoPlayAble(bannerData.size() > 1);
                banner.setPointsIsVisible(true);
                banner.setData(R.layout.home_banner_layout, bannerData, null);//banner_image_layout
            }else {
                data.addAll(picturesBean.getList().getData());
            }
            setAdapter();
        }
    }
}