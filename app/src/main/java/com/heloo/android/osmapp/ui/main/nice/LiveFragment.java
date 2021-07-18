package com.heloo.android.osmapp.ui.main.nice;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.heloo.android.osmapp.adapter.RecyclerAdapter;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.FragmentLiveBinding;
import com.heloo.android.osmapp.model.LivesBean;
import com.heloo.android.osmapp.model.RouteDataBO;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.LiveContract;
import com.heloo.android.osmapp.mvp.presenter.LivePresenter;
import com.heloo.android.osmapp.widget.StickyItemDecoration;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * 图文直播
 */
public class LiveFragment extends MVPBaseFragment<LiveContract.View, LivePresenter, FragmentLiveBinding>
    implements LiveContract.View{

    private RecyclerAdapter mAdapter;
    private int pageNo = 1;
    private int pageSize = 20;
    private LivesBean livesBean;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        mPresenter.getLives(MyApplication.spUtils.getString("token", ""),pageNo,pageSize);
    }

    private void initView() {
        viewBinding.nowList.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewBinding.nowList.addItemDecoration(new StickyItemDecoration());
        viewBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                pageNo = 1;
                mPresenter.getLives(MyApplication.spUtils.getString("token", ""),pageNo,pageSize);
                refreshLayout.finishRefresh(1000);
            }
        });

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
    public void getLives(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            livesBean = JSON.parseObject(jsonObject.optString("data"),LivesBean.class);
            viewBinding.nowList.setAdapter(mAdapter =new RecyclerAdapter(getActivity(), RouteDataBO.getDataList(livesBean)));
        }
    }
}