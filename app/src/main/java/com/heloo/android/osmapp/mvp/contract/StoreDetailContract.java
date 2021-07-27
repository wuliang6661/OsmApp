package com.heloo.android.osmapp.mvp.contract;

import com.heloo.android.osmapp.model.ShopDetailsBO;
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

        void getDetail(ShopDetailsBO body);

        void getAddCart(String body);

    }

    public  interface Presenter extends BasePresenter<View> {

        void getDetail(String id);

        void addCart(String goodId,String id,String num);
    }
}
