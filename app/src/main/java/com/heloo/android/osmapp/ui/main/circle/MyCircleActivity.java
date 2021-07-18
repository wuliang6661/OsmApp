package com.heloo.android.osmapp.ui.main.circle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.adapter.NineImageAdapter;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.TopicDetailLayoutBinding;
import com.heloo.android.osmapp.model.CircleBean;
import com.heloo.android.osmapp.utils.GlideSimpleTarget;
import com.heloo.android.osmapp.utils.MessageEvent;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.widget.NineGridView;
import com.zhy.adapter.recyclerview.CommonAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import ch.ielse.view.imagewatcher.ImageWatcher;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 2020-03-18
 * Describe: 我的话题
 */
public class MyCircleActivity extends BaseActivity implements ImageWatcher.OnPictureLongPressListener, ImageWatcher.Loader {

    private TopicDetailLayoutBinding binding;

    private List<CircleBean> data = new ArrayList<>();
    private com.zhy.adapter.recyclerview.CommonAdapter<CircleBean> adapter;

    private int pageNo = 1;
    private int pagesize = 20;
    private String isNewTab = "myPost";//最新newest，最热hot，我的myPost
    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = TopicDetailLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        token = MyApplication.spUtils.getString("token", "");
        ButterKnife.inject(this);
        binding.title.setText("我的帖子");
        binding.rlTopic.setVisibility(View.GONE);
        binding.imageWatcher.setTranslucentStatus(ScreenUtils.calcStatusBarHeight(MyCircleActivity.this));
        binding.imageWatcher.setErrorImageRes(R.mipmap.error_picture);
        binding.imageWatcher.setOnPictureLongPressListener(this);
        binding.imageWatcher.setLoader(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.topicList.setLayoutManager(layoutManager);
        getCircleList(token,pageNo,pagesize,null,isNewTab);
        EventBus.getDefault().register(this);
        binding.rlBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    private void setAdapter() {
        adapter = new CommonAdapter<CircleBean>(this, R.layout.circle_item_layout, data) {
            @Override
            protected void convert(final com.zhy.adapter.recyclerview.base.ViewHolder holder, final CircleBean s, int position) {
                final NineGridView picGridView =holder.getConvertView().findViewById(R.id.picGridView);
                final ShapeableImageView headerImage = holder.getConvertView().findViewById(R.id.headerImage);
                Glide.with(MyCircleActivity.this).load(s.getHeader()).placeholder(R.mipmap.header).error(R.mipmap.header).into(headerImage);
                TextView name = holder.getConvertView().findViewById(R.id.name);
                TextView content = holder.getConvertView().findViewById(R.id.content);
//                SpannableString spannableString = new SpannableString(String.format("#%s#%s",s.getTopicName(),s.getDescr()));
//                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, s.getTopicName().length()+2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (s.getUserName() != null
                        && !s.getUserName().equals("")) {
                    name.setText(s.getUserName());
                }else {
                    name.setText(String.format("%s%s","欧诗漫会员",s.getUid()));
                }
                holder.setText(R.id.commentNum,s.getCommentNum());
                holder.setText(R.id.time, s.getCreateTime());
                if (s.getTopicName() != null && !s.getTopicName().equals("")) {
                    SpannableString spannableString = new SpannableString(String.format("#%s#%s", s.getTopicName(), s.getDescr()));
                    spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, s.getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    content.setText(spannableString);
                    content.setMaxLines(5);
                }else {
                    content.setText(s.getDescr());
                }
                holder.setText(R.id.commentNum,s.getCommentNum());
                holder.setText(R.id.likeNum,s.getPointNum());
                if (s.getIsPraise().equals("0")){//点赞过
                    Glide.with(MyCircleActivity.this).load(R.mipmap.like_yes).into((ImageView)holder.getView(R.id.likeImage));
                }else {
                    Glide.with(MyCircleActivity.this).load(R.mipmap.like_no).into((ImageView)holder.getView(R.id.likeImage));
                }
                if (s.getIsComment().equals("0")){//评论过
                    Glide.with(MyCircleActivity.this).load(R.mipmap.comment_yes).into((ImageView)holder.getView(R.id.commentImage));
                }else {
                    Glide.with(MyCircleActivity.this).load(R.mipmap.comment_no).into((ImageView)holder.getView(R.id.commentImage));
                }

                if (s.getPicList() != null && s.getPicList().size()>0){
                    picGridView.setVisibility(View.VISIBLE);
                    if (s.getPicList().size() == 1){
                        picGridView.setSingleImageSize((int)(ScreenUtils.getScreenWidth() * 0.6),(int)(ScreenUtils.getScreenWidth() * 0.44));
                    }
                    picGridView.setAdapter(new NineImageAdapter(MyCircleActivity.this,s.getPicList()));
                    picGridView.setOnImageClickListener(new NineGridView.OnImageClickListener() {
                        @Override
                        public void onImageClick(int position, View view) {
                            binding.imageWatcher.show((ImageView) view, picGridView.getImageViews(), s.getPicList());
                        }
                    });

                }else {
                    picGridView.setVisibility(View.GONE);
                }


                holder.getView(R.id.itemLayout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyCircleActivity.this, CircleDetailActivity.class);
                        intent.putExtra("data",s);
                        startActivity(intent);
                    }
                });

                holder.getView(R.id.likeImage).setOnClickListener(v -> {
                    like(token,s.getTopicId());
                    Glide.with(MyCircleActivity.this).load(R.mipmap.like_yes).into((ImageView)holder.getView(R.id.likeImage));
                });

            }
        };
        binding.topicList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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
     * @param pageNo
     * @param isNewTab
     */
    private void getCircleList(String token,final int pageNo, int pagesize, String topicId, String isNewTab){
        HttpInterfaceIml.getCircle(token,pageNo,pagesize,topicId,isNewTab).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                binding.refreshRoot.finishRefresh();
                binding.refreshRoot.finishLoadMore();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MyCircleActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    if (pageNo == 1){
                        data.clear();
                    }
                    data.addAll(JSON.parseArray(jsonObject.optString("data"),CircleBean.class));
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
     * 点赞
     */
    private void like(String token,String id){
        HttpInterfaceIml.like(token,id).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(MyCircleActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
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


    /**
     * 更新列表数据
     * @param messageEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent){
        if (messageEvent.getMessageType().equals("point")){
            String mdata = messageEvent.getPassValue().toString();
            String id = mdata.substring(0,mdata.indexOf(":"));
            String isPoint = mdata.substring(mdata.indexOf(":")+1,mdata.indexOf(","));
            String pointNum = mdata.substring(mdata.indexOf(",")+1);
            if (data != null && data.size()>0) {
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getId().equals(id)) {
                        data.get(i).setIsPraise(isPoint);
                        data.get(i).setPointNum(pointNum);
                        if (adapter != null) {
                            adapter.notifyItemChanged(i);
                        }
                        break;
                    }
                }
            }
        }else if (messageEvent.getMessageType().equals("comment")){
            String mdata = messageEvent.getPassValue().toString();
            String id = mdata.substring(0,mdata.indexOf(":"));
            String isComment = mdata.substring(mdata.indexOf(":")+1,mdata.indexOf(","));
            String commentNum = mdata.substring(mdata.indexOf(",")+1);
            if (data != null && data.size()>0) {
                for (int i = 0; i < data.size(); i++) {
                    if (data.get(i).getId().equals(id)) {
                        data.get(i).setIsComment(isComment);
                        data.get(i).setCommentNum(commentNum);
                        if (adapter != null) {
                            adapter.notifyItemChanged(i);
                        }
                        break;
                    }
                }
            }
        }
    }


}
