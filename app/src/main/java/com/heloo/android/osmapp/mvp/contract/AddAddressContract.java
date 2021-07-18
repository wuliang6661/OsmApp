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
public class AddAddressContract {
    public interface View extends BaseRequestView {

        void getAddResult(ResponseBody addResult) throws JSONException, IOException;

        void modifyAddress(ResponseBody body) throws JSONException, IOException;

    }

    public  interface Presenter extends BasePresenter<View> {

        /**
         * 添加地址
         * @param token
         * @param address
         * @param province
         * @param city
         * @param area
         * @param name
         * @param phone
         * @param postcode
         * @param status //默认地址 1为默认 2为非
         */
        void addAddress(String token,String address,String province,
                        String city,String area,String name,String phone,String postcode,String status);

        /**
         * 修改地址
         * @param token
         * @param address
         * @param province
         * @param city
         * @param area
         * @param name
         * @param phone
         * @param postcode
         * @param status //默认地址 1为默认 2为非
         */
        void modifyAddress(String token,String id,String address,String province,
                        String city,String area,String name,String phone,String postcode,String status);
    }
}
