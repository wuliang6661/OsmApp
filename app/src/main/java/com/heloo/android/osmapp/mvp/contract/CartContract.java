package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.model.ShopCarBO;
import com.heloo.android.osmapp.model.UserInfo;
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


        void getCar(ShopCarBO carBO);

        void deleteShopSource();

        void getUserInner(UserInfo info);
    }

    public  interface Presenter extends BasePresenter<View> {

    }
}
