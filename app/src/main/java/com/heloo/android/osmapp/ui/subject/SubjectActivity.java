package com.heloo.android.osmapp.ui.subject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivitySubjectBinding;
import com.heloo.android.osmapp.model.SubjectBean;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
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
 * Description : SubjectActivity
 * 专题列表
 *
 * @author WITNESS
 * @date 7/4/21
 */
public class SubjectActivity extends BaseActivity {

    ActivitySubjectBinding binding;
    CommonAdapter<SubjectBean> adapter;
    private List<SubjectBean> data = new ArrayList<>();
    private int pageNo = 1;
    private int pageSize = 20;
    private SubjectBean subjectBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySubjectBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        setTitle("专题");
        setHeader();
        goBack();
        setAdapter();
        showProgress("");
        getData();
        binding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                pageNo++;
                getData();
                refreshLayout.finishLoadMore(1000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                getData();
                refreshLayout.finishRefresh(1000);
            }
        });
    }

    private void getData() {
        HttpInterfaceIml.getSubject(MyApplication.spUtils.getString("token", ""), pageNo, pageSize).subscribe(new Subscriber<ResponseBody>() {
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
                    if (status.equals("success")) {
                        if (pageNo == 1) {
                            data.clear();
                        }
                        data.addAll(JSON.parseArray(jsonObject.optString("data"), SubjectBean.class));
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

    private void setAdapter() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<SubjectBean>(this, R.layout.subject_item_layout, data) {
            @Override
            protected void convert(ViewHolder holder, SubjectBean item, int position) {
                ImageView subjectImg = holder.getConvertView().findViewById(R.id.subjectImg);
                if (item.getIcon().startsWith("http")) {
                    Glide.with(SubjectActivity.this)
                            .load(item.getIcon())
                            .into(subjectImg);
                } else {
                    Glide.with(SubjectActivity.this)
                            .load(HttpInterface.IMG_URL + item.getIcon())
                            .into(subjectImg);
                }
                holder.setText(R.id.title, item.getSubject());
                holder.setText(R.id.date, item.getCreateDate());
                holder.getView(R.id.button).setOnClickListener(v -> {
                    Intent intent = new Intent(SubjectActivity.this, SubjectDetailActivity.class);
                    intent.putExtra("id", item.getId());
                    startActivity(intent);
                });
            }
        };
        binding.list.setAdapter(adapter);
    }
}