package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class LoginContract {
    public interface View extends BaseRequestView {

        void getCode(ResponseBody data) throws JSONException, IOException;

        void login(ResponseBody data) throws JSONException, IOException;

        void getUserInfo(ResponseBody data) throws JSONException, IOException;
    }

    public  interface Presenter extends BasePresenter<View> {

        /**
         * 获取验证码类型，注册register,登录login,改密码resetPassword
         * @param telephone telephone
         * @param type 注册register,登录login,改密码resetPassword
         */
        void getRegisterCode(String telephone, String type);

        /**
         * 登录
         * @param loginType 密码byPassword/验证码byOtpCode
         * @param username username
         * @param otpCode 验证码
         * @param password 密码
         */
        void login(String loginType, String username,String otpCode,String password);

        /**
         * 获取用户信息
         * @param token token
         */
        void getUserInfo(String token);
    }
}
