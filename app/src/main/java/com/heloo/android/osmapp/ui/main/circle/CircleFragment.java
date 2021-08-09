package com.heloo.android.osmapp.ui.main.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.adapter.NineImageAdapter;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseFragment;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.CircleLayoutBinding;
import com.heloo.android.osmapp.model.CircleBean;
import com.heloo.android.osmapp.model.HotTopicBean;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.GlideSimpleTarget;
import com.heloo.android.osmapp.utils.MessageEvent;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.Utils;
import com.heloo.android.osmapp.widget.LoadingProgressDialog;
import com.heloo.android.osmapp.widget.NineGridView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.stx.xhb.androidx.XBanner;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.ButterKnife;
import ch.ielse.view.imagewatcher.ImageWatcher;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 2020-03-09
 * Describe: 圈子
 */
public class CircleFragment extends BaseFragment implements ImageWatcher.OnPictureLongPressListener, ImageWatcher.Loader, View.OnClickListener {

    CircleLayoutBinding binding;
    private static LoadingProgressDialog proDialog = null;
    private List<CircleBean> data = new ArrayList<>();
    private CommonAdapter<CircleBean> adapter;
    List<String> photoUrls = new ArrayList<String>();
    private List<HotTopicBean.ListBean.DataBean> hotTopicBeanList = new ArrayList<>();//热门话题
    private int pageNo = 1;
    private int pagesize = 20;
    private String isNewTab = "hot";//最新newest，最热hot，我的myPost
    private String token;
    private List<HotTopicBean.BannerBean> bannerData = new ArrayList<>();
    private HotTopicBean hotTopicBean;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = CircleLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        token = MyApplication.spUtils.getString("token", "");
        initView();
        startProgressDialog("加载中...", getActivity());
        getCircleList(token, null, isNewTab);
        getTopicList();
    }

    @Override
    public void onResume() {
        super.onResume();
        token = MyApplication.spUtils.getString("token", "");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        binding.headLayout.post(() -> binding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(getActivity()), 0, 0));
        binding.sendCircle.setOnClickListener(this);
        binding.message.setOnClickListener(this);
        binding.circlePerson.setOnClickListener(this);
        binding.hotOne.setOnClickListener(this);
        binding.hotTwo.setOnClickListener(this);
        binding.hotThree.setOnClickListener(this);
        binding.hotFour.setOnClickListener(this);
        binding.hotFive.setOnClickListener(this);
        binding.hotMore.setOnClickListener(this);
        binding.tabs.addTab(binding.tabs.newTab().setText("热门"));
        binding.tabs.addTab(binding.tabs.newTab().setText("最新"));
        binding.tabs.getTabAt(0).select();
        binding.tabs.setTabMode(TabLayout.MODE_AUTO);
        binding.tabs.setTabIndicatorFullWidth(false);
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pageNo = 1;
                if (tab.getPosition() == 0) {
                    isNewTab = "hot";
                } else {
                    isNewTab = "newest";
                }
                startProgressDialog("加载中...", getActivity());
                getCircleList(token, null, isNewTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.circleList.setLayoutManager(layoutManager);
        binding.imageWatcher.setTranslucentStatus(Utils.calcStatusBarHeight(getActivity()));
        binding.imageWatcher.setErrorImageRes(R.mipmap.error_picture);
        binding.imageWatcher.setOnPictureLongPressListener(this);
        binding.imageWatcher.setLoader(this);
        binding.refreshRoot.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo++;
                getCircleList(token, null, isNewTab);
                binding.refreshRoot.finishLoadMore(2000);
                refreshLayout.finishLoadMore(2000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                getCircleList(token, null, isNewTab);
                getTopicList();
                binding.refreshRoot.finishRefresh(2000);
                refreshLayout.finishRefresh(2000);
            }
        });
        binding.refreshRoot.setRefreshFooter(new ClassicsFooter(getActivity()));
    }

    private void setAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<CircleBean>(getActivity(), R.layout.circle_item_layout, data) {
            @Override
            protected void convert(final ViewHolder holder, final CircleBean s, int position) {
                final NineGridView picGridView = holder.getConvertView().findViewById(R.id.picGridView);
                final ShapeableImageView headerImage = holder.getConvertView().findViewById(R.id.headerImage);
                TextView content = holder.getConvertView().findViewById(R.id.content);
                Glide.with(getActivity()).load(s.getHeader()).placeholder(R.mipmap.header).error(R.mipmap.header).into(headerImage);
                if (s.getUserName() != null && !s.getUserName().equals("") && s.getAnonymous().equals("0")) {
                    holder.setText(R.id.name, s.getUserName());
                } else {
                    holder.setText(R.id.name, String.format("%s%s", "欧诗漫会员", s.getUid()));
                }
                holder.setText(R.id.commentNum, s.getCommentNum());
                holder.setText(R.id.time, s.getCreateDate());
                if (s.getTopicName() != null && !s.getTopicName().equals("")) {
                    SpannableString spannableString = new SpannableString(String.format("#%s#%s", s.getTopicName(), s.getDescr()));
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, s.getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setText(spannableString);
                    content.setMaxLines(5);
                } else {
                    content.setText(s.getDescr());
                }
                holder.setText(R.id.commentNum, s.getCommentNum());
                holder.setText(R.id.likeNum, s.getPointNum());
                if (s.getIsPraise().equals("0")) {//点赞过
                    Glide.with(getActivity()).load(R.mipmap.like_yes).into((ImageView) holder.getView(R.id.likeImage));
                } else {
                    Glide.with(getActivity()).load(R.mipmap.like_no).into((ImageView) holder.getView(R.id.likeImage));
                }
                if (s.getIsComment().equals("0")) {//评论过
                    Glide.with(getActivity()).load(R.mipmap.comment_yes).into((ImageView) holder.getView(R.id.commentImage));
                } else {
                    Glide.with(getActivity()).load(R.mipmap.comment_no).into((ImageView) holder.getView(R.id.commentImage));
                }
                if (s.getPicList() != null && s.getPicList().size() > 0) {
                    picGridView.setVisibility(View.VISIBLE);
                    if (s.getPicList().size() == 1) {
                        picGridView.setSingleImageSize((int) (ScreenUtils.getScreenWidth() * 0.6), (int) (ScreenUtils.getScreenWidth() * 0.44));
                    }
                    picGridView.setOnImageClickListener(new NineGridView.OnImageClickListener() {
                        @Override
                        public void onImageClick(int position, View view) {
                            binding.imageWatcher.show((ImageView) view, picGridView.getImageViews(), s.getPicList());
                        }
                    });
                    picGridView.setAdapter(new NineImageAdapter(getActivity(), s.getPicList()));
                } else {
                    picGridView.setVisibility(View.GONE);
                }


                holder.getView(R.id.itemLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), CircleDetailActivity.class);
                        intent.putExtra("data", s);
                        startActivity(intent);
                    }
                });

                holder.getView(R.id.like).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!s.getIsPraise().equals("0")) {
                            if (goLogin()) {
                                like(token, s.getId());
                                holder.setText(R.id.likeNum, String.valueOf(Integer.valueOf(s.getPointNum()) + 1));
                                Glide.with(getActivity()).load(R.mipmap.like_yes).into((ImageView) holder.getView(R.id.likeImage));
                            }
                        }
                    }
                });
                holder.getView(R.id.comment).setOnClickListener(v -> {
                    if (goLogin()) {
                        Intent intent = new Intent(getActivity(), CircleDetailActivity.class);
                        intent.putExtra("data", s);
                        startActivity(intent);
                    }
                });

            }
        };
        binding.circleList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    /**
     * 轮播图
     *
     * @param banner
     */
    private void initBanner(XBanner banner) {
        //设置广告图片点击事件
        banner.setOnItemClickListener((banner12, model, view, position) -> {
            Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
            if (StringUtils.isEmpty(bannerData.get(position).getJumpId()) || bannerData.get(position).getJumpType() == 1) {
                return;
            }
            intent.putExtra("topicId", bannerData.get(position).getJumpId());
            intent.putExtra("topicName", bannerData.get(position).getSubject());
            for (int i = 0; i < hotTopicBeanList.size(); i++) {
                if (bannerData.get(position).getJumpId().equals(hotTopicBeanList.get(i).getId())) {
                    intent.putExtra("pic", hotTopicBeanList.get(i).getIcon());
                    intent.putExtra("num", hotTopicBeanList.get(i).getPostNum());
                    intent.putExtra("des", hotTopicBeanList.get(i).getIntroduce());
                    break;
                }
            }
            startActivity(intent);
        });
        //加载广告图片
        banner.loadImage((banner1, model, view, position) -> {
            ImageView image = view.findViewById(R.id.topicImg);
            TextView topicName = view.findViewById(R.id.topicName);
            topicName.setText(bannerData.get(position).getSubject());
            Glide.with(getActivity()).load(bannerData.get(position).getImgurl()).into(image);
        });

    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), TopicDetailActivity.class);
        switch (v.getId()) {
            case R.id.sendCircle:
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), SendCircleActivity.class));
                }
                break;
            case R.id.message:
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), MessageActivity.class));
                }
                break;
            case R.id.circlePerson:
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), MyCircleActivity.class));
                }
                break;
            case R.id.hotOne:
                intent.putExtra("topicId", hotTopicBeanList.get(0).getId());
                intent.putExtra("topicName", hotTopicBeanList.get(0).getName());
                intent.putExtra("num", hotTopicBeanList.get(0).getPostNum());
                intent.putExtra("pic", hotTopicBeanList.get(0).getIcon());
                intent.putExtra("num", hotTopicBeanList.get(0).getPostNum());
                intent.putExtra("des", hotTopicBeanList.get(0).getIntroduce());
                startActivity(intent);
                break;
            case R.id.hotTwo:
                if (hotTopicBeanList.size() < 2) return;
                intent.putExtra("topicId", hotTopicBeanList.get(2).getId());
                intent.putExtra("topicName", hotTopicBeanList.get(2).getName());
                intent.putExtra("num", hotTopicBeanList.get(2).getPostNum());
                intent.putExtra("pic", hotTopicBeanList.get(2).getIcon());
                intent.putExtra("num", hotTopicBeanList.get(2).getPostNum());
                intent.putExtra("des", hotTopicBeanList.get(2).getIntroduce());
                startActivity(intent);

                break;
            case R.id.hotThree:
                if (hotTopicBeanList.size() < 4) return;
                intent.putExtra("topicId", hotTopicBeanList.get(4).getId());
                intent.putExtra("topicName", hotTopicBeanList.get(4).getName());
                intent.putExtra("num", hotTopicBeanList.get(4).getPostNum());
                intent.putExtra("pic", hotTopicBeanList.get(4).getIcon());
                intent.putExtra("num", hotTopicBeanList.get(4).getPostNum());
                intent.putExtra("des", hotTopicBeanList.get(4).getIntroduce());
                startActivity(intent);
                break;
            case R.id.hotFour:
                if (hotTopicBeanList.size() < 1) return;
                intent.putExtra("topicId", hotTopicBeanList.get(1).getId());
                intent.putExtra("topicName", hotTopicBeanList.get(1).getName());
                intent.putExtra("num", hotTopicBeanList.get(1).getPostNum());
                intent.putExtra("pic", hotTopicBeanList.get(1).getIcon());
                intent.putExtra("num", hotTopicBeanList.get(1).getPostNum());
                intent.putExtra("des", hotTopicBeanList.get(1).getIntroduce());
                startActivity(intent);
                break;
            case R.id.hotFive:
                if (hotTopicBeanList.size() < 3) return;
                intent.putExtra("topicId", hotTopicBeanList.get(3).getId());
                intent.putExtra("topicName", hotTopicBeanList.get(3).getName());
                intent.putExtra("num", hotTopicBeanList.get(3).getPostNum());
                intent.putExtra("pic", hotTopicBeanList.get(3).getIcon());
                intent.putExtra("num", hotTopicBeanList.get(3).getPostNum());
                intent.putExtra("des", hotTopicBeanList.get(3).getIntroduce());
                startActivity(intent);
                break;
            case R.id.hotMore:
                startActivity(new Intent(getActivity(), HotTopicActivity.class));
                break;
        }
    }


    @Override
    public void load(Context context, String url, ImageWatcher.LoadCallback lc) {
        Glide.with(context).asBitmap().load(url).into(new GlideSimpleTarget(lc));
    }

    @Override
    public void onPictureLongPress(ImageView v, String url, int pos) {

    }


    /**
     * 获取圈子列表
     *
     * @param isNewTab
     */
    private void getCircleList(String token, String topicId, String isNewTab) {
        HttpInterfaceIml.getCircle(token, pageNo, pagesize, topicId, isNewTab).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                binding.refreshRoot.finishRefresh();
                binding.refreshRoot.finishLoadMore();
                stopProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                stopProgressDialog();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    if (pageNo == 1) {
                        data.clear();
                    }
                    data.addAll(JSON.parseArray(jsonObject.optString("data"), CircleBean.class));
                    setAdapter();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 获取热门话题
     */
    private void getTopicList() {
        HttpInterfaceIml.getTopicList(token, 1, 20).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                stopProgressDialog();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    hotTopicBean = JSON.parseObject(jsonObject.optString("data"), HotTopicBean.class);
                    hotTopicBeanList.clear();
                    hotTopicBeanList.addAll(hotTopicBean.getList().getData());

                    binding.hotOne.setText(hotTopicBeanList.get(0).getName());
                    if (hotTopicBeanList.size() > 1) {
                        binding.hotFour.setText(hotTopicBeanList.get(1).getName());
                    }
                    if (hotTopicBeanList.size() > 2) {
                        binding.hotTwo.setText(hotTopicBeanList.get(2).getName());
                    }
                    if (hotTopicBeanList.size() > 3) {
                        binding.hotFive.setText(hotTopicBeanList.get(3).getName());
                    }
                    if (hotTopicBeanList.size() > 4) {
                        binding.hotThree.setText(hotTopicBeanList.get(4).getName());
                    }
                    bannerData.clear();
                    bannerData.addAll(hotTopicBean.getBanner());
                    initBanner(binding.banner);
                    binding.banner.setAutoPlayAble(bannerData.size() > 1);
                    binding.banner.setPointsIsVisible(true);
                    binding.banner.setData(R.layout.circle_banner_layout, bannerData, null);//banner_image_layout

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 点赞
     */
    private void like(String token, String id) {
        HttpInterfaceIml.like(token, id).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    public static void startProgressDialog(String progressMsg, Context mcontext) {
        if (proDialog == null) {
            proDialog = LoadingProgressDialog.createDialog(mcontext);
            proDialog.setMessage(progressMsg);
            proDialog.setCanceledOnTouchOutside(false);
        }
        proDialog.show();
    }

    public static void stopProgressDialog() {
        if (proDialog != null) {
            proDialog.dismiss();
            proDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        proDialog = null;
        stopProgressDialog();
    }

    /**
     * 更新列表数据
     *
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        if (messageEvent.getMessageType().equals("point")) {
            String mdata = messageEvent.getPassValue().toString();
            String id = mdata.substring(0, mdata.indexOf(":"));
            String isPoint = mdata.substring(mdata.indexOf(":") + 1, mdata.indexOf(","));
            String pointNum = mdata.substring(mdata.indexOf(",") + 1);
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getId().equals(id)) {
                    data.get(i).setIsPraise(isPoint);
                    data.get(i).setPointNum(pointNum);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        } else if (messageEvent.getMessageType().equals("comment")) {
            String mdata = messageEvent.getPassValue().toString();
            String id = mdata.substring(0, mdata.indexOf(":"));
            String isComment = mdata.substring(mdata.indexOf(":") + 1, mdata.indexOf(","));
            String commentNum = mdata.substring(mdata.indexOf(",") + 1);
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getId().equals(id)) {
                    data.get(i).setIsComment(isComment);
                    data.get(i).setCommentNum(commentNum);
                    adapter.notifyItemChanged(i);
                    break;
                }
            }
        } else if (messageEvent.getMessageType().equals("showNew")) {
            binding.tabs.getTabAt(1).select();
            pageNo = 1;
            isNewTab = "1";
            startProgressDialog("加载中...", getActivity());
            getCircleList(token, null, isNewTab);
            binding.circleList.smoothScrollToPosition(0);
        }
    }
}
