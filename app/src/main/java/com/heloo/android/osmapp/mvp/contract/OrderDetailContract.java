package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.model.OrderBO;
import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class OrderDetailContract {
    public interface View extends BaseRequestView {

        void getOrderDetails(OrderBO orderBO);

    }

    public  interface Presenter extends BasePresenter<View> {


    }
}
