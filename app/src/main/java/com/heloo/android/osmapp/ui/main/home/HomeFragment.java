package com.heloo.android.osmapp.ui.main.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.adapter.BaseFragmentAdapter;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FragmentHomeBinding;
import com.heloo.android.osmapp.model.SignBO;
import com.heloo.android.osmapp.model.TitleBean;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.HomeContract;
import com.heloo.android.osmapp.mvp.presenter.HomePresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.ui.search.SearchActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;
import com.heloo.android.osmapp.utils.ScreenUtils;
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
        implements HomeContract.View {

    private List<Fragment> mFragments;
    private SuggestFragment suggestFragment = new SuggestFragment();
    private BaseFragmentAdapter adapter;
    private List<String> titles = new ArrayList<>();
    private List<TitleBean> titleList = new ArrayList<>();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.getTitle(MyApplication.spUtils.getString("token", ""));
        sign();
    }


    private void initViews() {
        viewBinding.headLayout.post(() -> viewBinding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(getActivity()), 0, 0));
        mFragments = new ArrayList<>();
        mFragments.clear();
        mFragments.add(suggestFragment);
        for (int i = 0; i < titleList.size(); i++) {
            if (titleList.get(i).getName().equals("五美党建")) {
                mFragments.add(SuggestFragment.newInstance("五美党建", titleList.get(i).getCategoryId()));
            } else {
                mFragments.add(NewsFragment.newInstance(titleList.get(i).getCategoryId()));
            }
        }
        adapter = new BaseFragmentAdapter(getChildFragmentManager(), mFragments, titles);
        viewBinding.viewPager.setAdapter(adapter);
        viewBinding.viewPager.setOffscreenPageLimit(2);//设置预加载数量
        viewBinding.tabLayout.setupWithViewPager(viewBinding.viewPager);
        viewBinding.tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewBinding.tabLayout.setTabIndicatorFullWidth(false);

        viewBinding.searchBtn.setOnClickListener(v -> startActivity(new Intent(getActivity(), SearchActivity.class)));
        viewBinding.signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (goLogin()) {
                    Intent intent3 = new Intent(getActivity(), WebViewActivity.class);
                    if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                        intent3.putExtra("url", HttpInterface.URL + LocalConfiguration.signUrl
                                + "?uid=" + LocalConfiguration.userInfo.getUid() + "&username=" + LocalConfiguration.userInfo.getUsername());
                    } else {
                        intent3.putExtra("url", HttpInterface.URL + LocalConfiguration.signUrl);
                    }
                    startActivity(intent3);
                }
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
    public void getTitle(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")) {
            titleList.clear();
            titleList.addAll(JSON.parseArray(jsonObject.optString("data"), TitleBean.class));
            LocalConfiguration.titleBeanList.clear();
            LocalConfiguration.titleBeanList.addAll(titleList);
            titles.add("推荐");
            for (int i = 0; i < titleList.size(); i++) {
                titles.add(titleList.get(i).getName());
            }
            initViews();
        } else {
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            ToastUtils.showShortToast(jsonObject1.optString("errMsg"));
        }
    }


    public void sign() {
        HttpInterfaceIml.sign().subscribe(new HttpResultSubscriber<SignBO>() {
            @Override
            public void onSuccess(SignBO s) {
                if (s.isSourcess()) {  //签到成功
                    showDialog(s.date);
                }
            }

            @Override
            public void onFiled(String message) {

            }
        });
    }


    public void showDialog(String num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.AlertDialog);
        View view = getLayoutInflater().inflate(R.layout.dialog_sign_in, null);
        TextView signText = view.findViewById(R.id.jifen_num);
        signText.setText("+" + num);
        builder.setView(view);//设置login_layout为对话提示框
        builder.setCancelable(true);//设置为不可取消
        AlertDialog dialog = builder.create();
        dialog.show();//显示Dialog对话框
        //此处设置位置窗体大小
        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setDimAmount(0f);
        dialog.getWindow().setLayout(ScreenUtils.dp2px(180), ScreenUtils.dp2px(150));
    }
}