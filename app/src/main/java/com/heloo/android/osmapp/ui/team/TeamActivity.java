package com.heloo.android.osmapp.ui.team;

import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivityTeamBinding;
import com.heloo.android.osmapp.model.TeamBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.TeamContract;
import com.heloo.android.osmapp.mvp.presenter.TeamPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;

/**
 * 我的团队
 */
public class TeamActivity extends MVPBaseActivity<TeamContract.View, TeamPresenter, ActivityTeamBinding>
    implements TeamContract.View, View.OnClickListener {

    private CommonAdapter<TeamBean.UserlistBean> adapter;
    private List<TeamBean.UserlistBean> data = new ArrayList<>();
    private CommonAdapter<TeamBean.DeptlistBean> adapterDep;
    private List<TeamBean.DeptlistBean> dataDep = new ArrayList<>();

    private String type = "";//1是阅读量排序  2是转发量排序
    private TeamBean teamBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        showProgress("");
        mPresenter.getData(MyApplication.spUtils.getString("token", ""),"",type);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        goBack();
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("默认"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("转发"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("浏览"));
        viewBinding.tabLayout.setTabMode(TabLayout.MODE_FIXED);
        viewBinding.tabLayout.setTabIndicatorFullWidth(false);
        //设置第一个
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout2,null);
        TextView name = view.findViewById(R.id.name);
        name.setText(viewBinding.tabLayout.getTabAt(0).getText());
        viewBinding.tabLayout.getTabAt(0).setCustomView(view);
        viewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout2,null);
                TextView name = view.findViewById(R.id.name);
                name.setText(tab.getText());
                tab.setCustomView(view);
                if (tab.getPosition() == 0){
                    type = "";
                }else if (tab.getPosition() == 1){
                    type = "2";
                }else {
                    type = "1";
                }
                showProgress("");
                mPresenter.getData(MyApplication.spUtils.getString("token", ""),"",type);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewBinding.list.setLayoutManager(new LinearLayoutManager(this));
        viewBinding.listDep.setLayoutManager(new LinearLayoutManager(this));
        setAdpter();
    }

    /**
     * 员工
     */
    private void setAdpter() {
        if (adapter != null){
            adapter.notifyDataSetChanged();
            return;
        }
        adapter = new CommonAdapter<TeamBean.UserlistBean>(this,R.layout.team_item_layout,data) {
            @Override
            protected void convert(ViewHolder holder, TeamBean.UserlistBean item, int position) {
                ShapeableImageView headerImg = holder.getConvertView().findViewById(R.id.headerImg);
                Glide.with(TeamActivity.this).load(item.getIcon()).error(R.drawable.default_head).into(headerImg);
                holder.setText(R.id.teamName,item.getRealName());
                holder.setText(R.id.teamInfo,String.format("转发%s次/浏览%s次",item.getForwardNumber(),item.getReadNumber()));
                holder.getView(R.id.personNum).setVisibility(View.GONE);
                holder.getView(R.id.teamStatus).setVisibility(View.GONE);
                holder.getView(R.id.btnDetail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TeamActivity.this,TeamDetailActivity.class);
                        intent.putExtra("id",item.getUsername());
                        startActivity(intent);
                    }
                });
            }
        };
        viewBinding.list.setAdapter(adapter);
    }

    /**
     * 部门
     */
    private void setDepAdapter() {
        if (adapterDep != null){
            adapterDep.notifyDataSetChanged();
            return;
        }
        adapterDep = new CommonAdapter<TeamBean.DeptlistBean>(this,R.layout.team_item_layout,dataDep) {
            @Override
            protected void convert(ViewHolder holder, TeamBean.DeptlistBean item, int position) {
                ShapeableImageView headerImg = holder.getConvertView().findViewById(R.id.headerImg);
                Glide.with(TeamActivity.this).load(item.getIcon()).error(R.drawable.default_group).into(headerImg);
                holder.setText(R.id.teamName,item.getName());
                holder.setText(R.id.teamInfo,String.format("转发%s次/浏览%s次",item.getForwardnumber(),item.getReadnumber()));
                holder.getView(R.id.personNum).setVisibility(View.VISIBLE);
                holder.setText(R.id.personNum,String.format("%s人",item.getUserNumber()));
                holder.getView(R.id.teamStatus).setVisibility(View.GONE);
                holder.getView(R.id.btnDetail).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };
        viewBinding.listDep.setAdapter(adapterDep);
    }

    @Override
    public void onClick(View v) {

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
    public void getData(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            teamBean = JSON.parseObject(jsonObject.optString("data"),TeamBean.class);
            data.clear();
            dataDep.clear();
            data.addAll(teamBean.getUserlist());
            dataDep.addAll(teamBean.getDeptlist());
            setAdpter();
            setDepAdapter();
        }
    }
}