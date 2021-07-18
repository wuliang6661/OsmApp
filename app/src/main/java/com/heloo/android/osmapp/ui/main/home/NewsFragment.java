package com.heloo.android.osmapp.ui.main.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FragmentNewsBinding;
import com.heloo.android.osmapp.model.ArticleBean;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.NewsContract;
import com.heloo.android.osmapp.mvp.presenter.NewsPresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
/**
 * Description : 新闻资讯
 *
 * @author WITNESS
 * @date 5/11/21
 */
public class NewsFragment extends MVPBaseFragment<NewsContract.View, NewsPresenter, FragmentNewsBinding>
        implements NewsContract.View{

    private ArticleBean articleBean;
    private CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean> adapter;
    private CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean> adapter2;
    private List<ArticleBean.ArticleInfoListBean.DataBean> firstNewsData = new ArrayList<>();
    private List<ArticleBean.ArticleInfoListBean.DataBean> secondNewsData = new ArrayList<>();
    private String categoryId;
    private int firstPageNo = 1;
    private int firstPageSize = 20;
    private int secondPageNo = 1;
    private int secondPageSize = 20;
    private String firstCategoryId;//第一个分类id
    private String secondCategoryId;//第二个分类id
    private boolean isFirst = true;//当前是否选中第一个标签


    public static NewsFragment newInstance(String categoryId) {
        NewsFragment newsFragment = new NewsFragment();
        Bundle b = new Bundle();
        b.putString("categoryId",categoryId);
        newsFragment.setArguments(b);
        return newsFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            categoryId = args.getString("categoryId");
        }
        initViews();
    }

    private void initViews() {
        for (int i=0;i< LocalConfiguration.titleBeanList.size();i++){
            if (categoryId.equals(LocalConfiguration.titleBeanList.get(i).getCategoryId())){
                if (LocalConfiguration.titleBeanList.get(i).getChildren().size() >= 2){
                    viewBinding.newsBtn.setText(LocalConfiguration.titleBeanList.get(i).getChildren().get(0).getName());
                    viewBinding.infoBtn.setText(LocalConfiguration.titleBeanList.get(i).getChildren().get(1).getName());
                    firstCategoryId = LocalConfiguration.titleBeanList.get(i).getChildren().get(0).getCategoryId();
                    secondCategoryId = LocalConfiguration.titleBeanList.get(i).getChildren().get(1).getCategoryId();
                }
                break;
            }
        }
        mPresenter.getList(MyApplication.spUtils.getString("token", ""),firstPageNo,firstPageSize,firstCategoryId,"","");
        initListener();
        setAdapter();
        setAdapter2();
    }

    private void initListener() {
        viewBinding.newsBtn.setOnClickListener(v -> {
            viewBinding.newsBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.btn_left_select,null));
            viewBinding.infoBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.btn_right_normal,null));
            viewBinding.newsBtn.setTextColor(Color.parseColor("#FFFFFF"));
            viewBinding.infoBtn.setTextColor(Color.parseColor("#B0986F"));
            isFirst = true;
            viewBinding.listOne.setVisibility(View.VISIBLE);
            viewBinding.listTwo.setVisibility(View.GONE);
            mPresenter.getList(MyApplication.spUtils.getString("token", ""),firstPageNo,firstPageSize,firstCategoryId,"","");
        });

        viewBinding.infoBtn.setOnClickListener(v -> {
            viewBinding.newsBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.btn_left_normal,null));
            viewBinding.infoBtn.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.btn_right_select,null));
            viewBinding.newsBtn.setTextColor(Color.parseColor("#B0986F"));
            viewBinding.infoBtn.setTextColor(Color.parseColor("#FFFFFF"));
            isFirst = false;
            viewBinding.listOne.setVisibility(View.GONE);
            viewBinding.listTwo.setVisibility(View.VISIBLE);
            mPresenter.getList(MyApplication.spUtils.getString("token", ""),secondPageNo,secondPageSize,secondCategoryId,"","");
        });
        viewBinding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            if (isFirst){
                firstPageNo = 1;
                mPresenter.getList(MyApplication.spUtils.getString("token", ""),firstPageNo,firstPageSize,firstCategoryId,"","");
            }else {
                secondPageNo = 1;
                mPresenter.getList(MyApplication.spUtils.getString("token", ""),secondPageNo,secondPageSize,secondCategoryId,"","");
            }
            refreshLayout.finishRefresh(100);
        });
        setScrollListener(viewBinding.listOne);
        setScrollListener(viewBinding.listTwo);
    }

    
    private void setScrollListener(ListView listView){
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(firstVisibleItem + visibleItemCount == totalItemCount){
                    View last_view = listView.getChildAt(listView.getChildCount() - 1);
                    if(last_view != null && last_view.getBottom() == listView.getHeight()){
                        if (isFirst){
                            firstPageNo ++;
                            mPresenter.getList(MyApplication.spUtils.getString("token", ""),firstPageNo,firstPageSize,firstCategoryId,"","");
                        }else {
                            secondPageNo++;
                            mPresenter.getList(MyApplication.spUtils.getString("token", ""), secondPageNo, secondPageSize, secondCategoryId, "", "");
                        }
                    }
                }
            }
        });
    }


    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean>(getActivity(),R.layout.news_item_layout,firstNewsData) {
            @Override
            protected void convert(ViewHolder holder, ArticleBean.ArticleInfoListBean.DataBean item, int position) {
                TextView title = holder.getConvertView().findViewById(R.id.title);
                ShapeableImageView imageView = holder.getConvertView().findViewById(R.id.image);
                if (item.getIcon().startsWith("http")){
                    Glide.with(getActivity()).load(item.getIcon()).into(imageView);
                }else {
                    Glide.with(getActivity()).load(HttpInterface.IMG_URL + item.getIcon()).into(imageView);
                }
                holder.setText(R.id.hotTxt,"置顶");
                if (item.getIsTop() != null && item.getIsTop().equals("1")) {
                    holder.getView(R.id.hotTxt).setVisibility(View.VISIBLE);
                    SpannableStringBuilder span = new SpannableStringBuilder("缩进啊" + item.getSubject());
                    span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 3,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    title.setText(span);
                }else {
                    holder.getView(R.id.hotTxt).setVisibility(View.GONE);
                    title.setText(item.getSubject());
                }
                holder.setText(R.id.time,item.getCreateDate());
                holder.setText(R.id.glance,item.getViewNum());
                holder.getView(R.id.newItemBtn).setOnClickListener(v -> {
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
        viewBinding.listOne.setAdapter(adapter);
    }


    private void setAdapter2() {
        if (adapter2 != null){
            adapter2.notifyDataSetChanged();
            return;
        }
        adapter2 = new CommonAdapter<ArticleBean.ArticleInfoListBean.DataBean>(getActivity(),R.layout.news_item_layout,secondNewsData) {
            @Override
            protected void convert(ViewHolder holder, ArticleBean.ArticleInfoListBean.DataBean item, int position) {
                TextView title = holder.getConvertView().findViewById(R.id.title);
                ShapeableImageView imageView = holder.getConvertView().findViewById(R.id.image);
                if (item.getIcon().startsWith("http")){
                    Glide.with(getActivity()).load(item.getIcon()).into(imageView);
                }else {
                    Glide.with(getActivity()).load(HttpInterface.IMG_URL + item.getIcon()).into(imageView);
                }
                holder.setText(R.id.hotTxt,"置顶");
                if (item.getIsTop() != null && item.getIsTop().equals("1")) {
                    holder.getView(R.id.hotTxt).setVisibility(View.VISIBLE);
                    SpannableStringBuilder span = new SpannableStringBuilder("缩进啊" + item.getSubject());
                    span.setSpan(new ForegroundColorSpan(Color.TRANSPARENT), 0, 3,
                            Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    title.setText(span);
                }else {
                    holder.getView(R.id.hotTxt).setVisibility(View.GONE);
                    title.setText(item.getSubject());
                }
                holder.setText(R.id.time,item.getCreateDate());
                holder.setText(R.id.glance,item.getViewNum());
                holder.getView(R.id.newItemBtn).setOnClickListener(v -> {
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
        viewBinding.listTwo.setAdapter(adapter2);
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
        if (status.equals("success")){
            articleBean = JSON.parseObject(jsonObject.optString("data"),ArticleBean.class);
            if (isFirst){
                if (firstPageNo == 1){
                    firstNewsData.clear();
                }
                firstNewsData.addAll(articleBean.getArticleInfoList().getData());
                setAdapter();
            }else {
                if (secondPageNo == 1){
                    secondNewsData.clear();
                }
                secondNewsData.addAll(articleBean.getArticleInfoList().getData());
                setAdapter2();
            }
        }
    }
}