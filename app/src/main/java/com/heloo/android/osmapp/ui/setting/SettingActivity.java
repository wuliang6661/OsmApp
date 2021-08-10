package com.heloo.android.osmapp.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterface;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.databinding.ActivitySettingBinding;
import com.heloo.android.osmapp.ui.WebActivity;
import com.heloo.android.osmapp.ui.address.AddressActivity;
import com.heloo.android.osmapp.ui.password.ModifyActivity;
import com.heloo.android.osmapp.ui.person.PersonActivity;
import com.heloo.android.osmapp.utils.BubbleUtils;

import cn.jpush.android.api.JPushInterface;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySettingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        goBack();
        binding.headLayout.post(() -> binding.headLayout.setPadding(0, BubbleUtils.getStatusBarHeight(this), 0, 0));
        binding.addressBtn.setOnClickListener(this);
        binding.personInfoBtn.setOnClickListener(this);
        binding.changePasswordBtn.setOnClickListener(this);
        binding.registerBtn.setOnClickListener(this);
        binding.logoutBtn.setOnClickListener(this);
        binding.xieyi.setOnClickListener(view -> {
            Intent intent = new Intent(SettingActivity.this, WebActivity.class);
            intent.putExtra("url", HttpInterface.URL + LocalConfiguration.xieyiUrl);
            startActivity(intent);
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addressBtn://收货地址
                startActivity(new Intent(SettingActivity.this, AddressActivity.class));
                break;
            case R.id.personInfoBtn://个人信息
                startActivity(new Intent(SettingActivity.this, PersonActivity.class));
                break;
            case R.id.changePasswordBtn://修改密码
                startActivity(new Intent(SettingActivity.this, ModifyActivity.class));
                break;
            case R.id.registerBtn://新员工注册

                break;
            case R.id.logoutBtn://退出登录
                MyApplication.isLogin = ConditionEnum.NOLOGIN;
                LocalConfiguration.userInfo = null;
                MyApplication.spUtils.clear();
                JPushInterface.deleteAlias(this, 1);
                JPushInterface.cleanTags(this, 1);
                finish();
                break;
        }
    }
}