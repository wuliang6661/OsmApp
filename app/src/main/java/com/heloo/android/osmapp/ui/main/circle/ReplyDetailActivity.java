package com.heloo.android.osmapp.ui.main.circle;

import static android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE;
import static com.heloo.android.osmapp.ui.main.circle.CircleFragment.startProgressDialog;
import static com.heloo.android.osmapp.ui.main.circle.CircleFragment.stopProgressDialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ReplyLayoutBinding;
import com.heloo.android.osmapp.model.CommentDetailBean;
import com.heloo.android.osmapp.ui.login.LoginActivity;
import com.heloo.android.osmapp.utils.HttpImgUtils;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 2020-03-11
 * Describe:  回复详情
 */
public class ReplyDetailActivity extends BaseActivity {

    private List<CommentDetailBean.PostCommentModelListBean> data = new ArrayList<>();
    private CommonAdapter<CommentDetailBean.PostCommentModelListBean> adapter;
    private CommentDetailBean commentDetailBean;

    private ShapeableImageView headerImage;
    private TextView name;
    private TextView time;
    private TextView replyContent;
    private TextView delete;
    private TextView reply;
    private ShapeableImageView topicImg;
    private TextView topicText;
    private String token;
    private String uid;

    private ReplyLayoutBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ReplyLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        token = MyApplication.spUtils.getString("token", "");
        uid = LocalConfiguration.userInfo.getUid();
        setStatusBar();
        initView();
        startProgressDialog("", ReplyDetailActivity.this);
        getComment(getIntent().getStringExtra("commentId"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        token = MyApplication.spUtils.getString("token", "");
        uid = LocalConfiguration.userInfo.getUid();
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


    private void initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.reply_detail_layout, null);
        headerImage = view.findViewById(R.id.headerImage);
        name = view.findViewById(R.id.name);
        time = view.findViewById(R.id.time);
        replyContent = view.findViewById(R.id.replyContent);
        delete = view.findViewById(R.id.delete);
        reply = view.findViewById(R.id.reply);
        topicImg = view.findViewById(R.id.topicImg);
        topicText = view.findViewById(R.id.topicText);
        binding.topicList.addHeaderView(view);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                    deleteComment(commentDetailBean.getPostCommentModel().getId());
                } else {
                    startActivity(new Intent(ReplyDetailActivity.this, LoginActivity.class));
                }
            }
        });

        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name;
                if (commentDetailBean.getPostCommentModel().getName() != null
                        && !commentDetailBean.getPostCommentModel().getName().equals("")) {
                    name = commentDetailBean.getPostCommentModel().getName();
                } else {
                    name = "欧诗漫会员" + commentDetailBean.getPostCommentModel().getUid();
                }
                showInputLayout(name, "2",
                        commentDetailBean.getPostCommentModel().getId(),
                        commentDetailBean.getPostCommentModel().getUid(),
                        commentDetailBean.getPostCommentModel().getId(),
                        commentDetailBean.getPostCommentModel().getUid());
                showSoft();
            }
        });
        binding.rlBack.setOnClickListener(v -> finish());
    }

    private void setAdapter() {
        adapter = new CommonAdapter<CommentDetailBean.PostCommentModelListBean>(this, R.layout.reply_item_layout, data) {
            @Override
            protected void convert(ViewHolder viewHolder, final CommentDetailBean.PostCommentModelListBean item, int position) {
                Glide.with(ReplyDetailActivity.this).load(item.getHeader()).placeholder(R.mipmap.header).error(R.mipmap.header).into((ImageView) viewHolder.getView(R.id.headerImage));
                if (item.getUid().equals(uid)) {
                    viewHolder.getView(R.id.person).setVisibility(View.VISIBLE);
                    viewHolder.getView(R.id.deleteReply).setVisibility(View.VISIBLE);
                    viewHolder.setText(R.id.name, "我");
                } else {
                    viewHolder.getView(R.id.person).setVisibility(View.GONE);
                    viewHolder.getView(R.id.deleteReply).setVisibility(View.GONE);
                    if (item.getName() != null) {
                        viewHolder.setText(R.id.name, item.getName());
                    } else {
                        viewHolder.setText(R.id.name, String.format("%s%s", "欧诗漫会员", item.getUid()));
                    }
                }
                if (item.getReplyUid() != null && item.getReplyUid().equals(uid)) {
                    viewHolder.setText(R.id.replyName, String.format("@%s", "我"));
                } else {
                    if (item.getReplyName() != null) {
                        viewHolder.setText(R.id.replyName, String.format("@%s", item.getReplyName()));
                    } else {
                        viewHolder.setText(R.id.replyName, String.format("@%s%s", "欧诗漫会员", item.getReplyUid()));
                    }
                }
                viewHolder.setText(R.id.replyContent, String.format(":%s", item.getWord()));
                viewHolder.setText(R.id.time, item.getCreateTime());

                viewHolder.getView(R.id.deleteReply).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                            deleteComment(item.getId());
                        } else {
                            startActivity(new Intent(ReplyDetailActivity.this, LoginActivity.class));
                        }
                    }
                });
            }
        };
        binding.topicList.setAdapter(adapter);
    }

    /**
     * 评论详情
     */
    private void getComment(final String commentId) {
        HttpInterfaceIml.commentDetail(token, commentId).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgressDialog();
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ReplyDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")) {
                        commentDetailBean = JSON.parseObject(jsonObject.optString("data"), CommentDetailBean.class);
                        data = commentDetailBean.getPostCommentModelList();
                        setAdapter();
                        if (commentDetailBean.getPostInfoModel().getTopicName() != null) {
                            SpannableString spannableString = new SpannableString(String.format("#%s#%s", commentDetailBean.getPostInfoModel().getTopicName(), commentDetailBean.getPostInfoModel().getDescr()));
                            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#D5AC5A")), 0, commentDetailBean.getPostInfoModel().getTopicName().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            topicText.setText(spannableString);
                        } else {
                            topicText.setText(commentDetailBean.getPostInfoModel().getDescr());
                        }
                        if (commentDetailBean.getPostInfoModel().getPicList() != null && commentDetailBean.getPostInfoModel().getPicList().size() > 0) {
                            Glide.with(ReplyDetailActivity.this).load(HttpImgUtils.getImgUrl(commentDetailBean.getPostInfoModel().getPicList().get(0))).placeholder(R.mipmap.header).error(R.mipmap.header).into(topicImg);
                        }
                        time.setText(commentDetailBean.getPostInfoModel().getCreateTime());
                        //最新的评论回复
                        Glide.with(ReplyDetailActivity.this).load(HttpImgUtils.getImgUrl(commentDetailBean.getPostCommentModel().getHeader())).placeholder(R.mipmap.header).error(R.mipmap.header).into(headerImage);
                        if (commentDetailBean.getPostCommentModel().getName() != null
                                && !commentDetailBean.getPostCommentModel().getName().equals("")) {
                            name.setText(commentDetailBean.getPostCommentModel().getName());
                        } else {
                            name.setText(String.format("%s%s", "欧诗漫会员", commentDetailBean.getPostCommentModel().getUid()));
                        }
                        time.setText(commentDetailBean.getPostCommentModel().getCreateTime());
                        replyContent.setText(commentDetailBean.getPostCommentModel().getWord());
//                        if (commentDetailBean.getPostCommentModel().getUid().equals(uid)){//是自己发的显示删除,不是自己的显示回复
//                            delete.setVisibility(View.VISIBLE);
//                            reply.setVisibility(View.GONE);
//                        }else {
//                            delete.setVisibility(View.GONE);
//                            reply.setVisibility(View.VISIBLE);
//                        }
                        reply.setVisibility(View.VISIBLE);
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
    private void deleteComment(String id) {
        HttpInterfaceIml.deleteComment(token, id).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ReplyDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")) {
                        ToastUtils.showShortToast("删除成功");
                        getComment(getIntent().getStringExtra("commentId"));
                    } else {
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


    /**
     * 发布评论
     */
    PopupWindow pop;
    EditText myInputEdit;

    private void showInputLayout(String name, final String categoryId,
                                 final String pId, final String pUid, final String replyId, final String replyUid) {
        View layout = getLayoutInflater().inflate(R.layout.input_layout, null);
        myInputEdit = layout.findViewById(R.id.inputEdit);
        myInputEdit.setHint(String.format("回复 %s", name));
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

        myInputEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEND) {
                    if (TextUtils.isEmpty(myInputEdit.getText())) {
                        Toast.makeText(ReplyDetailActivity.this, "请输入消息", Toast.LENGTH_SHORT).show();
                        InputMethodManager inputMethodManager = (InputMethodManager) myInputEdit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(myInputEdit, 0);
                    } else {
                        addComment(token, commentDetailBean.getPostInfoModel().getId(),
                                myInputEdit.getText().toString(),
                                categoryId, pId, pUid, replyId, replyUid);
                        pop.dismiss();
                        hideSoft();
                    }

                }
                return false;
            }
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

    /**
     * 关闭键盘
     */
    private void hideSoft() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(
                InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }


    /**
     * 评论
     */
    private void addComment(String token, String topicId, String descr, String categoryId,
                            String pId, String pUid, String replyId, String replyUid) {
        HttpInterfaceIml.addComment(token, topicId, descr, categoryId, pId, pUid, replyId, replyUid).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(ReplyDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    if (code.equals("success")) {
                        ToastUtils.showShortToast("评论成功");
                        getComment(getIntent().getStringExtra("commentId"));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopProgressDialog();
    }

}
