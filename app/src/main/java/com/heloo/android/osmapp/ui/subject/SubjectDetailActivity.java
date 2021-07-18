package com.heloo.android.osmapp.ui.subject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivitySubjectDetailBinding;
import com.heloo.android.osmapp.model.SubjectDetailBean;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import rx.Subscriber;

public class SubjectDetailActivity extends BaseActivity {

    ActivitySubjectDetailBinding binding;
    private ImageView image;
    private TextView des;
    private TextView nameDetail;
    private SubjectDetailBean subjectDetailBean;
    private CommonAdapter<SubjectDetailBean.ArticlelistBean> adapter;
    private List<SubjectDetailBean.ArticlelistBean> data = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectDetailBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        setHeader();
        goBack();
        setTitle("专题");
        initView();
        showProgress("");
        getData();
    }

    private void initView() {
        View view = LayoutInflater.from(this).inflate(R.layout.subject_header_layout, null);
        image = view.findViewById(R.id.image);
        des = view.findViewById(R.id.des);
        nameDetail = view.findViewById(R.id.nameDetail);
        ViewGroup.LayoutParams params = image.getLayoutParams();
        params.height = (int)(ScreenUtils.getScreenWidth()*0.37);
        image.setLayoutParams(params);
        binding.list.addHeaderView(view);

    }

    private void getData() {
        HttpInterfaceIml.getSubjectDetail(MyApplication.spUtils.getString("token", ""),getIntent().getStringExtra("id")).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgress();
            }

            @Override
            public void onError(Throwable e) {
                stopProgress();
            }

            @Override
            public void onNext(ResponseBody body) {
                stopProgress();
                try {
                    String s = new String(body.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String status = jsonObject.optString("status");
                    if (status.equals("success")){
                        subjectDetailBean = JSON.parseObject(jsonObject.optString("data"),SubjectDetailBean.class);
                        if (subjectDetailBean == null){
                            return;
                        }
                        nameDetail.setText(subjectDetailBean.getSpecial().getSubject());
                        if (subjectDetailBean.getSpecial().getIcon().startsWith("http")) {
                            Glide.with(SubjectDetailActivity.this).load(subjectDetailBean.getSpecial().getIcon()).into(image);
                        }else {
                            Glide.with(SubjectDetailActivity.this).load(HttpInterface.IMG_URL+subjectDetailBean.getSpecial().getIcon()).into(image);
                        }
                        des.setText(subjectDetailBean.getSpecial().getDescription());
                        data.clear();
                        data.addAll(subjectDetailBean.getArticlelist());
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


    private void setAdapter(){
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<SubjectDetailBean.ArticlelistBean>(this,R.layout.subject_detail_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, SubjectDetailBean.ArticlelistBean item, int position) {
                ImageView image = holder.getConvertView().findViewById(R.id.image);
                ViewGroup.LayoutParams params = image.getLayoutParams();
                params.width = (int)(ScreenUtils.getScreenWidth()*0.37);
                params.height = (int)(ScreenUtils.getScreenWidth()*0.15);
                image.setLayoutParams(params);
                if (item.getIcon().startsWith("http")) {
                    Glide.with(SubjectDetailActivity.this).load(item.getIcon()).into(image);
                }else {
                    Glide.with(SubjectDetailActivity.this).load(HttpInterface.IMG_URL+item.getIcon()).into(image);
                }
                holder.setText(R.id.time,item.getCreate_date());
                holder.setText(R.id.num,item.getRead_num());
                holder.setText(R.id.title,item.getSubject());

                holder.getView(R.id.button).setOnClickListener(v -> {
                    Intent intent = new Intent(SubjectDetailActivity.this, WebViewActivity.class);
                    if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticle_id()
                                + "&uid=" + LocalConfiguration.userInfo.getUid()+ "&username=" + LocalConfiguration.userInfo.getUsername()+"&app=1");
                    }else {
                        intent.putExtra("url", HttpInterface.URL + LocalConfiguration.newsDetailUrl + "?articleId=" + item.getArticle_id()+"&app=1");
                    }
                    startActivity(intent);
                });
            }
        };
        binding.list.setAdapter(adapter);
    }

}