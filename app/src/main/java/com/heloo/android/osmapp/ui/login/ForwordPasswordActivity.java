package com.heloo.android.osmapp.ui.login;

import android.os.Bundle;
import android.view.View;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.databinding.ActivityForwordPasswordBinding;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;

import androidx.annotation.Nullable;

public class ForwordPasswordActivity extends BaseActivity {


    private ActivityForwordPasswordBinding binding;

    private String phone;
    private String yanzhengma;
    private String password;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForwordPasswordBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        setHeader();
        goBack();
        setTitle("忘记密码");
        binding.codeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = binding.accountInput.getText().toString().trim();
                if (StringUtils.isEmpty(phone)) {
                    ToastUtils.showShortToast("请输入电话号码！");
                    return;
                }
                getCode();
            }
        });
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phone = binding.accountInput.getText().toString().trim();
                if (StringUtils.isEmpty(phone)) {
                    ToastUtils.showShortToast("请输入电话号码！");
                    return;
                }
                yanzhengma = binding.passCodeInput.getText().toString().trim();
                if (StringUtils.isEmpty(yanzhengma)) {
                    ToastUtils.showShortToast("请输入验证码！");
                    return;
                }
                password = binding.setPasswordInput.getText().toString().trim();
                if (StringUtils.isEmpty(password)) {
                    ToastUtils.showShortToast("请输入密码！");
                    return;
                }
                resetPassword();
            }
        });
    }


    private void getCode() {
        HttpInterfaceIml.getOPTCode(phone, "resetPassword").subscribe(new HttpResultSubscriber<Object>() {
            @Override
            public void onSuccess(Object responseBody) {
                binding.codeBtn.setTextAfter("秒");
                binding.codeBtn.startTimer();
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShortToast(message);
            }
        });
    }


    private void resetPassword() {
        HttpInterfaceIml.resetPassword(yanzhengma, password, phone).subscribe(new HttpResultSubscriber<Object>() {
            @Override
            public void onSuccess(Object s) {
                ToastUtils.showShortToast("修改密码成功！");
                finish();
            }

            @Override
            public void onFiled(String message) {
                ToastUtils.showShortToast(message);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.codeBtn.onDestroy();
    }
}
