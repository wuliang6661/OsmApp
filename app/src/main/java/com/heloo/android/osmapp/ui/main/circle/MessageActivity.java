package com.heloo.android.osmapp.ui.main.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.NewMessageLayoutBinding;
import com.heloo.android.osmapp.model.CircleBean;
import com.heloo.android.osmapp.model.MyCommentBean;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.widget.LoadingProgressDialog;
import com.heloo.android.osmapp.widget.ViewPagerScroller;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.stx.xhb.androidx.transformers.DepthPageTransformer;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 2020-03-11
 * Describe: 点赞,提醒的消息
 */
public class MessageActivity extends BaseActivity implements View.OnClickListener {

    private NewMessageLayoutBinding binding;
    /**
     * 二个页面
     */
    private View likeView;//点赞
    private View commentView;//评论
    /**
     * viewPager相关
     */
    private List<View> viewListData = new ArrayList<>();//view数组
    private PagerAdapter pagerAdapter;

    /** 点赞 */
    private SmartRefreshLayout likeRefresh;
    private RecyclerView likeList;

    /** 评论 */
    private SmartRefreshLayout commentRefresh;
    private RecyclerView commentList;

    /** 点赞 */
    private List<CircleBean> data = new ArrayList<>();
    private CommonAdapter<CircleBean> adapter;

    /** 评论 */
    private List<MyCommentBean> commentData = new ArrayList<>();
    private CommonAdapter<MyCommentBean> commentAdapter;

    private static LoadingProgressDialog proDialog = null;
    private int pageNo = 1;
    private int pageSize = 20;

