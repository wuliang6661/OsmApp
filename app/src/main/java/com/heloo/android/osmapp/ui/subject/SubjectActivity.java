package com.heloo.android.osmapp.ui.subject;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivitySubjectBinding;
import com.heloo.android.osmapp.model.SubjectBean;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGViewHolder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
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
    LGRecycleViewAdapter<SubjectBean> adapter;
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
        binding.list.setLayoutManager(new LinearLayoutManager(this));
        showProgress("");
        getData();
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
        adapter = new LGRecycleViewAdapter<SubjectBean>(data) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.subject_item_layout;
            }

            @Override
            public void convert(LGViewHolder holder, SubjectBean item, int position) {
                ImageView subjectImg = (ImageView) holder.getView(R.id.subjectImg);
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