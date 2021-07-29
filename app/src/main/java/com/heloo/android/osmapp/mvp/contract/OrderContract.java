package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.model.OrderBO;
import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class OrderContract {
    public interface View extends BaseRequestView {

        void getOrder(List<OrderBO> orderBOS);

        void cancleSuress();

        void comfimOrder();

        void getOrderDetails(OrderBO orderBO);

    }

    public  interface Presenter extends BasePresenter<View> {


    }
}
