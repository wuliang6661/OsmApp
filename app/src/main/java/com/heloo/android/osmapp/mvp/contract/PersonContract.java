package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class PersonContract {
    public interface View extends BaseRequestView {

        void changeInfo(ResponseBody body) throws JSONException, IOException;

    }

    public  interface Presenter extends BasePresenter<View> {

        void changeInfo(String token,List<MultipartBody.Part> partList);

    }
}
