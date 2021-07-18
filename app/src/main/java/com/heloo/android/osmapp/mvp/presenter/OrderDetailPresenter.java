package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.OrderDetailContract;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class OrderDetailPresenter extends BasePresenterImpl<OrderDetailContract.View>
        implements OrderDetailContract.Presenter{

    @Override
    public void addAddress(String distributorId, String name, String telephone, String province, String city, String area, String address, String timeStamp, String mac) {

    }
}
