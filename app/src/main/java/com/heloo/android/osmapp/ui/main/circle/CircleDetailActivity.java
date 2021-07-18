package com.heloo.android.osmapp.ui.main.circle;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
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
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.DetailLayoutBinding;
import com.heloo.android.osmapp.model.CircleBean;
import com.heloo.android.osmapp.model.CommentBean;
import com.heloo.android.osmapp.utils.GlideSimpleTarget;
import com.heloo.android.osmapp.utils.MessageEvent;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.heloo.android.osmapp.utils.Utils;
import com.heloo.android.osmapp.widget.LoadingProgressDialog;
import com.heloo.android.osmapp.widget.NineGridView;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.ielse.view.imagewatcher.ImageWatcher;
import okhttp3.ResponseBody;
import rx.Subscriber;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;

/**
 * Created by Witness on 2020-03-11
 * Describe: 详情
 */
public class CircleDetailActivity extends BaseActivity implements ImageWatcher.OnPictureLongPressListener, ImageWatcher.Loader, View.OnClickListener {

    private DetailLayoutBinding binding;
    private static LoadingProgressDialog proDialog = null;
    private List<CommentBean> commentData = new ArrayList<>();
    private CommonAdapter<CommentBean> adapter;
    private CircleBean circleBean;
    private LinearLayout comment;
    private LinearLayout like;
    private TextView name,time,commentNum,likeNum,content;
    private ImageView likeImage,commentImage;
    private NineGridView picGridView;
    private ShapeableImageView headerImage;
    private String token;
    private String uid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //关键代码
        binding = DetailLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        token = MyApplication.spUtils.getString("token", "");
        circleBean = (CircleBean) getIntent().getSerializableExtra("data");
        if (LocalConfiguration.userInfo != null && LocalConfiguration.userInfo.getUid() != null) {
            uid = LocalConfiguration.userInfo.getUid();
        }else {
            uid = "";
        }
        setStatusBar();
        initView();
        startProgressDialog("加载中...", CircleDetailActivity.this);
        getCommentList(token,circleBean.getId());
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
            case R.id.comment:
                if (circleBean.getUserName() != null
                        && !circleBean.getUserName().equals("")) {
                    showInputLayout(circleBean.getUserName(), uid, "1", "", "", "", "");
                }else {
                    showInputLayout("欧诗漫会员"+circleBean.getUid(), uid, "1", "", "", "", "");
                }
                showSoft();
                break;
            case R.id.like:
                if (!circleBean.getIsPraise().equals("0")) {
                    like(token, circleBean.getId());
                }
                break;
        }
    }

    private void initView() {
        comment = findViewById(R.id.comment);
        like = findViewById(R.id.like);
        name = findViewById(R.id.name);
        time = findViewById(R.id.time);
        commentNum = findViewById(R.id.commentNum);
        likeNum = findViewById(R.id.likeNum);
        likeImage = findViewById(R.id.likeImage);
        commentImage = findViewById(R.id.commentImage);
        content = findViewById(R.id.content);
        picGridView = findViewById(R.id.picGridView);
        headerImage = findViewById(R.id.headerImage);
        binding.rlBack.setOnClickListener(this);
        comment.setOnClickListener(this);
        like.setOnClickListener(this);
        binding.imageWatcher.setTranslucentStatus(Utils.calcStatusBarHeight(this));
        binding.imageWatcher.setErrorImageRes(R.mipmap.error_picture);
        binding.imageWatcher.setOnPictureLongPressListener(this);
        binding.imageWatcher.setLoader(this);
        if (circleBean != null) {
            Glide.with(CircleDetailActivity.this).load(circleBean.getHeader()).placeholder(R.mipmap.header).error(R.mipmap.header).into(headerImage);
            name.setText(circleBean.getUserName());
            if (circleBean.getUserName() != null && !circleBean.getUserName().equals("") && circleBean.getAnonymous().equals("0"))  {
                name.setText(circleBean.getUserName());
            }else {
                name.setText(String.format("%s%s","欧诗漫会员",circleBean.getUid()));
            }
            time.setText(circleBean.getCreateDate());
            commentNum.setText(circleBean.getCommentNum());
            likeNum.setText(circleBean.getPointNum());
            if (circleBean.getIsPraise().equals("0")) {
                Glide.with(CircleDetailActivity.this).load(R.mipmap.like_yes).into(likeImage);
            } else {
                Glide.with(CircleDetailActivity.this).load(R.mipmap.like_no).into(likeImage);
            }
            if (circleBean.getIsComment().equals("0")){//评论过
                Glide.with(CircleDetailActivity.this).load(R.mipmap.comment_yes).into(commentImage);
            }else {
                Glide.with(CircleDetailActivity.this).load(R.mipmap.comment_no).into(commentImage);
            }
            if (circleBean.getTopicName() != null && !circleBean.getTopicName().equals("")) {
                SpannableString spannableString = new SpannableString(String.format("#%s#%s", circleBean.getTopicName(), circleBean.getDescr()));
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, circleBean.getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setText(spannableString);
                content.setMaxLines(5);
            }else {
                content.setText(circleBean.getDescr());
            }
            picGridView.setOnImageClickListener(new NineGridView.OnImageClickListener() {
                @Override
                public void onImageClick(int position, View view) {
                    binding.imageWatcher.show((ImageView) view, picGridView.getImageViews(), circleBean.getPicList());
                }
            });
            if (circleBean.getPicList() != null && circleBean.getPicList().size()>0){
                picGridView.setVisibility(View.VISIBLE);
                if (circleBean.getPicList().size() == 1){
                    picGridView.setSingleImageSize((int)(ScreenUtils.getScreenWidth() * 0.6),(int)(ScreenUtils.getScreenWidth() * 0.44));
                }
                picGridView.setOnImageClickListener(new NineGridView.OnImageClickListener() {
                    @Override
                    public void onImageClick(int position, View view) {
                        binding.imageWatcher.show((ImageView) view, picGridView.getImageViews(), circleBean.getPicList());
                    }
                });
                picGridView.setAdapter(new NineImageAdapter(this,circleBean.getPicList()));
            }else {
                picGridView.setVisibility(View.GONE);
            }

        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(CircleDetailActivity.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.commentList.setLayoutManager(layoutManager);
        binding.commentList.setFocusable(false);
    }

    @Override
    public void load(Context context, String url, ImageWatcher.LoadCallback lc) {
        Glide.with(context).asBitmap().load(url).into(new GlideSimpleTarget(lc));
    }

    @Override
    public void onPictureLongPress(ImageView v, String url, int pos) {

    }

    /**
     * 设置评论列表
     */
    private void setAdapter() {
        adapter = new CommonAdapter<CommentBean>(CircleDetailActivity.this, R.layout.comment_item_layout, commentData) {
            @Override
            protected void convert(final ViewHolder holder1, final CommentBean s1, int position) {
                RecyclerView detailCommentList = holder1.getConvertView().findViewById(R.id.detailCommentList);
                final LinearLayoutManager layoutManager1 = new LinearLayoutManager(
                        CircleDetailActivity.this);
                layoutManager1.setOrientation(RecyclerView.VERTICAL);
                detailCommentList.setLayoutManager(layoutManager1);
                TextView content = holder1.getConvertView().findViewById(R.id.content);
                String name;
                if (s1.getUid().equals(uid)){
                    name = "我";
                }else {
                    if (s1.getName() != null) {
                        name = s1.getName();
                    }else {
                        name = "欧诗漫会员"+ s1.getUid();
                    }
                }
                SpannableString spannableString = new SpannableString(String.format("%s:%s",name,s1.getWord()));
                spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                content.setText(spannableString);

                holder1.setText(R.id.date, s1.getCreateTime());
                if (s1.getUid().equals(uid)){//是自己评论的就可以删除
                    holder1.getView(R.id.delete).setVisibility(View.VISIBLE);
                }else {
                    holder1.getView(R.id.delete).setVisibility(View.GONE);
                }
                holder1.getView(R.id.floorPerson).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//回复层主评论
                        String name;
                        if (s1.getName() != null && !s1.getName().equals("")){
                            name = s1.getName();
                        }else {
                            name = "欧诗漫会员"+s1.getUid() ;
                        }

                        showInputLayout(name, uid, "2", s1.getId(), s1.getUid(), s1.getId(), s1.getUid());
                        showSoft();
                    }
                });
                holder1.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {//删除自己的评论
                        if (goLogin()) {
                            deleteComment(s1.getId());
                        }
                    }
                });
                if (s1.getPostCommentModelList() != null && s1.getPostCommentModelList().size() > 0) {
                    CommonAdapter<CommentBean> adapter1 = new CommonAdapter<CommentBean>(
                            CircleDetailActivity.this,
                            R.layout.item_detail_layout, s1.getPostCommentModelList()) {
                        @Override
                        protected void convert(ViewHolder holder3, final CommentBean s3, int position) {
                            TextView replyText = holder3.getConvertView().findViewById(R.id.replyText);
                            String name;
                            String replyName;
                            if (s3.getUid().equals(uid)){
                                name = "我";
                            }else {
                                if (s3.getName() != null) {
                                    name = s3.getName();
                                }else {
                                    name = String.format("%s%s","欧诗漫会员",s3.getUid());
                                }
                            }
                            if (s3.getReplyUid().equals(uid)){
                                replyName = "我";
                            }else {
                                if (s3.getReplyName() != null) {
                                    replyName = s3.getReplyName();
                                }else {
                                    replyName = String.format("%s%s","欧诗漫会员",s3.getUid());
                                }
                            }

                            SpannableString spannableString = new SpannableString(String.format("%s回复%s:%s",name,replyName,s3.getWord()));
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), name.length()+2, name.length()+2+replyName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            replyText.setText(spannableString);


                            if (s3.getUid().equals(uid)){//如果uid是自己,那么就显示删除
                                holder3.getView(R.id.delete).setVisibility(View.VISIBLE);
                            }else {
                                holder3.getView(R.id.delete).setVisibility(View.GONE);
                            }
                            holder3.getView(R.id.delete).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {//删除自己的评论
                                    if (goLogin()) {
                                        deleteComment(s3.getId());
                                    }
                                }
                            });
                            holder3.getView(R.id.commentItemClick).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {//回复二级或多级评论
                                    if (s3.getPId() != null) {
                                        String name;
                                        if (s3.getName() != null && !s3.getName().equals("")){
                                            name = s3.getName();
                                        }else {
                                            name = "欧诗漫会员"+s3.getUid() ;
                                        }
                                        showInputLayout(name, uid, "2", s3.getPId(), s3.getPUid(), s3.getReplyId(), s3.getReplyUid());
                                        showSoft();
                                    }
                                }
                            });
                        }
                    };
                    detailCommentList.setAdapter(adapter1);
                }
            }
        };
        binding.commentList.setAdapter(adapter);
    }

    /**
     * 获取评论列表
     */
    private void getCommentList(String token,String postId) {
        HttpInterfaceIml.getCommentList(token,postId).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(CircleDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
                    commentData = JSON.parseArray(jsonObject1.optString("postCommentModelList"), CommentBean.class);
                    if (!jsonObject1.optString("postInfoModel").equals("null")) {
                        JSONObject jsonObject2 = new JSONObject(jsonObject1.optString("postInfoModel"));
                        commentNum.setText(jsonObject2.optString("commentNum"));
                        likeNum.setText(jsonObject2.optString("pointNum"));
                        if (jsonObject2.optString("isPraise").equals("0")) {
                            Glide.with(CircleDetailActivity.this).load(R.mipmap.like_yes).into(likeImage);
                        } else {
                            Glide.with(CircleDetailActivity.this).load(R.mipmap.like_no).into(likeImage);
                        }
                        if (jsonObject2.optString("isComment").equals("0")) {//评论过
                            Glide.with(CircleDetailActivity.this).load(R.mipmap.comment_yes).into(commentImage);
                        } else {
                            Glide.with(CircleDetailActivity.this).load(R.mipmap.comment_no).into(commentImage);
                        }
                        EventBus.getDefault().post(new MessageEvent("point", jsonObject2.optString("id")+":"+jsonObject2.optString("isPraise")+","+jsonObject2.optString("pointNum")));
                        EventBus.getDefault().post(new MessageEvent("comment", jsonObject2.optString("id")+":"+jsonObject2.optString("isComment")+","+jsonObject2.optString("commentNum")));
                    }
                    if (commentData != null && commentData.size()>0) {
                        setAdapter();
                    }else {
                        commentData = new ArrayList<>();
                        commentData.clear();
                        setAdapter();
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
     * 发布评论
     */
    PopupWindow pop;
    EditText myInputEdit;

    private void showInputLayout(String name, final String uid, final String categoryId,
                                 final String pId, final String pUid, final String replyId, final String replyUid) {
        View layout = getLayoutInflater().inflate(R.layout.input_layout, null);
        myInputEdit = layout.findViewById(R.id.inputEdit);
        if (name.equals("")){
            myInputEdit.setHint("评论:");
        }else {
            myInputEdit.setHint(String.format("回复 %s", name));
        }
        myInputEdit.requestFocus();
        myInputEdit.setInputType(TYPE_TEXT_FLAG_MULTI_LINE);
        myInputEdit.setSingleLine(false);
        pop = new PopupWindow(layout, ScreenUtils.getScreenWidth(), RelativeLayout.LayoutParams.WRAP_CONTENT);

        // 设置出现和消失样式
        pop.setAnimationStyle(R.style.anim_menu_bottombar);

        pop.update();
        pop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        pop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        pop.setTouchable(true); // 设置popupwindow可点击
        pop.setOutsideTouchable(true); // 设置popupwindow外部可点击
        pop.setFocusable(true); // 获取焦点
        pop.showAtLocation(layout, Gravity.BOTTOM, 0, 0);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {

            }
        });

        myInputEdit.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEND) {
                if (TextUtils.isEmpty(myInputEdit.getText())) {
                    Toast.makeText(CircleDetailActivity.this, "请输入消息", Toast.LENGTH_SHORT).show();
                    InputMethodManager inputMethodManager = (InputMethodManager) myInputEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(myInputEdit, 0);
                } else {
                    addComment(token, circleBean.getId(),
                            myInputEdit.getText().toString(),
                            categoryId, pId, pUid, replyId, replyUid);
                    hideSoft();
                    pop.dismiss();
                }

            }
            return false;
        });

        pop.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                /**** 如果点击了popupwindow的外部，popupwindow也会消失 ****/
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    pop.dismiss();
                    return true;
                }
                return false;
            }

        });
    }

    private void showSoft() {
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {

            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) myInputEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(myInputEdit, 0);
            }
        }, 10);
    }

    /**
     * 关闭键盘
     */
    private void hideSoft(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(
                InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    /**
     * 评论
     */
    private void addComment(String token, String topicId, String word, String categoryId,
                            String pId, String pUid, String replyId, String replyUid) {
        HttpInterfaceIml.addComment(token, topicId, word, categoryId, pId, pUid, replyId, replyUid).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(CircleDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")) {
                        ToastUtils.showShortToast("评论成功");
                        getCommentList(token,circleBean.getId());
                    }

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

    /**
     * 点赞
     */
    private void like(String uid,String postId){
        HttpInterfaceIml.like(uid,postId).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(CircleDetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")){
                        getCommentList(token,circleBean.getId());
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
     * 删除评论
     */
    private void deleteComment(String id){
        HttpInterfaceIml.deleteComment(token,id).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(CircleDetailActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")){
                        ToastUtils.showShortToast("删除成功");
                        getCommentList(token,circleBean.getId());
                    }else {
                        ToastUtils.showShortToast("删除失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        proDialog = null;
        stopProgressDialog();
    }

}
