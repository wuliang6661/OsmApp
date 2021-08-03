package com.heloo.android.osmapp.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSON;
import com.heloo.android.osmapp.R;
import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.config.ConditionEnum;
import com.heloo.android.osmapp.config.LocalConfiguration;
import com.heloo.android.osmapp.model.UserInfo;
import com.heloo.android.osmapp.ui.main.MainActivity;
import com.heloo.android.osmapp.utils.MyAnimationDrawable;
import com.heloo.android.osmapp.utils.StringUtils;
import com.heloo.android.osmapp.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Witness on 2020-04-01
 * Describe:
 */
public class StartActivity extends BaseActivity {

    private String token;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_layout);
        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        token = MyApplication.spUtils.getString("token", "");

        MyAnimationDrawable.animateRawManuallyFromXML(R.drawable.animator,findViewById(R.id.image), () -> {

        }, () -> {
            if (StringUtils.isEmpty(token)) {   //未登陆过
//                Intent intent = new Intent(StartActivity.this,LoginActivity.class);
//                intent.putExtra("tag","1");
//                startActivity(intent);
                gotoActivity(MainActivity.class,true);
            } else {
                // 停止刷新
                new Handler().postDelayed(this::login, 0);
            }
        });
    }

    private void login(){
        HttpInterfaceIml.getUserInfo(token).subscribe(new Subscriber<ResponseBody>() {
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
                    if (status.equals("success")){
                        MyApplication.isLogin = ConditionEnum.LOGIN;
                        LocalConfiguration.userInfo = JSON.parseObject(jsonObject.optString("data"), UserInfo.class);
                        gotoActivity(MainActivity.class, true);
                    }else {
                        MyApplication.isLogin = ConditionEnum.NOLOGIN;
                        gotoActivity(MainActivity.class, true);
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