    private int commentPageNo = 1;
    private int commentPageSize = 20;
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewMessageLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        token = MyApplication.spUtils.getString("token", "");
        setStatusBar();
        initView();
        initListener();
        startProgressDialog("", MessageActivity.this);
        getLikeList(token, pageNo, pageSize);
    }

    /**
     * 浸入式状态栏实现同时取消5.0以上的阴影
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = getWindow().getDecorView();
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
        //修改字体颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rlBack:
                finish();
                break;
        }
    }


    private void initView() {
        binding.headLayout.post(() -> binding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        binding.rlBack.setOnClickListener(this);
        binding.tabs.addTab(binding.tabs.newTab().setText("点赞"));
        binding.tabs.addTab(binding.tabs.newTab().setText("评论"));
        binding.tabs.getTabAt(0).select();
        binding.tabs.setTabMode(TabLayout.MODE_FIXED);
        binding.tabs.setTabIndicatorFullWidth(false);
        binding.viewPager.setScanScroll(false);
        binding.viewPager.setPageTransformer(true, new DepthPageTransformer());
        LayoutInflater inflater = getLayoutInflater();
        likeView = inflater.inflate(R.layout.message_layout, null);
        commentView = inflater.inflate(R.layout.message_layout, null);

        viewListData.add(likeView);
        viewListData.add(commentView);
        setViewPagerAdapter();
        likeRefresh = likeView.findViewById(R.id.refresh_root);
        likeList = likeView.findViewById(R.id.topicList);
        commentRefresh = commentView.findViewById(R.id.refresh_root);
        commentList = commentView.findViewById(R.id.topicList);
        binding.viewPager.setCurrentItem(0);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        likeList.setLayoutManager(layoutManager);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(RecyclerView.VERTICAL);
        commentList.setLayoutManager(layoutManager2);

        likeRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {//点赞刷新
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo ++;
                getLikeList(token, pageNo, pageSize);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                getLikeList(token, pageNo, pageSize);
            }
        });
        commentRefresh.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                commentPageNo++;
                getCommentList(token, commentPageNo, pageSize);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                commentPageNo = 1;
                getCommentList(token, commentPageNo, pageSize);
            }
        });

    }


    private void setViewPagerAdapter() {
        pagerAdapter = new PagerAdapter() {

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                // TODO Auto-generated method stub
                return arg0 == arg1;
            }

            @Override
            public int getCount() {
                // TODO Auto-generated method stub
                return viewListData.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position,
                                    Object object) {
                // TODO Auto-generated method stub
                container.removeView(viewListData.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                // TODO Auto-generated method stub
                container.addView(viewListData.get(position));


                return viewListData.get(position);
            }
        };
        ViewPagerScroller scroller = new ViewPagerScroller(this);
        scroller.setScrollDuration(1000);//时间越长，速度越慢。
        scroller.initViewPagerScroll(binding.viewPager);
        binding.viewPager.setAdapter(pagerAdapter);
    }


    private void initListener() {
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                startProgressDialog("", MessageActivity.this);
                if (tab.getPosition() == 0) {
                    getLikeList(token, pageNo, pageSize);
                    binding.viewPager.setCurrentItem(0, true);
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    getCommentList(token, commentPageNo, pageSize);
                    binding.viewPager.setCurrentItem(1, true);
                    if (commentAdapter != null) {
                        commentAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 点赞
     */
    private void setLikeAdapter() {
        adapter = new CommonAdapter<CircleBean>(MessageActivity.this, R.layout.message_item_layout, data) {
            @Override
            protected void convert(ViewHolder holder, final CircleBean s, int position) {

                holder.getView(R.id.likeLayout).setVisibility(View.VISIBLE);
                holder.getView(R.id.commentLayout).setVisibility(View.GONE);

                TextView topicText = holder.getConvertView().findViewById(R.id.topicText);
                Glide.with(MessageActivity.this)
                        .load(s.getHeader()).placeholder(R.mipmap.header).error(R.mipmap.header).into((ImageView) holder.getView(R.id.headerImage));
                if (s.getUserName() != null && !s.getUserName().equals("")) {
                    holder.setText(R.id.name, s.getUserName());
                }else {
                    holder.setText(R.id.name, String.format("%s%s","欧诗漫会员",s.getUid()));
                }
                if (s.getPostPraiseVoList() != null && s.getPostPraiseVoList().size()>0 &&s.getPostPraiseVoList().get(0).getName() != null) {
                    holder.setText(R.id.num, String.format("%s等%s人赞了你的帖子", s.getPostPraiseVoList().get(0).getName(), s.getPointNum()));
                }else {
                    holder.setText(R.id.num, String.format("%s%s等%s人赞了你的帖子", "欧诗漫会员",s.getUid(), s.getPointNum()));
                }

                if (s.getPostPraiseVoList() != null && s.getPostPraiseVoList().size() > 0) {
                    holder.getView(R.id.headerImageOne).setVisibility(View.VISIBLE);
                    Glide.with(MessageActivity.this)
                            .load(s.getPostPraiseVoList().get(0).getHeader())
                            .placeholder(R.mipmap.header).error(R.mipmap.header)
                            .into((ImageView) holder.getView(R.id.headerImageOne));
                    if (s.getPostPraiseVoList().size() > 1) {
                        holder.getView(R.id.headerImageTwo).setVisibility(View.VISIBLE);
                        Glide.with(MessageActivity.this)
                                .load(s.getPostPraiseVoList().get(1).getHeader())
                                .placeholder(R.mipmap.header).error(R.mipmap.header).into((ImageView) holder.getView(R.id.headerImageTwo));
                        if (s.getPostPraiseVoList().size() > 2) {
                            holder.getView(R.id.headerImageThree).setVisibility(View.VISIBLE);
                            Glide.with(MessageActivity.this)
                                    .load(s.getPostPraiseVoList().get(2).getHeader())
                                    .placeholder(R.mipmap.header).error(R.mipmap.header).into((ImageView) holder.getView(R.id.headerImageThree));
                        } else {
                            holder.getView(R.id.headerImageThree).setVisibility(View.GONE);
                        }
                    } else {
                        holder.getView(R.id.headerImageTwo).setVisibility(View.GONE);
                        holder.getView(R.id.headerImageThree).setVisibility(View.GONE);
                    }
                } else {
                    holder.getView(R.id.headerImageOne).setVisibility(View.GONE);
                    holder.getView(R.id.headerImageTwo).setVisibility(View.GONE);
                    holder.getView(R.id.headerImageThree).setVisibility(View.GONE);
                }
//                SpannableString spannableString = new SpannableString(String.format("#%s#%s", s.getTopicName(), s.getDescr()));
//                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, s.getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                topicText.setText(spannableString);
                if (s.getTopicName() != null && !s.getTopicName().equals("")) {
                    SpannableString spannableString = new SpannableString(String.format("#%s#%s", s.getTopicName(), s.getDescr()));
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, s.getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    topicText.setText(spannableString);
                    topicText.setMaxLines(5);
                }else {
                    topicText.setText(s.getDescr());
                }
                if (s.getPicList() != null && s.getPicList().size() > 0) {
                    Glide.with(MessageActivity.this)
                            .load(s.getPicList().get(0)).into((ImageView) holder.getView(R.id.topicImg));
                }
                holder.setText(R.id.time, s.getCreateTime());
                holder.getView(R.id.likeLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MessageActivity.this, LikeListActivity.class);
                        intent.putExtra("postId", s.getId());
                        startActivity(intent);
                    }
                });

                holder.getView(R.id.itemLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(MessageActivity.this, ReplyDetailActivity.class);
//                        intent.putExtra("commentId",s.getId());
//                        startActivity(intent);
                    }
                });
            }
        };
        likeList.setAdapter(adapter);
    }

    /**
     *  评论列表
     */
    private void setCommentAdapter(){
        if (commentAdapter != null){
            commentAdapter.notifyDataSetChanged();
            return;
        }
        commentAdapter = new CommonAdapter<MyCommentBean>(MessageActivity.this, R.layout.message_item_layout, commentData) {
            @Override
            protected void convert(ViewHolder holder, final MyCommentBean myCommentBean, int position) {
                holder.getView(R.id.likeLayout).setVisibility(View.GONE);
                holder.getView(R.id.commentLayout).setVisibility(View.VISIBLE);
                TextView commentContent= holder.getConvertView().findViewById(R.id.commentContent);
                commentContent.setMaxLines(1);
                commentContent.setEllipsize(TextUtils.TruncateAt.END);
                TextView topicText = holder.getConvertView().findViewById(R.id.topicText);
                Glide.with(MessageActivity.this)
                        .load(myCommentBean.getHeader()).placeholder(R.mipmap.header).error(R.mipmap.header).into((ImageView) holder.getView(R.id.headerImage));
                if (myCommentBean.getName() != null
                        && !myCommentBean.getName().equals("")) {
                    holder.setText(R.id.name, myCommentBean.getName());
                }else {
                    holder.setText(R.id.name, String.format("%s%s","欧诗漫会员",myCommentBean.getUid()));
                }
                holder.setText(R.id.time,myCommentBean.getCreateTime());
                holder.setText(R.id.commentContent,myCommentBean.getWord());

//                SpannableString spannableString = new SpannableString(String.format("#%s#%s", myCommentBean.getPostVo().getTopicName(), myCommentBean.getPostVo().getDescr()));
//                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, myCommentBean.getPostVo().getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                topicText.setText(spannableString);

                if (myCommentBean.getPostInfoModel() != null) {
                    if (myCommentBean.getPostInfoModel().getTopicName() != null && !myCommentBean.getPostInfoModel().getTopicName().equals("")) {
                        SpannableString spannableString = new SpannableString(String.format("#%s#%s", myCommentBean.getPostInfoModel().getTopicName(), myCommentBean.getPostInfoModel().getDescr()));
                        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, myCommentBean.getPostInfoModel().getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        topicText.setText(spannableString);
                        topicText.setMaxLines(5);
                    } else {
                        topicText.setText(myCommentBean.getPostInfoModel().getDescr());
                    }
                }
                if (myCommentBean.getPostInfoModel() != null
                        && myCommentBean.getPostInfoModel().getPictures() != null) {
                    Glide.with(MessageActivity.this)
                            .asBitmap().load(myCommentBean.getPostInfoModel().getPictures().substring(0, myCommentBean.getPostInfoModel().getPictures().indexOf(","))).into((ImageView) holder.getView(R.id.topicImg));
                }else {
                    Glide.with(MessageActivity.this)
                            .asBitmap().load("").into((ImageView) holder.getView(R.id.topicImg));
                }

                holder.getView(R.id.itemLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MessageActivity.this, ReplyDetailActivity.class);
                        intent.putExtra("commentId",myCommentBean.getId());
                        startActivity(intent);
                    }
                });
            }
        };
        commentList.setAdapter(commentAdapter);
    }


    /**
     * 点赞列表
     *
     * @param token
     * @param pageNo
     * @param pagesize
     */
    private void getLikeList(String token, final int pageNo, int pagesize) {
        HttpInterfaceIml.likeList(token, pageNo, pagesize).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgressDialog();
                likeRefresh.finishRefresh();
                likeRefresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")) {
                        if (pageNo == 1) {
                            data.clear();
                        }
                        data.addAll(JSON.parseArray(jsonObject.optString("data"), CircleBean.class));
                        setLikeAdapter();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 评论列表
     *
     * @param token
     * @param pageNo
     * @param pagesize
     */
    private void getCommentList(String token, int pageNo, int pagesize) {
        HttpInterfaceIml.commentList(token, pageNo, pagesize).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgressDialog();
                commentRefresh.finishRefresh();
                commentRefresh.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MessageActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")) {
                        if (commentPageNo == 1) {
                            commentData.clear();
                        }
                        commentData.addAll(JSON.parseArray(jsonObject.optString("data"), MyCommentBean.class));
                        for (int i=0;i<commentData.size();i++){
                            if (commentData.get(i).getPostInfoModel() == null){
                                commentData.remove(i);
                                i--;
                            }
                        }
                        setCommentAdapter();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void startProgressDialog(String progressMsg, Context mcontext) {
        if (proDialog == null) {
            proDialog = LoadingProgressDialog.createDialog(mcontext);
            proDialog.setMessage(progressMsg);
            proDialog.setCanceledOnTouchOutside(false);
        }
        proDialog.show();
    }

    public void stopProgressDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
            proDialog = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgressDialog();
    }
}
