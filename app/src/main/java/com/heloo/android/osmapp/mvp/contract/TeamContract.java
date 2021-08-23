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
public class TeamContract {
    public interface View extends BaseRequestView {

        void getData(ResponseBody body) throws JSONException, IOException;

    }

    public interface Presenter extends BasePresenter<View> {

        void getData(String deptId, String type);

    }
}
