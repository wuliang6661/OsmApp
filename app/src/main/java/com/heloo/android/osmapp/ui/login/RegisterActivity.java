package com.heloo.android.osmapp.ui.login;

import android.os.Bundle;
import android.view.View;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.databinding.ActivityRegisterBinding;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;

import androidx.annotation.Nullable;

public class RegisterActivity extends BaseActivity {


    private ActivityRegisterBinding binding;

    private String phone;
    private String yanzhengma;
    private String password;
    private String nikeName;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);

        setHeader();
        goBack();
        setTitle("注册");
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
                nikeName = binding.editNikeName.getText().toString().trim();
                if (StringUtils.isEmpty(nikeName)) {
                    ToastUtils.showShortToast("请输入昵称！");
                    return;
                }
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
                if (password.length() <= 6) {
                    ToastUtils.showShortToast("密码过短，请完善密码");
                    return;
                }
                if(!binding.agreeImg.isChecked()){
                    ToastUtils.showShortToast("请先阅读并同意《欧诗漫头条协议》");
                    return;
                }
                register(phone, yanzhengma, password, nikeName);
            }
        });
    }


    private void getCode() {
        HttpInterfaceIml.getOPTCode(phone, "register").subscribe(new HttpResultSubscriber<Object>() {
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


    public void register(String username, String otpCode, String password, String nikeName) {
        HttpInterfaceIml.register(username, otpCode, password, nikeName).subscribe(new HttpResultSubscriber<Object>() {

            @Override
            public void onSuccess(Object o) {
                ToastUtils.showShortToast("注册成功");
                finish();
            }

            @Override
            public void onFiled(String message) {

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.codeBtn.onDestroy();
    }
}
