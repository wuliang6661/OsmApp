package com.heloo.android.osmapp.ui.main.circle;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.LikeLayoutBinding;
import com.heloo.android.osmapp.model.CircleBean;
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
 * Describe: 点赞列表
 */
public class LikeListActivity extends BaseActivity {

    private List<CircleBean.PostPraiseVoListBean> data = new ArrayList<>();
    private CommonAdapter<CircleBean.PostPraiseVoListBean> adapter;
    private String token;
    private LikeLayoutBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LikeLayoutBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        token = MyApplication.spUtils.getString("token", "");
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.topicList.setLayoutManager(layoutManager);
        getPersonList(getIntent().getStringExtra("postId"));
        binding.rlBack.setOnClickListener(v -> finish());
    }

    private void setAdapter(){
        adapter = new CommonAdapter<CircleBean.PostPraiseVoListBean>(this,R.layout.like_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, CircleBean.PostPraiseVoListBean s, int position) {
                Glide.with(LikeListActivity.this).load(s.getHeader()).placeholder(R.mipmap.header).error(R.mipmap.header).into((ImageView)holder.getView(R.id.headerImage));
                if (s.getName() != null) {
                    holder.setText(R.id.name, s.getName());
                }else {
                    holder.setText(R.id.name, String.format("%s%s","欧诗漫会员",s.getUid()));
                }
                holder.setText(R.id.time,s.getCreateTime());//时间
            }
        };
        binding.topicList.setAdapter(adapter);
    }

    /**
     * 人员列表
     */
    private void getPersonList(String postId){
        HttpInterfaceIml.likePersonList(token,postId).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(LikeListActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(ResponseBody responseBody) {
                try {
                    String s = new String(responseBody.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String code = jsonObject.optString("status");
                    String postPraiseVoList = jsonObject.optString("data");
                    if (code.equals("success")){
                        data = JSON.parseArray(postPraiseVoList,CircleBean.PostPraiseVoListBean.class);
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


}
