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
public class StoreDetailContract {
    public interface View extends BaseRequestView {

        void getDetail(ResponseBody body) throws JSONException, IOException;

        void getAddCart(ResponseBody body) throws JSONException, IOException;

    }

    public  interface Presenter extends BasePresenter<View> {

        void getDetail(String token,String id);

        void addCart(String token,String id,String num);
    }
}
