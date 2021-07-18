package com.heloo.android.osmapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.content.res.ResourcesCompat;

import com.alibaba.fastjson.JSON;
import com.google.android.material.tabs.TabLayout;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityLogin2Binding;
import com.heloo.android.osmapp.model.TokenBean;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.LoginContract;
import com.heloo.android.osmapp.mvp.presenter.LoginPresenter;
import com.heloo.android.osmapp.ui.main.MainActivity;
import com.heloo.android.osmapp.ui.password.ModifyActivity;
import com.heloo.android.osmapp.utils.ScreenUtils;
import com.heloo.android.osmapp.utils.ToastUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import okhttp3.ResponseBody;

/**
 * Description : 登录,注册
 *
 * @author WITNESS
 * @date 4/26/21
 */
public class LoginActivity extends MVPBaseActivity<LoginContract.View, LoginPresenter, ActivityLogin2Binding>
    implements LoginContract.View, View.OnClickListener {

    private int loginWay = 1;//1 手机快捷登录 2 账号密码登录 3 注册
    private boolean isAgree = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getStringExtra("tag") != null){
            coverBack();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
    }

    private void initView() {
        setTitle("登录");
        setHeader();
        goRegister();
        viewBinding.tabLayout.removeAllTabs();
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("手机快捷登录"));
        viewBinding.tabLayout.addTab(viewBinding.tabLayout.newTab().setText("账号密码登录"));
        viewBinding.codeBtn.setOnClickListener(this);
        viewBinding.forgetBtn.setOnClickListener(this);
        viewBinding.submitBtn.setOnClickListener(this);
        viewBinding.newPersonBtn.setOnClickListener(this);
        viewBinding.agreeBtn.setOnClickListener(this);
        viewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewBinding.passCodeInput.setText("");
                viewBinding.setPasswordInput.setText("");
                viewBinding.accountInput.setText("");
                if (tab.getPosition() == 0){//手机快捷登录
                    loginWay = 1;
                    viewBinding.accountImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_phone,null));
                    viewBinding.accountInput.setHint("输入手机号码");
                    viewBinding.passCodeImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_code,null));
                    viewBinding.passCodeInput.setHint("输入验证码");
                    viewBinding.passCodeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
                    viewBinding.passCodeInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    viewBinding.codeBtn.setVisibility(View.VISIBLE);
                    viewBinding.setPasswordLayout.setVisibility(View.GONE);
                    viewBinding.forgetBtn.setVisibility(View.GONE);
                }else {//账号密码登录
                    loginWay = 2;
                    viewBinding.accountImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_account,null));
                    viewBinding.accountInput.setHint("输入账号");
                    viewBinding.passCodeImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_password,null));
                    viewBinding.passCodeInput.setHint("输入密码");
                    viewBinding.passCodeInput.setTransformationMethod(PasswordTransformationMethod.getInstance()); //设置为密码输入框
                    viewBinding.codeBtn.setVisibility(View.GONE);
                    viewBinding.setPasswordLayout.setVisibility(View.GONE);
                    viewBinding.forgetBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        findViewById(R.id.registerBtn).setOnClickListener(v -> {
            loginWay = 3;
            setTitle("注册");
            findViewById(R.id.registerBtn).setVisibility(View.GONE);
            viewBinding.accountImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_phone,null));
            viewBinding.accountInput.setHint("输入手机号码");
            viewBinding.passCodeImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_code,null));
            viewBinding.passCodeInput.setHint("输入验证码");
            viewBinding.passCodeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            viewBinding.codeBtn.setVisibility(View.VISIBLE);
            viewBinding.setPasswordLayout.setVisibility(View.VISIBLE);
            viewBinding.forgetBtn.setVisibility(View.GONE);
            viewBinding.newPersonBtn.setVisibility(View.INVISIBLE);
            viewBinding.tabLayout.setVisibility(View.INVISIBLE);
            viewBinding.submitBtn.setText("立即注册");
            LinearLayout.LayoutParams lp = new
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(ScreenUtils.dp2px(22), ScreenUtils.dp2px(16), ScreenUtils.dp2px(22), ScreenUtils.dp2px(75));
            viewBinding.submitBtn.setLayoutParams(lp);
        });
        findViewById(R.id.backBtn).setOnClickListener(v -> back());
    }

    @Override
    public void onBackPressed() {
        back();
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
        switch (v.getId()){
            case R.id.codeBtn:
                if (TextUtils.isEmpty(viewBinding.accountInput.getText())){
                    ToastUtils.showShortToast("请输入手机号");
                    return;
                }
                if (viewBinding.accountInput.getText().length() != 11 || !viewBinding.accountInput.getText().toString().startsWith("1")){
                    ToastUtils.showShortToast("请输入正确的手机号");
                    return;
                }
                viewBinding.codeBtn.setTextAfter("秒");
                viewBinding.codeBtn.startTimer();
                getCode();
                break;
            case R.id.forgetBtn:
                startActivity(new Intent(LoginActivity.this, ModifyActivity.class));
                break;
            case R.id.submitBtn:
                submit();
                break;
            case R.id.newPersonBtn:

                break;
            case R.id.agreeBtn:
                if (isAgree){
                    viewBinding.agreeImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.agree_bg,null));
                    isAgree = false;
                }else {
                    viewBinding.agreeImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.agree_select_yes,null));
                    isAgree = true;
                }
                break;
        }
    }

    /**
     * 登录注册操作
     */
    private void submit() {
        switch (loginWay){
            case 1://1 手机快捷登录
                if (TextUtils.isEmpty(viewBinding.accountInput.getText())){
                    ToastUtils.showShortToast("请先输入手机号码");
                    return;
                }
                if (TextUtils.isEmpty(viewBinding.passCodeInput.getText())){
                    ToastUtils.showShortToast("请先输入验证码");
                    return;
                }
                if (!isAgree){
                    ToastUtils.showShortToast("请先阅读并同意《欧诗漫头条协议》");
                    return;
                }
                showProgress("");
                mPresenter.login("byOtpCode",viewBinding.accountInput.getText().toString(),viewBinding.passCodeInput.getText().toString(),"");
                break;
            case 2://2 账号密码登录
                if (TextUtils.isEmpty(viewBinding.accountInput.getText())){
                    ToastUtils.showShortToast("请先输入手机号码");
                    return;
                }
                if (TextUtils.isEmpty(viewBinding.passCodeInput.getText())){
                    ToastUtils.showShortToast("请先输入密码");
                    return;
                }
                if (!isAgree){
                    ToastUtils.showShortToast("请先阅读并同意《欧诗漫头条协议》");
                    return;
                }
                showProgress("");
                mPresenter.login("byPassword",viewBinding.accountInput.getText().toString(),"",viewBinding.passCodeInput.getText().toString());
                break;
            case 3://3 注册
                if (TextUtils.isEmpty(viewBinding.passCodeInput.getText())){
                    ToastUtils.showShortToast("请先输入验证码");
                    return;
                }
                if (TextUtils.isEmpty(viewBinding.setPasswordInput.getText())){
                    ToastUtils.showShortToast("请先输入密码");
                    return;
                }
                if (viewBinding.setPasswordInput.getText().toString().length() <= 6){
                    ToastUtils.showShortToast("密码过短，请完善密码");
                    return;
                }
                if (!isAgree){
                    ToastUtils.showShortToast("请先阅读并同意《欧诗漫头条协议》");
                    return;
                }
                showProgress("");
                mPresenter.register(viewBinding.accountInput.getText().toString(),
                        viewBinding.passCodeInput.getText().toString(),
                        viewBinding.setPasswordInput.getText().toString());
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        switch (loginWay){
            case 1://1 手机快捷登录
                mPresenter.getRegisterCode(viewBinding.accountInput.getText().toString(),"login");
                break;
            case 2://2 账号密码登录

                break;
            case 3://3 注册
                mPresenter.getRegisterCode(viewBinding.accountInput.getText().toString(),"register");
                break;
        }
    }

    /**
     * 返回
     */
    private void back(){
        if (loginWay == 3){
            loginWay = 1;
            setTitle("登录");
            findViewById(R.id.registerBtn).setVisibility(View.VISIBLE);
            viewBinding.tabLayout.getTabAt(0).select();
            viewBinding.accountImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_phone,null));
            viewBinding.accountInput.setHint("输入手机号码");
            viewBinding.passCodeImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.mipmap.login_code,null));
            viewBinding.passCodeInput.setHint("输入验证码");
            viewBinding.passCodeInput.setInputType(InputType.TYPE_CLASS_NUMBER);
            viewBinding.codeBtn.setVisibility(View.VISIBLE);
            viewBinding.setPasswordLayout.setVisibility(View.GONE);
            viewBinding.forgetBtn.setVisibility(View.GONE);
            viewBinding.newPersonBtn.setVisibility(View.INVISIBLE);
            viewBinding.tabLayout.setVisibility(View.VISIBLE);
            viewBinding.passCodeInput.setText("");
            viewBinding.setPasswordInput.setText("");
            viewBinding.agreeImg.setImageDrawable(ResourcesCompat.getDrawable(getResources(),R.drawable.agree_bg,null));
            isAgree = false;
            viewBinding.codeBtn.onDestroy();
            viewBinding.submitBtn.setText("立即登录");
            LinearLayout.LayoutParams lp = new
                    LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(ScreenUtils.dp2px(22), ScreenUtils.dp2px(10), ScreenUtils.dp2px(22), 0);
            viewBinding.submitBtn.setLayoutParams(lp);
        }else {
            finish();
        }
    }

    @Override
    public void getCode(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            ToastUtils.showShortToast("验证码发送成功");
        }else {
            ToastUtils.showShortToast("验证码发送失败，请稍后重试...");
        }
    }

    @Override
    public void register(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            ToastUtils.showShortToast("注册成功");
            back();
        }else {
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            ToastUtils.showShortToast(jsonObject1.optString("errMsg"));
        }
    }

    @Override
    public void login(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            LocalConfiguration.tokenBean = JSON.parseObject(jsonObject.optString("data"),TokenBean.class);
            MyApplication.spUtils.put("token",LocalConfiguration.tokenBean.getTokenHead()+LocalConfiguration.tokenBean.getToken());
            showProgress("");
            mPresenter.getUserInfo(LocalConfiguration.tokenBean.getTokenHead()+LocalConfiguration.tokenBean.getToken());
        }else {
            MyApplication.spUtils.clear();
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            ToastUtils.showShortToast(jsonObject1.optString("errMsg"));
        }
    }

    @Override
    public void getUserInfo(ResponseBody data) throws JSONException, IOException {
        String s = new String(data.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            MyApplication.isLogin = ConditionEnum.LOGIN;
            LocalConfiguration.userInfo = JSON.parseObject(jsonObject.optString("data"),UserInfo.class);
            //极光推送注册
            JPushInterface.setAlias(getApplicationContext(),111,LocalConfiguration.userInfo.getPhone());
            Set<String> tags = new HashSet<>();
            tags.add(LocalConfiguration.userInfo.getPhone());
            JPushInterface.setTags(getApplicationContext(),222,tags);
            if (getIntent().getStringExtra("tag") != null){
                gotoActivity(MainActivity.class,true);
            }else {
                finish();
            }
        }else {
            MyApplication.isLogin = ConditionEnum.NOLOGIN;
            JSONObject jsonObject1 = jsonObject.getJSONObject("data");
            ToastUtils.showShortToast(jsonObject1.optString("errMsg"));
        }
    }
}