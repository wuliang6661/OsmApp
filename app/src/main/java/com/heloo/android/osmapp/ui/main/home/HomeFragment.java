package com.heloo.android.osmapp.ui.main.home;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.adapter.BaseFragmentAdapter;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FragmentHomeBinding;
import com.heloo.android.osmapp.model.TitleBean;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.HomeContract;
import com.heloo.android.osmapp.mvp.presenter.HomePresenter;
import com.heloo.android.osmapp.ui.search.SearchActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * 首页
 */
public class HomeFragment extends MVPBaseFragment<HomeContract.View, HomePresenter, FragmentHomeBinding>
        implements HomeContract.View{

    private List<Fragment> mFragments;
    private SuggestFragment suggestFragment = new SuggestFragment();
    private BaseFragmentAdapter adapter;
    private List<String> titles = new ArrayList<>();
    private List<TitleBean> titleList = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getTitle(MyApplication.spUtils.getString("token", ""));
    }

    private void initViews() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(getActivity()), 0, 0));
        mFragments = new ArrayList<>();
        mFragments.clear();
        mFragments.add(suggestFragment);
        for (int i=0;i<titleList.size();i++) {
            if (titleList.get(i).getName().equals("五美党建")){
                mFragments.add(SuggestFragment.newInstance("五美党建",titleList.get(i).getCategoryId()));
            }else {
                mFragments.add(NewsFragment.newInstance(titleList.get(i).getCategoryId()));
            }
        }
        adapter = new BaseFragmentAdapter(getChildFragmentManager(), mFragments, titles);
        viewBinding.viewPager.setAdapter(adapter);
        viewBinding.viewPager.setOffscreenPageLimit(2);//设置预加载数量
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager);
        viewBinding.tabLayout.setTabMode(TabLayout.MODE_AUTO);
        viewBinding.tabLayout.setTabIndicatorFullWidth(false);
        //设置第一个
        View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout,null);
        TextView name = view.findViewById(R.id.name);
        name.setText(viewBinding.tabLayout.getTabAt(0).getText());
        viewBinding.tabLayout.getTabAt(0).setCustomView(view);
        viewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.tab_item_layout,null);
                TextView name = view.findViewById(R.id.name);
                name.setText(tab.getText());
                tab.setCustomView(view);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.setCustomView(null);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewBinding.searchBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));
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
    public void getTitle(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            titleList.clear();
            titleList.addAll(JSON.parseArray(jsonObject.optString("data"),TitleBean.class));
            LocalConfiguration.titleBeanList.clear();
            LocalConfiguration.titleBeanList.addAll(titleList);
            titles.add("推荐");
            for (int i=0;i<titleList.size();i++){
                titles.add(titleList.get(i).getName());
            }
            initViews();
        }else {
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            ToastUtils.showShortToast(jsonObject1.optString("errMsg"));
        }
    }
}