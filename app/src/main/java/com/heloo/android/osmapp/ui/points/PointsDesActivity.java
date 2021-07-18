package com.heloo.android.osmapp.ui.points;

import android.os.Bundle;
import android.view.View;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.base.BaseActivity;
import com.heloo.android.osmapp.base.MyApplication;
import com.heloo.android.osmapp.databinding.ActivityPointsDesBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Description : 积分说明
 *
 * @author WITNESS
 * @date 6/17/21
 */

public class PointsDesActivity extends BaseActivity {

    private ActivityPointsDesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointsDesBinding.inflate(getLayoutInflater());
        View rootView = binding.getRoot();
        setContentView(rootView);
        goBack();
        setTitle("珍币说明");
        setHeader();
        showProgress("");
        getInfo();
    }

    private void getInfo(){
        HttpInterfaceIml.getDes(MyApplication.spUtils.getString("token", "")).subscribe(new Subscriber<ResponseBody>() {
            @Override
            public void onCompleted() {
                stopProgress();
            }

            @Override
            public void onError(Throwable e) {
               stopProgress();
            }

            @Override
            public void onNext(ResponseBody data) {
                try {
                    String s = new String(data.bytes());
                    JSONObject jsonObject = new JSONObject(s);
                    String status = jsonObject.optString("status");
                    if (status.equals("success")){
                        JSONObject jsonObject1 = new JSONObject(jsonObject.optString("data"));
                        binding.des.setText(jsonObject1.optString("response"));
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