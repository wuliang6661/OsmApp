package com.heloo.android.osmapp.ui.points;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.JSON;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityPointsBinding;
import com.heloo.android.osmapp.model.AddScoreBean;
import com.heloo.android.osmapp.model.ScoreListBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.PointsContract;
import com.heloo.android.osmapp.mvp.presenter.PointsPresenter;
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
/**
 * Description : 我的积分
 *
 * @author WITNESS
 * @date 4/26/21
 */
public class PointsActivity extends MVPBaseActivity<PointsContract.View, PointsPresenter, ActivityPointsBinding>
    implements PointsContract.View{

    private CommonAdapter<ScoreListBean> adapter;
    private List<ScoreListBean> data = new ArrayList<>();
    private CommonAdapter<AddScoreBean> addAdapter;
    private List<AddScoreBean> addData = new ArrayList<>();
    TextView myPoints;
    TabLayout tabLayout;
    private int pageNo = 1;
    private int pageSize = 20;
    private int pageNoAdd = 1;
    private String tag = "1";//1 增加  2 消费
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showProgress("");
        initView();
        mPresenter.getAddData(MyApplication.spUtils.getString("token", ""),pageNoAdd,pageSize);
        viewBinding.list.setVisibility(View.GONE);
        viewBinding.listAdd.setVisibility(View.VISIBLE);
    }

    private void initView() {
        setHeader();
        setTitle("我的珍币");
        goBack();
        View headView = LayoutInflater.from(this).inflate(R.layout.points_head_layout,null);
        myPoints = headView.findViewById(R.id.myPoints);
        tabLayout = headView.findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("珍币获取"));
        tabLayout.addTab(tabLayout.newTab().setText("珍币消费"));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setTabIndicatorFullWidth(false);
        //设置第一个
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout2,null);
        TextView name = view.findViewById(R.id.name);
        name.setText(tabLayout.getTabAt(0).getText());
        tabLayout.getTabAt(0).setCustomView(view);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout2,null);
                TextView name = view.findViewById(R.id.name);
                name.setText(tab.getText());
                tab.setCustomView(view);
                if (tab.getPosition() == 0){
                    showProgress("");
                    mPresenter.getAddData(MyApplication.spUtils.getString("token", ""),pageNoAdd,pageSize);
                    viewBinding.list.setVisibility(View.GONE);
                    viewBinding.listAdd.setVisibility(View.VISIBLE);
                    tag = "1";
                }else {
                    showProgress("");
                    mPresenter.getDelData(MyApplication.spUtils.getString("token", ""),pageNo,pageSize);
                    viewBinding.list.setVisibility(View.VISIBLE);
                    viewBinding.listAdd.setVisibility(View.GONE);
                    tag = "2";
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        myPoints.setText(LocalConfiguration.userInfo.getIntegration());
        viewBinding.list.addHeaderView(headView);
        viewBinding.listAdd.addHeaderView(headView);
        viewBinding.refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (tag.equals("1")){
                    pageNoAdd++;
                    mPresenter.getAddData(MyApplication.spUtils.getString("token", ""), pageNoAdd, pageSize);
                }else {
                    pageNo++;
                    mPresenter.getDelData(MyApplication.spUtils.getString("token", ""), pageNo, pageSize);
                }
                refreshLayout.finishLoadMore(1000);
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (tag.equals("1")){
                    pageNoAdd = 1;
                    mPresenter.getAddData(MyApplication.spUtils.getString("token", ""), pageNoAdd, pageSize);
                }else {
                    pageNo = 1;
                    mPresenter.getDelData(MyApplication.spUtils.getString("token", ""), pageNo, pageSize);
                }
                refreshLayout.finishRefresh(1000);
            }
        });
        viewBinding.registerBtn.setOnClickListener(v -> startActivity(new Intent(PointsActivity.this,PointsDesActivity.class)));
    }

    /**
     * 积分消费
     */
    private void setAdapter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<ScoreListBean>(this,R.layout.points_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, ScoreListBean item, int position) {
                holder.setText(R.id.name,item.getSubject());
                holder.setText(R.id.time,item.getCreateDate());
                holder.setText(R.id.pointsNum,"-"+item.getConsumeScore());
                holder.getView(R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PointsActivity.this,PointsDetailActivity.class);
                        intent.putExtra("tag","2");
                        intent.putExtra("data",item);
                        startActivity(intent);
                    }
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }

    /**
     * 积分增加
     */
    private void setAddAdapter() {
        if (addAdapter != null){
            addAdapter.notifyDataSetChanged();
            return;
        }
        addAdapter = new CommonAdapter<AddScoreBean>(this,R.layout.points_item_layout,addData) {
            @Override
            protected void convert(ViewHolder holder, AddScoreBean item, int position) {
                holder.setText(R.id.name,item.getDescription());
                holder.setText(R.id.time,item.getCreateDate());
                holder.setText(R.id.pointsNum,"+"+item.getScore());
                holder.getView(R.id.button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PointsActivity.this,PointsDetailActivity.class);
                        intent.putExtra("tag","1");
                        intent.putExtra("data",item);
                        startActivity(intent);
                    }
                });
            }
        };
        viewBinding.listAdd.setAdapter(addAdapter);
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
    public void getDelData(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
            if (pageNo == 1){
                data.clear();
            }
            JSONObject jsonObject2 = new JSONObject(jsonObject1.optString("consuptionscorelist"));
            data.addAll(JSON.parseArray(jsonObject2.optString("data"),ScoreListBean.class));
            setAdapter();
        }
    }

    @Override
    public void getAddData(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
            if (pageNoAdd == 1){
                addData.clear();
            }
            JSONObject jsonObject2 = new JSONObject(jsonObject1.optString("getscorelist"));
            addData.addAll(JSON.parseArray(jsonObject2.optString("data"),AddScoreBean.class));
            setAddAdapter();
        }
    }
}