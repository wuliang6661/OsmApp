package com.heloo.android.osmapp.ui.password;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivityModifyBinding;
import com.heloo.android.osmapp.mvp.MVPBaseActivity;
import com.heloo.android.osmapp.mvp.contract.ModifyContract;
import com.heloo.android.osmapp.mvp.presenter.ModifyPresenter;
import com.heloo.android.osmapp.ui.login.LoginActivity;
import com.heloo.android.osmapp.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import cn.jpush.android.api.JPushInterface;
import okhttp3.ResponseBody;

/**
 * Description : 修改密码
 *
 * @author WITNESS
 * @date 4/23/21
 */

public class ModifyActivity extends MVPBaseActivity<ModifyContract.View, ModifyPresenter, ActivityModifyBinding>
    implements ModifyContract.View, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    private void initView() {
        goBack();
        setTitle("修改密码");
        setHeader();
        viewBinding.submitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submitBtn:
                if (TextUtils.isEmpty(viewBinding.oldPassword.getText())){
                    ToastUtils.showShortToast("请输入原密码");
                    return;
                }
                if (TextUtils.isEmpty(viewBinding.newPassword.getText())){
                    ToastUtils.showShortToast("请输入新密码");
                    return;
                }
                if (TextUtils.isEmpty(viewBinding.confirmNewPassword.getText())){
                    ToastUtils.showShortToast("请确认新密码");
                    return;
                }
                if (!viewBinding.newPassword.getText().toString().equals(viewBinding.confirmNewPassword.getText().toString())){
                    ToastUtils.showShortToast("两次输入的密码不一致，请重新输入");
                    return;
                }
                showProgress("");
                mPresenter.modify(MyApplication.spUtils.getString("token", ""),
                        viewBinding.newPassword.getText().toString(),viewBinding.oldPassword.getText().toString());
                break;
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
    public void getResult(ResponseBody body) throws JSONException, IOException {
        String s = new String(body.bytes());
        JSONObject jsonObject = new JSONObject(s);
        String status = jsonObject.optString("status");
        if (status.equals("success")){
            ToastUtils.showShortToast("修改成功,请重新登录");
            MyApplication.isLogin = ConditionEnum.NOLOGIN;
            LocalConfiguration.userInfo = null;
            MyApplication.spUtils.clear();
            JPushInterface.deleteAlias(getApplicationContext(),111);
            gotoActivity(LoginActivity.class,true);
        }
    }
}