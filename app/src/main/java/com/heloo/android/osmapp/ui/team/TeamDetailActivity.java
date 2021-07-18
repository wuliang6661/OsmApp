package com.heloo.android.osmapp.ui.team;


import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.adapter.BaseRecyclerHolder;
import com.heloo.android.osmapp.adapter.BaseRecyclerViewAdater;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivityTeamDetailBinding;
import com.heloo.android.osmapp.model.LinkBean;
import com.heloo.android.osmapp.model.TeamDetailBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.TeamDetailContract;
import com.heloo.android.osmapp.mvp.presenter.TeamDetailPresenter;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Description : 团队详情
 *
 * @author WITNESS
 * @date 4/25/21
 */
public class TeamDetailActivity extends MVPBaseActivity<TeamDetailContract.View, TeamDetailPresenter, ActivityTeamDetailBinding>
    implements TeamDetailContract.View, View.OnClickListener {

    private LinkBean linkBean;
    private RAdapter rAdapter;
    private boolean moveToTop = false;
    private int index;
    private int pageNo = 1;
    private int pageSize = 20;

    private TeamDetailBean teamDetailBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        mPresenter.getData(MyApplication.spUtils.getString("token", ""),pageNo,pageSize,getIntent().getStringExtra("id"));
        initData();
        initListener();
    }

    private void initView() {
        goBack();
        viewBinding.headerLayout.post(() -> viewBinding.headerLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.titleLayout.post(() -> viewBinding.titleLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
    }

    private void initData() {
        linkBean = new LinkBean();
        linkBean.itemLS = new ArrayList<>();
        linkBean.itemS = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            LinkBean.ItemL itemL = new LinkBean.ItemL();
            itemL.setTitle("欧诗漫"+i);
            linkBean.itemLS.add(itemL);

//            for (int j = 0; j < 16; j++) {
//                if (i % 2 == 0 && j % 2 == 0){
//                }else {
//                    LinkBean.Item item = new LinkBean.Item();
//                    item.setTitle("欧诗漫"+i);
//                    item.setName("名称"+j);
//                    item.setPrice("￥:"+(2+i+j)*3);
//                    linkBean.itemS.add(item);
//                }
//            }
        }
        viewBinding.tvHeader.setText(linkBean.itemLS.get(0).getTitle());
        viewBinding.rvRight.setLayoutManager(new LinearLayoutManager(this));
        rAdapter = new RAdapter(this,R.layout.team_detail_item_layout,linkBean.itemS);
        viewBinding.rvRight.setAdapter(rAdapter);
    }

    private void initListener() {
        viewBinding.rvRight.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) viewBinding.rvRight.getLayoutManager();
                if (moveToTop){ //向下滑动时，只会把改条目显示出来；我们还需要让该条目滑动到顶部；
                    moveToTop = false;
                    int m = index - layoutManager.findFirstVisibleItemPosition();
                    if (m >= 0 && m <= layoutManager.getChildCount()){
                        int top = layoutManager.getChildAt(m).getTop();
                        viewBinding.rvRight.smoothScrollBy(0,top);
                    }
                }else {
                    int index = layoutManager.findFirstVisibleItemPosition();
                    viewBinding.tvHeader.setText(rAdapter.getmData().get(index).getName());
                }
            }
        });

        viewBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {

                refreshLayout.finishRefresh(1000);
            }
        });

        viewBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if(verticalOffset >= 0){
                viewBinding.refreshLayout.setEnabled(true);
            }else{
                viewBinding.refreshLayout.setEnabled(false);
            }

            if (verticalOffset > - 500){
                viewBinding.titleLayout.setVisibility(View.INVISIBLE);
                viewBinding.backBtn.setVisibility(View.VISIBLE);
            }else {
                viewBinding.titleLayout.setVisibility(View.VISIBLE);
                viewBinding.backBtn.setVisibility(View.INVISIBLE);
            }

        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
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
            teamDetailBean = JSON.parseObject(jsonObject.optString("data"),TeamDetailBean.class);


        }
    }


    class RAdapter extends BaseRecyclerViewAdater<LinkBean.Item>{


        public RAdapter(Context context, int resLayout, List<LinkBean.Item> data) {
            super(context, resLayout, data);
        }

        @Override
        public void convert(BaseRecyclerHolder holder, final int position) {
            //悬停的标题头
            FrameLayout headLayout = holder.getView(R.id.stick_header);
            headLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.F5,null));
            TextView tvHead = holder.getView(R.id.tvHeader);
            if (position == 0){
                headLayout.setVisibility(View.VISIBLE);
                tvHead.setText(getmData().get(position).getName());
            }else {
                if (TextUtils.equals(getmData().get(position).getName(),getmData().get(position-1).getName())){
                    headLayout.setVisibility(View.GONE);
                }else {
                    headLayout.setVisibility(View.VISIBLE);
                    tvHead.setText(getmData().get(position).getName());
                }
            }
        }
    }
}