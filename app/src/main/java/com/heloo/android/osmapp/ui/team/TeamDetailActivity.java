package com.heloo.android.osmapp.ui.team;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityTeamDetailBinding;
import com.heloo.android.osmapp.model.TeamDetailBean;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.TeamDetailContract;
import com.heloo.android.osmapp.mvp.presenter.TeamDetailPresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGRecycleViewAdapter;
import com.heloo.android.osmapp.widget.lgrecycleadapter.LGViewHolder;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Description : 团队详情
 *
 * @author WITNESS
 * @date 4/25/21
 */
public class TeamDetailActivity extends MVPBaseActivity<TeamDetailContract.View, TeamDetailPresenter, ActivityTeamDetailBinding>
        implements TeamDetailContract.View, View.OnClickListener {

    private boolean moveToTop = false;
    private int index;

    private TeamDetailBean teamDetailBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        mPresenter.getData(getIntent().getStringExtra("id"));
        initListener();
    }

    private void initView() {
        goBack();
        viewBinding.headerLayout.post(() -> viewBinding.headerLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.titleLayout.post(() -> viewBinding.titleLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        viewBinding.rvRight.setLayoutManager(new LinearLayoutManager(this));
    }


    private void initListener() {
        viewBinding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (verticalOffset > -500) {
                viewBinding.titleLayout.setVisibility(View.INVISIBLE);
                viewBinding.backBtn.setVisibility(View.VISIBLE);
            } else {
                viewBinding.titleLayout.setVisibility(View.VISIBLE);
                viewBinding.backBtn.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

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
    public void getData(TeamDetailBean body) {
        teamDetailBean = body;
        Glide.with(this).load(body.user.getIcon()).into(viewBinding.image);
        viewBinding.teamName.setText(body.user.getRealName());
        viewBinding.coinNum.setText(body.user.getIntegration());
        viewBinding.sendNum.setText(body.user.getForwardnumber());
        viewBinding.viewNum.setText(body.user.getClickNumber());
        setAdapter();
    }


    private void setAdapter() {
        LGRecycleViewAdapter<TeamDetailBean.Collect> adapter = new LGRecycleViewAdapter<TeamDetailBean.Collect>(teamDetailBean.collect) {
            @Override
            public int getLayoutId(int viewType) {
                return R.layout.team_detail_item_layout;
            }

            @Override
            public void convert(LGViewHolder holder, TeamDetailBean.Collect collect, int position) {
                holder.setText(R.id.tvHeader, collect.Day);
                RecyclerView recyclerView = (RecyclerView) holder.getView(R.id.recyclerview);
                recyclerView.setLayoutManager(new LinearLayoutManager(TeamDetailActivity.this));
                recyclerView.setNestedScrollingEnabled(false);
                LGRecycleViewAdapter<TeamDetailBean.Collect.ListDataBean> adapter1 =
                        new LGRecycleViewAdapter<TeamDetailBean.Collect.ListDataBean>(collect.listData) {
                            @Override
                            public int getLayoutId(int viewType) {
                                return R.layout.item_read_history;
                            }

                            @Override
                            public void convert(LGViewHolder holder, TeamDetailBean.Collect.ListDataBean listDataBean, int position) {
                                if (!StringUtils.isEmpty(listDataBean.icon) && !listDataBean.icon.startsWith("http")) {
                                    listDataBean.icon = HttpInterface.IMG_URL + listDataBean.icon;
                                }
                                holder.setImageUrl(TeamDetailActivity.this, R.id.teamImg, listDataBean.icon);
                                holder.setText(R.id.title, listDataBean.subject);
                                holder.setText(R.id.num, listDataBean.clickNum + "");
                                holder.setText(R.id.zhuanfa, listDataBean.forwardNum + "");
                            }
                        };
//                adapter1.setOnItemClickListener(R.id.item_layout, new ItemClickListener() {
//                    @Override
//                    public void onItemClicked(View view, int position) {
//                        Intent intent3 = new Intent(TeamDetailActivity.this, WebViewActivity.class);
//                        intent3.putExtra("url", HttpInterface.URL + LocalConfiguration.signUrl);
//                        startActivity(intent3);
//                    }
//                });
                recyclerView.setAdapter(adapter1);
            }
        };
        viewBinding.rvRight.setAdapter(adapter);
    }

}