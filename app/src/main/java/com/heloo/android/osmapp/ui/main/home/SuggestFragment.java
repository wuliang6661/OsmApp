package com.heloo.android.osmapp.ui.main.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.heloo.android.osmapp.databinding.FragmentSuggestBinding;
import com.heloo.android.osmapp.model.ArticleBean;
import com.heloo.android.osmapp.model.BannerBean;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.SuggestContract;
import com.heloo.android.osmapp.mvp.presenter.SuggestPresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.ui.subject.SubjectActivity;
import com.heloo.android.osmapp.ui.subject.SubjectDetailActivity;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.StringUtils;
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
 * Description : 推荐
 *
 * @author WITNESS
 * @date 5/11/21
 */

public class SuggestFragment extends MVPBaseFragment<SuggestContract.View, SuggestPresenter, FragmentSuggestBinding>
        implements SuggestContract.View {

    private CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean> adapter;
    private ArticleBean articleBean;
    private List<ArticleBean.ArticleInfoListBean.DataBean> newsData = new ArrayList<>();
    private List<ArticleBean.ArticleInfoListBean.DataBean> suggestNewsData = new ArrayList<>();//推荐
    private List<ArticleBean.ArticleInfoListBean.DataBean> armyNewsData = new ArrayList<>();//五美党建
    private XBanner banner, banner2;
    private List<BannerBean.BannerBean2> bannerData = new ArrayList<>();
    private List<BannerBean.ArticlespecialBean> bannerData2 = new ArrayList<>();
    private TextView nowPage, totalPage, noticeTxt, moreBtn;
    private LinearLayout paperLayout;
    private LinearLayout noticeLayout;//公告
    private List<ArticleBean.BannerBean> armyBannerData = new ArrayList<>();
    //推荐
    private int pageNo = 1;
    private int pageSize = 20;
    //五美党建
    private int armyPageNo = 1;
    private int armyPageSize = 20;

    private String typeName;
    private String categoryId;//五美党建id
    private BannerBean bannerBean;

    private View device;


    public static SuggestFragment newInstance(String typeName, String categoryId) {
        SuggestFragment suggestFragment = new SuggestFragment();
        Bundle b = new Bundle();
        b.putString("typeName", typeName);
        b.putString("categoryId", categoryId);
        suggestFragment.setArguments(b);
        return suggestFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            typeName = args.getString("typeName");
            categoryId = args.getString("categoryId");
        }
        initViews();
        if (typeName != null && typeName.equals("五美党建")) {
            newsData.clear();
            moreBtn.setVisibility(View.GONE);
            mPresenter.getList(MyApplication.spUtils.getString("token", ""), armyPageNo, armyPageSize, categoryId, "", "");
        } else {
            newsData.clear();
            moreBtn.setVisibility(View.VISIBLE);
            mPresenter.getList(MyApplication.spUtils.getString("token", ""), pageNo, pageSize, "", "Y", "");
            mPresenter.getBanner(MyApplication.spUtils.getString("token", ""));
        }
    }

    private void initViews() {
        View headView = LayoutInflater.from(getActivity()).inflate(R.layout.suggest_header_layout, null);
        RelativeLayout banner1Layout = headView.findViewById(R.id.banner1Layout);
        RelativeLayout banner2Layout = headView.findViewById(R.id.banner2Layout);
        banner = headView.findViewById(R.id.banner);
        banner2 = headView.findViewById(R.id.banner2);
        nowPage = headView.findViewById(R.id.nowPage);
        totalPage = headView.findViewById(R.id.totalPage);
        noticeTxt = headView.findViewById(R.id.noticeTxt);
        paperLayout = headView.findViewById(R.id.paperLayout);
        noticeLayout = headView.findViewById(R.id.noticeLayout);
        moreBtn = headView.findViewById(R.id.moreBtn);
        device = headView.findViewById(R.id.device);
        noticeTxt.setSelected(true);
        viewBinding.list.addHeaderView(headView);
        viewBinding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                refreshLayout.finishLoadMore(100);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (typeName != null && typeName.equals("五美党建")) {
                    armyPageNo = 1;
                    mPresenter.getList(MyApplication.spUtils.getString("token", ""), armyPageNo, armyPageSize, categoryId, "", "");
                } else {
                    pageNo = 1;
                    mPresenter.getBanner(MyApplication.spUtils.getString("token", ""));
                    mPresenter.getList(MyApplication.spUtils.getString("token", ""), pageNo, pageSize, "", "Y", "");
                }
                refreshLayout.finishRefresh(100);
            }
        });
        viewBinding.list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    View last_view = viewBinding.list.getChildAt(viewBinding.list.getChildCount() - 1);
                    if (last_view != null && last_view.getBottom() == viewBinding.list.getHeight()) {
                        if (typeName != null && typeName.equals("五美党建")) {
                            armyPageNo++;
                            mPresenter.getList(MyApplication.spUtils.getString("token", ""), armyPageNo, armyPageSize, categoryId, "", "");
                        } else {
                            pageNo++;
                            mPresenter.getList(MyApplication.spUtils.getString("token", ""), pageNo, pageSize, "", "Y", "");
                        }
                    }
                }
            }
        });
        moreBtn.setOnClickListener(v -> {//更多
//            if (goLogin()) {
            startActivity(new Intent(getActivity(), SubjectActivity.class));
//            }
        });
    }

    private void setAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean>(getActivity(), R.layout.news_item_layout, newsData) {
            @Override
            protected void convert(ViewHolder holder, ArticleBean.ArticleInfoListBean.DataBean item, int position) {
                TextView title = holder.getConvertView().findViewById(R.id.title);
                ShapeableImageView imageView = holder.getConvertView().findViewById(R.id.image);
                if (item.getIcon().startsWith("http")) {
                    Glide.with(getActivity()).load(item.getIcon()).into(imageView);
                } else {
                    Glide.with(getActivity()).load(HttpInterface.IMG_URL + item.getIcon()).into(imageView);
                }

                if (item.getIsTop() != null && item.getIsTop().equals("1")) {
                    holder.getView(R.id.hotTxt).setVisibility(View.VISIBLE);
                    if (typeName != null && typeName.equals("五美党建")) {
                        holder.setText(R.id.hotTxt, "置顶");
                        SpannableStringBuilder span = new SpannableStringBuilder("缩进啊" + item.getSubject());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 3,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        title.setText(span);
                    } else {
                        holder.setText(R.id.hotTxt, "热");
                        SpannableStringBuilder span = new SpannableStringBuilder("缩进" + item.getSubject());
                        span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
                                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        title.setText(span);
                    }
                } else {
                    holder.getView(R.id.hotTxt).setVisibility(View.GONE);
                    title.setText(item.getSubject());
                }

                holder.setText(R.id.time, item.getCreateDate());
                holder.setText(R.id.glance, item.getViewNum());
                holder.getView(R.id.newItemBtn).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                            intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId()
                                    + "&uid=" + LocalConfiguration.userInfo.getUid() + "&username=" + LocalConfiguration.userInfo.getUsername() + "&app=1");
                        } else {
                            intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticleId() + "&app=1");
                        }
                        startActivity(intent);
                    }
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }


    /**
     * 轮播图
     *
     * @param banner
     */
    private void initBanner(XBanner banner) {
        banner.setOnItemClickListener((banner12, model, view, position) -> {
            if (typeName != null && typeName.equals("五美党建")) {
                if (armyBannerData.get(position).getBannerType() == 1
                        || armyBannerData.get(position).getBannerType() == 2
                        || armyBannerData.get(position).getBannerType() == 5) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + armyBannerData.get(position).getJumpId()
                                + "&uid=" + LocalConfiguration.userInfo.getUid() + "&username=" + LocalConfiguration.userInfo.getUsername() + "&app=1");
                    } else {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + armyBannerData.get(position).getJumpId() + "&app=1");
                    }
                    startActivity(intent);
                } else {
                    if (armyBannerData.get(position).getJumpUrl() != null
                            && armyBannerData.get(position).getJumpUrl().length() > 10) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra("url", armyBannerData.get(position).getJumpUrl());
                        startActivity(intent);
                    }
                }
            } else {
                if (bannerData.get(position).getJumpUrl() != null
                        && bannerData.get(position).getJumpUrl().length() > 10) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra("url", bannerData.get(position).getJumpUrl());
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + bannerData.get(position).getJumpId()
                                + "&uid=" + LocalConfiguration.userInfo.getUid() + "&username=" + LocalConfiguration.userInfo.getUsername() + "&app=1");
                    } else {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + bannerData.get(position).getJumpId() + "&app=1");
                    }
                    startActivity(intent);
                }
            }
        });
        banner.loadImage((banner1, model, view, position) -> {
            ImageView image = view.findViewById(R.id.image);
            TextView desc = view.findViewById(R.id.desc);
            if (typeName != null && typeName.equals("五美党建")) {
                if (armyBannerData.get(position).getImgurl().startsWith("http")) {
                    Glide.with(getActivity()).load(armyBannerData.get(position).getImgurl()).into(image);
                } else {
                    Glide.with(getActivity()).load(HttpInterface.IMG_URL + armyBannerData.get(position).getImgurl()).into(image);
                }
//                desc.setText(armyBannerData.get(position).getSubject());
            } else {
                if (bannerData.get(position).getImgurl().startsWith("http")) {
                    Glide.with(getActivity()).load(bannerData.get(position).getImgurl()).into(image);
                } else {
                    Glide.with(getActivity()).load(HttpInterface.IMG_URL + bannerData.get(position).getImgurl()).into(image);
                }
                desc.setText(bannerData.get(position).getSubject());
                nowPage.setText(String.valueOf(position + 1));
            }
        });

    }


    /**
     * 专题轮播图
     *
     * @param banner
     */
    private void initBanner2(XBanner banner) {
        banner.setOnItemClickListener((banner12, model, view, position) -> {
            if (StringUtils.isEmpty(bannerData2.get(position).getWebUrl())) {
                Intent intent = new Intent(getActivity(), SubjectDetailActivity.class);
                intent.putExtra("id", bannerData2.get(position).getId());
                startActivity(intent);
            } else {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url", bannerData2.get(position).getWebUrl());
                startActivity(intent);
            }
        });
        banner.loadImage((banner1, model, view, position) -> {
            ShapeableImageView image = view.findViewById(R.id.image);
            if (bannerData2.get(position).getIcon().startsWith("http")) {
                Glide.with(getActivity()).load(bannerData2.get(position).getIcon()).into(image);
            } else {
                Glide.with(getActivity()).load(HttpInterface.IMG_URL + bannerData2.get(position).getIcon()).into(image);
            }
        });
    }

    @Override
    public void onRequestError(String msg) {
        stopProgress();
    }

    @Override
    public void onRequestEnd() {
        stopProgress();
    }

    @Override
    public void getList(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")) {
            articleBean = JSON.parseObject(jsonObject.optString("data"), ArticleBean.class);
            if (typeName != null && typeName.equals("五美党建")) {
                device.setVisibility(View.VISIBLE);
                noticeLayout.setVisibility(View.GONE);
                if (armyPageNo == 1) {
                    armyNewsData.clear();
                    newsData.clear();
                    if (articleBean.getBanner() != null) {
                        //初始化banner
                        armyBannerData.clear();
                        armyBannerData.addAll(articleBean.getBanner());
                        paperLayout.setVisibility(View.GONE);
                        totalPage.setText(String.valueOf(armyBannerData.size()));
                        initBanner(banner);
                        banner.setAutoPlayAble(armyBannerData.size() > 1);
                        banner.setPointsIsVisible(false);
                        banner.setData(R.layout.home_banner_layout3, armyBannerData, null);//banner_image_layout
                    }
                }
                if (articleBean.getRecommend() != null) {
                    BannerBean.ArticlespecialBean bean = new BannerBean.ArticlespecialBean();
                    bean.setIcon(articleBean.getRecommend().icon);
                    bean.setId(articleBean.getRecommend().id);
                    bean.setWebUrl(articleBean.getRecommend().url);
                    bannerData2.clear();
                    bannerData2.add(bean);
                    initBanner2(banner2);
                    banner2.setAutoPlayAble(bannerData2.size() > 1);
                    banner2.setPointsIsVisible(bannerData2.size() > 1);
                    banner2.setPointPosition(XBanner.LEFT);
                    banner2.setData(R.layout.home_banner_layout2, bannerData2, null);//banner_image_layout2
                }
                armyNewsData.addAll(articleBean.getArticleInfoList().getData());
                newsData.addAll(armyNewsData);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            } else {
                noticeLayout.setVisibility(View.VISIBLE);
                device.setVisibility(View.GONE);
                if (pageNo == 1) {
                    suggestNewsData.clear();
                    newsData.clear();
                }
                suggestNewsData.addAll(articleBean.getArticleInfoList().getData());
                newsData.addAll(suggestNewsData);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
            setAdapter();
        }
    }

    @Override
    public void getBanner(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")) {
            bannerBean = JSON.parseObject(jsonObject.optString("data"), BannerBean.class);
            noticeTxt.setText(String.format("%s", bannerBean.getAnnouncement().getContent() + "                    "));
            bannerData.clear();
            bannerData.addAll(bannerBean.getBanner());
            bannerData2.clear();
            bannerData2.addAll(bannerBean.getArticlespecial());
            totalPage.setText(String.valueOf(bannerData.size()));
            //初始化banner
            paperLayout.setVisibility(View.VISIBLE);
            initBanner(banner);
            banner.setAutoPlayAble(bannerData.size() > 1);
            banner.setPointsIsVisible(false);
            banner.setData(R.layout.home_banner_layout3, bannerData, null);//banner_image_layout
            //初始化专题banner
            initBanner2(banner2);
            banner2.setAutoPlayAble(bannerData2.size() > 1);
            banner2.setPointsIsVisible(bannerData2.size() > 1);
            banner2.setPointPosition(XBanner.LEFT);
            banner2.setData(R.layout.home_banner_layout2, bannerData2, null);//banner_image_layout2
        }
    }
}