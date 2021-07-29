package com.heloo.android.osmapp.mvp.presenter;

import com.heloo.android.osmapp.api.HttpInterfaceIml;
import com.heloo.android.osmapp.api.HttpResultSubscriber;
import com.heloo.android.osmapp.model.OrderBO;
import com.heloo.android.osmapp.mvp.BasePresenterImpl;
import com.heloo.android.osmapp.mvp.contract.OrderContract;

import java.util.List;

/**
 * Created by Witness on 3/30/21
 * Describe:
 */
public class OrderPresenter extends BasePresenterImpl<OrderContract.View>
        implements OrderContract.Presenter {


    public void getOrder(int pageNum, int type) {
        HttpInterfaceIml.getShopOrderItem(pageNum, type).subscribe(new HttpResultSubscriber<List<OrderBO>>() {
            @Override
            public void onSuccess(List<OrderBO> s) {
                if (mView != null) {
                    mView.getOrder(s);
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


    public void cancleOrder(String id) {
        HttpInterfaceIml.cancleOrder(id).subscribe(new HttpResultSubscriber<Object>() {
            @Override
            public void onSuccess(Object s) {
                if (mView != null) {
                    mView.cancleSuress();
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


    public void comfimOrder(String id){
        HttpInterfaceIml.comfimOrder(id).subscribe(new HttpResultSubscriber<Object>() {
            @Override
            public void onSuccess(Object s) {
                if (mView != null) {
                    mView.comfimOrder();
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
