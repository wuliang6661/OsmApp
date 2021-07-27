package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.model.ShopBannarBO;
import com.heloo.android.osmapp.model.ShopListBO;
import com.heloo.android.osmapp.mvp.BasePresenter;
import com.heloo.android.osmapp.mvp.BaseRequestView;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class StoreContract {
    public interface View extends BaseRequestView {

        void getClassify(ShopListBO body);

        void getBanner(ShopBannarBO body);

    }

    public  interface Presenter extends BasePresenter<View> {


        void getBanner();
    }
}
