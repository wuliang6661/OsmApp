package com.heloo.android.osmapp.ui.main.mine;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import android.view.View;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.FragmentMineBinding;
import com.heloo.android.osmapp.model.SignBean;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.mvp.MVPBaseFragment;
import com.heloo.android.osmapp.mvp.contract.MineContract;
import com.heloo.android.osmapp.mvp.presenter.MinePresenter;
import com.heloo.android.osmapp.ui.WebViewActivity;
import com.heloo.android.osmapp.ui.article.ArticleActivity;
import com.heloo.android.osmapp.ui.hero.HeroActivity;
import com.heloo.android.osmapp.ui.main.MainActivity;
import com.heloo.android.osmapp.ui.order.OrderActivity;
import com.heloo.android.osmapp.ui.person.PersonActivity;
import com.heloo.android.osmapp.ui.points.PointsActivity;
import com.heloo.android.osmapp.ui.setting.SettingActivity;
import com.heloo.android.osmapp.ui.team.TeamActivity;
import com.heloo.android.osmapp.utils.ToastUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Description : 我的
 *
 * @author WITNESS
 * @date 4/26/21
 */

public class MineFragment extends MVPBaseFragment<MineContract.View, MinePresenter, FragmentMineBinding>
        implements MineContract.View, View.OnClickListener, UMShareListener {

    private UserInfo userInfo;
    private SignBean signBean;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.isLogin == ConditionEnum.LOGIN
                && LocalConfiguration.userInfo != null) {
            getInfo();
        } else {
            viewBinding.name.setText("请登录");
            viewBinding.coinNum.setText("0");
            viewBinding.role.setVisibility(View.INVISIBLE);
            Glide.with(getActivity()).asBitmap().load(R.drawable.default_head).into(viewBinding.headerImg);
            mPresenter.getSignStatus(MyApplication.spUtils.getString("token", ""), "");
        }
    }

    /**
     * 设置个人信息
     */
    private void setUserInfo() {
        viewBinding.role.setVisibility(View.VISIBLE);
        Glide.with(getActivity()).asBitmap().load(userInfo.getIcon())
                .placeholder(R.drawable.default_head).error(R.drawable.default_head).into(viewBinding.headerImg);
        if (userInfo.getNickname() != null && !userInfo.getNickname().equals("")) {
            viewBinding.name.setText(userInfo.getNickname());
        } else {
            viewBinding.name.setText(userInfo.getUsername());
        }
        viewBinding.coinNum.setText(userInfo.getIntegration());
        viewBinding.myTeam.setVisibility(View.GONE);
        if (userInfo.getSourceType() == 1001) {
            viewBinding.role.setText("员工");
        } else if (userInfo.getSourceType() == 1003) {
            viewBinding.role.setText("管理员");
            viewBinding.myTeam.setVisibility(View.VISIBLE);
        } else {  //1002
            viewBinding.role.setText("会员");
        }
    }

    private void initView() {
        viewBinding.waitPay.setOnClickListener(this);
        viewBinding.havePaid.setOnClickListener(this);
        viewBinding.waitConfirm.setOnClickListener(this);
        viewBinding.finished.setOnClickListener(this);
        viewBinding.hotArticle.setOnClickListener(this);
        viewBinding.hero.setOnClickListener(this);
        viewBinding.myTeam.setOnClickListener(this);
        viewBinding.setting.setOnClickListener(this);
        viewBinding.headerImg.setOnClickListener(this);
        viewBinding.name.setOnClickListener(this);
        viewBinding.coinNum.setOnClickListener(this);
        viewBinding.scoreAndReward.setOnClickListener(this);
        viewBinding.signBtn.setOnClickListener(this);
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
    public void onClick(View v) {
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        switch (v.getId()) {
            case R.id.headerImg:
            case R.id.name:
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), PersonActivity.class));
                }
                break;
            case R.id.scoreAndReward:
                if (goLogin()) {
                    Intent intent2 = new Intent(getActivity(), WebViewActivity.class);
                    if (MyApplication.isLogin == ConditionEnum.LOGIN) {
                        intent2.putExtra("url", HttpInterface.URL + LocalConfiguration.rewordUrl
                                + "?uid=" + LocalConfiguration.userInfo.getUid() + "&username=" + LocalConfiguration.userInfo.getUsername());
                    } else {
                        intent2.putExtra("url", HttpInterface.URL + LocalConfiguration.rewordUrl);
                    }
                    startActivity(intent2);
                }
                break;
            case R.id.signBtn:
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
                break;
            case R.id.waitPay://待付款
                if (goLogin()) {
                    intent.putExtra("tag", "1");
                    startActivity(intent);
                }
                break;
            case R.id.havePaid://已付款
                if (goLogin()) {
                    intent.putExtra("tag", "2");
                    startActivity(intent);
                }
                break;
            case R.id.waitConfirm://待确认
                if (goLogin()) {
                    intent.putExtra("tag", "3");
                    startActivity(intent);
                }
                break;
            case R.id.finished://已完成
                if (goLogin()) {
                    intent.putExtra("tag", "4");
                    startActivity(intent);
                }
                break;
            case R.id.hotArticle://热门文章
                if(goLogin()){
                    startActivity(new Intent(getActivity(), ArticleActivity.class));
                }
                break;
            case R.id.hero://英雄榜
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), HeroActivity.class));
                }
                break;
            case R.id.myTeam://我的团队
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), TeamActivity.class));
                }
                break;
            case R.id.setting://设置
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), SettingActivity.class));
                }
//                ShareAction shareAction = new ShareAction(getActivity()).setPlatform(SHARE_MEDIA.WEIXIN);
//                shareAction.withSubject("测试").withText("测试").setCallback(this).share();
                break;
            case R.id.coinNum:
                if (goLogin()) {
                    startActivity(new Intent(getActivity(), PointsActivity.class));
                }
                break;
        }
    }

    @Override
    public void onStart(SHARE_MEDIA share_media) {

    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {

    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {

    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {

    }

    @Override
    public void getSignStatus(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")) {
            signBean = JSON.parseObject(jsonObject.optString("data"), SignBean.class);
            if (signBean != null) {
//                if (signBean.getTeamType().equals("1")) {
//                    viewBinding.myTeam.setVisibility(View.VISIBLE);
//                } else {
//                    viewBinding.myTeam.setVisibility(View.GONE);
//                }
                if (signBean.getSigninType().equals("0")) {//未签到
                    Glide.with(getActivity()).load(R.mipmap.sign_no).into(viewBinding.signImg);
                    viewBinding.signTxt.setText("签到有礼");
                } else {
                    Glide.with(getActivity()).load(R.mipmap.sign_yes).into(viewBinding.signImg);
                    viewBinding.signTxt.setText("今日已签");
                }
            }
        }
    }


    private void getInfo() {
        HttpInterfaceIml.getUserInfo(MyApplication.spUtils.getString("token", "")).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                ToastUtils.showShortToast(e.getMessage());
                MyApplication.isLogin = ConditionEnum.NOLOGIN;
                gotoActivity(MainActivity.class, true);
            }

            @Override
            public void onNext(ResponseBody data) {
                try {
                    String s = new String(data.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String status = jsonObject.optString("status");
                    if (status.equals("success")) {
                        MyApplication.isLogin = ConditionEnum.LOGIN;
                        LocalConfiguration.userInfo = JSON.parseObject(jsonObject.optString("data"), UserInfo.class);
                        userInfo = LocalConfiguration.userInfo;
                        setUserInfo();
                        mPresenter.getSignStatus(MyApplication.spUtils.getString("token", ""), LocalConfiguration.userInfo.getUsername());
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