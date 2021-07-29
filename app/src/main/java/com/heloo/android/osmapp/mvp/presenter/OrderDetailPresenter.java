package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.model.OrderBO;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.OrderDetailContract;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class OrderDetailPresenter extends BasePresenterImpl<OrderDetailContract.View>
        implements OrderDetailContract.Presenter {

    public void getOrderDetails(String orderId) {
        HttpInterfaceIml.getorderDetail(orderId).subscribe(new HttpResultSubscriber<OrderBO>() {
            @Override
            public void onSuccess(OrderBO s) {
                if (mView != null) {
                    mView.getOrderDetails(s);
                }
            }

            @Override
            public void onFiled(String message) {
                if (mView != null) {
                    mView.onRequestError(message);
                }
            }
        });
    }

}
