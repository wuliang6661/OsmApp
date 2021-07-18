package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.OrderContract;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class OrderPresenter extends BasePresenterImpl<OrderContract.View>
        implements OrderContract.Presenter{

    @Override
    public void addAddress(String distributorId, String name, String telephone, String province, String city, String area, String address, String timeStamp, String mac) {

    }
}
