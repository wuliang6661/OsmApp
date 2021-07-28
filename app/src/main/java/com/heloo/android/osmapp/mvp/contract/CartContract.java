package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.model.ShopCarBO;
import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class CartContract {
    public interface View extends BaseRequestView {

        void getAddResult(ResponseBody addResult) throws JSONException, IOException;

        void getCar(ShopCarBO carBO);

        void getCarNum(String num);

        void deleteShopSource();
    }

    public  interface Presenter extends BasePresenter<View> {

        void addAddress(String distributorId, String name, String telephone, String province, String city, String area, String address, String timeStamp, String mac);

    }
}
