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
public class PointsContract {
    public interface View extends BaseRequestView {

        void getDelData(ResponseBody body) throws JSONException, IOException;

        void getAddData(ResponseBody body) throws JSONException, IOException;
    }

    public  interface Presenter extends BasePresenter<View> {

        void getDelData(String token,int pageNum,int pageSize);

        void getAddData(String token,int pageNum,int pageSize);
    }
}
