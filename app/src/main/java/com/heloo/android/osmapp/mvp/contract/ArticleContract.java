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
public class ArticleContract {
    public interface View extends BaseRequestView {

        void getData(ResponseBody body) throws JSONException, IOException;

    }

    public  interface Presenter extends BasePresenter<View> {

        /**
         *
         * @param token
         * @param type //1当日,2本周,3本月,4本年
         */
        void getData(String token, String type);
    }
}
