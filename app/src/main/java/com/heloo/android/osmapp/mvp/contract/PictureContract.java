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
public class PictureContract {
    public interface View extends BaseRequestView {

        void getPictures(ResponseBody data) throws JSONException, IOException;

    }

    public  interface Presenter extends BasePresenter<View> {

        void getPictures(String token,int pageNum,int pageSize);

    }
}
